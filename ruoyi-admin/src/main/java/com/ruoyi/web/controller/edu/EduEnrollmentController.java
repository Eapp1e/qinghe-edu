package com.ruoyi.web.controller.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.edu.EduCourseEnrollment;
import com.ruoyi.system.service.edu.IEduEnrollmentService;

@RestController
@RequestMapping("/edu/enrollment")
public class EduEnrollmentController extends BaseController
{
    @Autowired
    private IEduEnrollmentService enrollmentService;

    @PreAuthorize("@ss.hasPermi('edu:enrollment:list')")
    @GetMapping("/list")
    public TableDataInfo list(EduCourseEnrollment enrollment)
    {
        startPage();
        List<EduCourseEnrollment> list = enrollmentService.selectEnrollmentList(enrollment);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('edu:enrollment:edit')")
    @Log(title = "课程报名", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EduCourseEnrollment enrollment)
    {
        return toAjax(enrollmentService.updateEnrollment(enrollment));
    }
}
