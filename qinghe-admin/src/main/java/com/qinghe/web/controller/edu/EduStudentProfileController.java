package com.qinghe.web.controller.edu;

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
import com.qinghe.common.annotation.Log;
import com.qinghe.common.core.controller.BaseController;
import com.qinghe.common.core.domain.AjaxResult;
import com.qinghe.common.core.page.TableDataInfo;
import com.qinghe.common.enums.BusinessType;
import com.qinghe.common.utils.poi.ExcelUtil;
import com.qinghe.system.domain.edu.EduStudentProfile;
import com.qinghe.system.service.edu.IEduStudentProfileService;

@RestController
@RequestMapping("/edu/student")
public class EduStudentProfileController extends BaseController
{
    @Autowired
    private IEduStudentProfileService profileService;

    @PreAuthorize("@ss.hasPermi('edu:student:list')")
    @GetMapping("/list")
    public TableDataInfo list(EduStudentProfile profile)
    {
        startPage();
        List<EduStudentProfile> list = profileService.selectProfileList(profile);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('edu:student:list')")
    @Log(title = "学生档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EduStudentProfile profile)
    {
        List<EduStudentProfile> list = profileService.selectProfileList(profile);
        ExcelUtil<EduStudentProfile> util = new ExcelUtil<EduStudentProfile>(EduStudentProfile.class);
        util.exportExcel(response, list, "学生档案数据");
    }

    @PreAuthorize("@ss.hasPermi('edu:student:list')")
    @GetMapping("/myChildren")
    public AjaxResult myChildren()
    {
        return success(profileService.selectCurrentUserChildren());
    }

    @PreAuthorize("@ss.hasPermi('edu:student:query')")
    @GetMapping("/{profileId}")
    public AjaxResult getInfo(@PathVariable Long profileId)
    {
        return success(profileService.selectProfileById(profileId));
    }

    @PreAuthorize("@ss.hasPermi('edu:student:add')")
    @GetMapping("/validate/student")
    public AjaxResult validateStudent(@RequestParam Long studentUserId)
    {
        return success(profileService.validateManagedStudent(studentUserId));
    }

    @PreAuthorize("@ss.hasPermi('edu:student:add')")
    @GetMapping("/validate/parent")
    public AjaxResult validateParent(@RequestParam Long parentUserId)
    {
        return success(profileService.validateManagedParent(parentUserId));
    }

    @PreAuthorize("@ss.hasPermi('edu:student:add') or @ss.hasRole('edu_student')")
    @Log(title = "学生档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EduStudentProfile profile)
    {
        return toAjax(profileService.insertProfile(profile));
    }

    @PreAuthorize("@ss.hasPermi('edu:student:edit') or @ss.hasRole('edu_student')")
    @Log(title = "学生档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EduStudentProfile profile)
    {
        return toAjax(profileService.updateProfile(profile));
    }

    @PreAuthorize("@ss.hasPermi('edu:student:remove')")
    @DeleteMapping("/{profileIds}")
    public AjaxResult remove(@PathVariable Long[] profileIds)
    {
        return toAjax(profileService.deleteProfileByIds(profileIds));
    }
}
