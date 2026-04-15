package com.eapple.web.controller.edu;

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
import org.springframework.web.bind.annotation.RestController;
import com.eapple.common.annotation.Log;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.page.TableDataInfo;
import com.eapple.common.enums.BusinessType;
import com.eapple.system.domain.edu.EduStudentProfile;
import com.eapple.system.service.edu.IEduStudentProfileService;

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
    @Log(title = "瀛︾敓妗ｆ", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EduStudentProfile profile)
    {
        return toAjax(profileService.insertProfile(profile));
    }

    @PreAuthorize("@ss.hasPermi('edu:student:edit')")
    @Log(title = "瀛︾敓妗ｆ", businessType = BusinessType.UPDATE)
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
