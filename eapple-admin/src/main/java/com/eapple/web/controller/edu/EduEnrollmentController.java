package com.eapple.web.controller.edu;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eapple.common.annotation.Log;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.page.TableDataInfo;
import com.eapple.common.enums.BusinessType;
import com.eapple.system.domain.edu.EduCourseEnrollment;
import com.eapple.system.service.edu.IEduEnrollmentService;

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

    @PreAuthorize("@ss.hasPermi('edu:enrollment:edit') or @ss.hasRole('edu_parent')")
    @Log(title = "课程报名", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EduCourseEnrollment enrollment)
    {
        return toAjax(enrollmentService.updateEnrollment(enrollment));
    }
}
