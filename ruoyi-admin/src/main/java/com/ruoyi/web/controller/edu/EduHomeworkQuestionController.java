package com.ruoyi.web.controller.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.edu.EduHomeworkQuestion;
import com.ruoyi.system.service.edu.IEduHomeworkQuestionService;

@RestController
@RequestMapping("/edu/question")
public class EduHomeworkQuestionController extends BaseController
{
    @Autowired
    private IEduHomeworkQuestionService questionService;

    @PreAuthorize("@ss.hasPermi('edu:question:list')")
    @GetMapping("/list")
    public TableDataInfo list(EduHomeworkQuestion question)
    {
        startPage();
        List<EduHomeworkQuestion> list = questionService.selectQuestionList(question);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('edu:question:query')")
    @GetMapping("/{questionId}")
    public AjaxResult getInfo(@PathVariable Long questionId)
    {
        return success(questionService.selectQuestionById(questionId));
    }

    @PreAuthorize("@ss.hasPermi('edu:question:add')")
    @Log(title = "作业问题", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EduHomeworkQuestion question)
    {
        return toAjax(questionService.insertQuestion(question));
    }

    @PreAuthorize("@ss.hasPermi('edu:question:edit')")
    @PostMapping("/regenerate/{questionId}")
    public AjaxResult regenerate(@PathVariable Long questionId)
    {
        return success(questionService.regenerateAnswer(questionId));
    }

    @PreAuthorize("@ss.hasPermi('edu:question:remove')")
    @DeleteMapping("/{questionIds}")
    public AjaxResult remove(@PathVariable Long[] questionIds)
    {
        return toAjax(questionService.deleteQuestionByIds(questionIds));
    }
}
