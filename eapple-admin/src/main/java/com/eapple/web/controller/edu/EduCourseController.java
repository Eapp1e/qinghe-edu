package com.eapple.web.controller.edu;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
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
import com.eapple.common.annotation.Log;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.page.TableDataInfo;
import com.eapple.common.enums.BusinessType;
import com.eapple.common.utils.poi.ExcelUtil;
import com.eapple.system.domain.edu.EduCourse;
import com.eapple.system.service.edu.IEduCourseService;

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
    @Log(title = "课程中心", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EduCourse course)
    {
        List<EduCourse> list = courseService.selectCourseList(course);
        ExcelUtil<EduCourse> util = new ExcelUtil<EduCourse>(EduCourse.class);
        util.exportExcel(response, list, "课程中心数据");
    }

    @PreAuthorize("@ss.hasPermi('edu:course:list')")
    @GetMapping("/myList")
    public AjaxResult myList()
    {
        return success(courseService.selectMyCourseList());
    }

    @PreAuthorize("@ss.hasPermi('edu:course:list')")
    @GetMapping("/recommend")
    public AjaxResult recommend(@RequestParam(required = false) Long studentUserId)
    {
        return success(courseService.recommendCoursesByStudent(studentUserId));
    }

    @PreAuthorize("@ss.hasPermi('edu:course:query')")
    @GetMapping("/{courseId}")
    public AjaxResult getInfo(@PathVariable Long courseId)
    {
        return success(courseService.selectCourseById(courseId));
    }

    @PreAuthorize("@ss.hasPermi('edu:course:add') or @ss.hasRole('edu_teacher')")
    @Log(title = "课后课程", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EduCourse course)
    {
        return toAjax(courseService.insertCourse(course));
    }

    @PreAuthorize("@ss.hasPermi('edu:course:edit') or @ss.hasRole('edu_teacher')")
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

    @PreAuthorize("@ss.hasPermi('edu:course:enroll')")
    @PostMapping("/cancelEnroll/{courseId}")
    public AjaxResult cancelEnroll(@PathVariable Long courseId, @RequestParam(required = false) Long studentUserId)
    {
        return toAjax(courseService.cancelEnrollment(courseId, studentUserId));
    }

    @PreAuthorize("@ss.hasPermi('edu:course:edit') or @ss.hasRole('edu_teacher')")
    @PostMapping("/notice/{courseId}")
    public AjaxResult generateNotice(@PathVariable Long courseId)
    {
        return success(courseService.generateCourseNotice(courseId));
    }

    @PreAuthorize("@ss.hasPermi('edu:course:edit') or @ss.hasRole('edu_teacher')")
    @PostMapping("/suggestion/{courseId}")
    public AjaxResult generateSuggestion(@PathVariable Long courseId)
    {
        return success(courseService.generateTeachingSuggestion(courseId));
    }
}
