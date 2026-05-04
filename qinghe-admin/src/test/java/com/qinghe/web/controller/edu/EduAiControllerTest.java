package com.qinghe.web.controller.edu;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.alibaba.fastjson2.JSONObject;
import com.qinghe.common.core.domain.AjaxResult;
import com.qinghe.common.core.domain.entity.SysUser;
import com.qinghe.common.core.domain.model.LoginUser;
import com.qinghe.common.exception.ServiceException;
import com.qinghe.system.config.EduAiProperties;
import com.qinghe.system.domain.edu.EduAiLog;
import com.qinghe.system.mapper.SysConfigMapper;
import com.qinghe.system.service.edu.IEduAiLogService;
import com.qinghe.system.service.edu.IEduAiService;
import com.qinghe.system.service.impl.edu.EduAiServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

class EduAiControllerTest
{
    private final EduAiController controller = new EduAiController();

    private final IEduAiService aiService = Mockito.mock(IEduAiService.class);

    @BeforeEach
    void setUp()
    {
        ReflectionTestUtils.setField(controller, "aiService", aiService);
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("student_demo");
        user.setPassword("123456");
        LoginUser loginUser = new LoginUser(1L, 100L, user, Set.of("edu:ai:list"));
        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken(loginUser, null));
    }

    @AfterEach
    void tearDown()
    {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRejectEmptyInterest()
    {
        EduAiController.OnlineResourceBody body = new EduAiController.OnlineResourceBody();
        body.setResources(Collections.singletonList(resource("Official resource", "Official", "General", "Resource", "https://example.com")));

        Assertions.assertThrows(ServiceException.class, () -> controller.onlineResourceRecommend(body));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldKeepAiEmptyRecommendationResultWithoutLocalFallback()
    {
        Mockito.when(aiService.generateOnlineResourceRecommendation(Mockito.eq(1L), Mockito.anyString()))
                .thenReturn("[]");

        EduAiController.OnlineResourceBody body = new EduAiController.OnlineResourceBody();
        body.setInterest("math thinking");
        body.setResources(List.of(
                resource("Math Thinking", "Bilibili", "Math", "Math thinking training", "https://example.com/math"),
                resource("English Speaking", "MOOC", "English", "English speaking", "https://example.com/english")));

        AjaxResult result = controller.onlineResourceRecommend(body);
        List<JSONObject> data = (List<JSONObject>) result.get(AjaxResult.DATA_TAG);

        Assertions.assertNotNull(data);
        Assertions.assertTrue(data.isEmpty());
        Mockito.verify(aiService, Mockito.times(1))
                .generateOnlineResourceRecommendation(Mockito.eq(1L), Mockito.anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldParseLinkLinesReturnedByAi()
    {
        Mockito.when(aiService.generateOnlineResourceRecommendation(Mockito.eq(1L), Mockito.anyString()))
                .thenReturn("- Math Animation https://example.com/video suitable for review");

        EduAiController.OnlineResourceBody body = new EduAiController.OnlineResourceBody();
        body.setInterest("math");
        body.setResources(Collections.singletonList(
                resource("Math Animation", "Bilibili", "Math", "Math video", "https://example.com/video")));

        AjaxResult result = controller.onlineResourceRecommend(body);
        List<JSONObject> data = (List<JSONObject>) result.get(AjaxResult.DATA_TAG);

        Assertions.assertEquals(1, data.size());
        Assertions.assertEquals("Math Animation", data.get(0).getString("title"));
        Assertions.assertEquals("https://example.com/video", data.get(0).getString("link"));
    }

    @Test
    void shouldFailInsteadOfMockingWhenAiConfigIsIncomplete()
    {
        EduAiServiceImpl realService = new EduAiServiceImpl();
        EduAiProperties properties = new EduAiProperties();
        properties.setEnabled(true);
        properties.setEndpoint("https://example.com/v1/chat/completions");
        properties.setApiKey("");
        properties.setModel("Qwen/Qwen2.5-7B-Instruct");
        properties.setMaxPromptLength(1200);
        properties.setTimeoutSeconds(1);

        IEduAiLogService logService = Mockito.mock(IEduAiLogService.class);
        SysConfigMapper configMapper = Mockito.mock(SysConfigMapper.class);
        ReflectionTestUtils.setField(realService, "aiProperties", properties);
        ReflectionTestUtils.setField(realService, "aiLogService", logService);
        ReflectionTestUtils.setField(realService, "sysConfigMapper", configMapper);

        ServiceException error = Assertions.assertThrows(ServiceException.class,
                () -> realService.answerHomeworkQuestion(100L, "question"));
        Assertions.assertTrue(error.getMessage().contains("AI"));

        ArgumentCaptor<EduAiLog> captor = ArgumentCaptor.forClass(EduAiLog.class);
        Mockito.verify(logService, Mockito.times(1)).insertAiLog(captor.capture());
        Assertions.assertEquals("failed", captor.getValue().getStatus());
        Assertions.assertEquals("review", captor.getValue().getRiskFlag());
    }

    private EduAiController.OnlineResourceItem resource(String title, String source, String category, String description, String link)
    {
        EduAiController.OnlineResourceItem item = new EduAiController.OnlineResourceItem();
        item.setTitle(title);
        item.setSource(source);
        item.setCategory(category);
        item.setDescription(description);
        item.setLink(link);
        return item;
    }
}
