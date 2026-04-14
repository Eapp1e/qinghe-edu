package com.ruoyi.web.controller.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.edu.EduCourse;
import com.ruoyi.system.service.edu.IEduCourseService;

@RestController
@RequestMapping("/edu/course")
public class EduCourseController extends BaseController
{
    @Autowired
    private IEduCourseService courseService;

    @PreAuthorize("@ss.hasPermi('edu:course:list')")
    @GetMapping("/list")
    public TableDataInfo list(EduCourse course)
    {
        startPage();
        List<EduCourse> list = courseService.selectCourseList(course);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('edu:course:list')")
    @GetMapping("/myList")
    public AjaxResult myList()
    {
        return success(courseService.selectMyCourseList());
    }

    @PreAuthorize("@ss.hasPermi('edu:course:query')")
    @GetMapping("/{courseId}")
    public AjaxResult getInfo(@PathVariable Long courseId)
    {
        return success(courseService.selectCourseById(courseId));
    }

    @PreAuthorize("@ss.hasPermi('edu:course:add')")
    @Log(title = "课后课程", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EduCourse course)
    {
        return toAjax(courseService.insertCourse(course));
    }

    @PreAuthorize("@ss.hasPermi('edu:course:edit')")
    @Log(title = "课后课程", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EduCourse course)
    {
        return toAjax(courseService.updateCourse(course));
    }

    @PreAuthorize("@ss.hasPermi('edu:course:remove')")
    @Log(title = "课后课程", businessType = BusinessType.DELETE)
    @DeleteMapping("/{courseIds}")
    public AjaxResult remove(@PathVariable Long[] courseIds)
    {
        return toAjax(courseService.deleteCourseByIds(courseIds));
    }

    @PreAuthorize("@ss.hasPermi('edu:course:enroll')")
    @PostMapping("/enroll/{courseId}")
    public AjaxResult enroll(@PathVariable Long courseId, @RequestParam(required = false) Long studentUserId)
    {
        return toAjax(courseService.enrollCourse(courseId, studentUserId));
    }

    @PreAuthorize("@ss.hasPermi('edu:course:edit')")
    @PostMapping("/notice/{courseId}")
    public AjaxResult generateNotice(@PathVariable Long courseId)
    {
        return success(courseService.generateCourseNotice(courseId));
    }

    @PreAuthorize("@ss.hasPermi('edu:course:edit')")
    @PostMapping("/suggestion/{courseId}")
    public AjaxResult generateSuggestion(@PathVariable Long courseId)
    {
        return success(courseService.generateTeachingSuggestion(courseId));
    }
}
