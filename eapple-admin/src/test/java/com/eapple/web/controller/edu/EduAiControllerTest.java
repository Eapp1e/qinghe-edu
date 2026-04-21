package com.eapple.web.controller.edu;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.alibaba.fastjson2.JSONObject;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.domain.entity.SysUser;
import com.eapple.common.core.domain.model.LoginUser;
import com.eapple.common.exception.ServiceException;
import com.eapple.system.service.edu.IEduAiService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        body.setResources(Collections.singletonList(resource("国家中小学智慧教育平台", "官方平台", "综合", "国家平台", "https://example.com")));

        Assertions.assertThrows(ServiceException.class, () -> controller.onlineResourceRecommend(body));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldFallbackToLocalRecommendationsWhenAiReturnsEmptyArray()
    {
        Mockito.when(aiService.generateOnlineResourceRecommendation(Mockito.eq(1L), Mockito.anyString()))
                .thenReturn("[]");

        EduAiController.OnlineResourceBody body = new EduAiController.OnlineResourceBody();
        body.setInterest("数学 思维");
        body.setResources(List.of(
                resource("小学数学思维课", "Bilibili", "数学", "适合中小学生拓展思维训练", "https://example.com/math"),
                resource("英语口语趣学", "中国大学MOOC", "英语", "适合日常英语启蒙", "https://example.com/english")));

        AjaxResult result = controller.onlineResourceRecommend(body);
        List<JSONObject> data = (List<JSONObject>) result.get(AjaxResult.DATA_TAG);

        Assertions.assertNotNull(data);
        Assertions.assertFalse(data.isEmpty());
        Assertions.assertEquals("小学数学思维课", data.get(0).getString("title"));
        Assertions.assertTrue(data.get(0).getString("reason").contains("数学 思维"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldParseLinkLinesReturnedByAi()
    {
        Mockito.when(aiService.generateOnlineResourceRecommendation(Mockito.eq(1L), Mockito.anyString()))
                .thenReturn("- 数学动画课堂 https://example.com/video 适合课后巩固");

        EduAiController.OnlineResourceBody body = new EduAiController.OnlineResourceBody();
        body.setInterest("数学");
        body.setResources(Collections.singletonList(
                resource("数学动画课堂", "Bilibili", "数学", "趣味数学视频", "https://example.com/video")));

        AjaxResult result = controller.onlineResourceRecommend(body);
        List<JSONObject> data = (List<JSONObject>) result.get(AjaxResult.DATA_TAG);

        Assertions.assertEquals(1, data.size());
        Assertions.assertEquals("数学动画课堂", data.get(0).getString("title"));
        Assertions.assertEquals("https://example.com/video", data.get(0).getString("link"));
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
