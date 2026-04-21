package com.eapple.web.controller.edu;

import java.util.List;
import com.eapple.common.annotation.Log;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.page.TableDataInfo;
import com.eapple.common.enums.BusinessType;
import com.eapple.common.utils.poi.ExcelUtil;
import com.eapple.system.domain.edu.EduHomeworkQuestion;
import com.eapple.system.service.edu.IEduHomeworkQuestionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PreAuthorize("@ss.hasPermi('edu:question:list') or @ss.hasAnyRoles('admin,edu_admin,edu_teacher')")
    @Log(title = "作业问答", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EduHomeworkQuestion question)
    {
        List<EduHomeworkQuestion> list = questionService.selectQuestionList(question);
        ExcelUtil<EduHomeworkQuestion> util = new ExcelUtil<>(EduHomeworkQuestion.class);
        util.exportExcel(response, list, "作业问答数据");
    }

    @PreAuthorize("@ss.hasPermi('edu:question:query') or @ss.hasAnyRoles('admin,edu_admin,edu_teacher,edu_parent,edu_student')")
    @GetMapping("/{questionId}")
    public AjaxResult getInfo(@PathVariable Long questionId)
    {
        return success(questionService.selectQuestionById(questionId));
    }

    @PreAuthorize("@ss.hasPermi('edu:question:add')")
    @Log(title = "作业问答", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EduHomeworkQuestion question)
    {
        return toAjax(questionService.insertQuestion(question));
    }

    @PreAuthorize("@ss.hasPermi('edu:question:edit') or @ss.hasAnyRoles('admin,edu_admin,edu_teacher')")
    @Log(title = "作业问答", businessType = BusinessType.UPDATE)
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
