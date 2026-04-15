package com.eapple.web.controller.system;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eapple.common.annotation.Log;
import com.eapple.common.constant.UserConstants;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.domain.entity.SysDept;
import com.eapple.common.enums.BusinessType;
import com.eapple.common.utils.StringUtils;
import com.eapple.system.service.ISysDeptService;

/**
 * 閮ㄩ棬淇℃伅
 * 
 * @author Eapp1e
 */
@RestController
@RequestMapping("/system/dept")
public class SysDeptController extends BaseController
{
    @Autowired
    private ISysDeptService deptService;

    /**
     * 鑾峰彇閮ㄩ棬鍒楄〃
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public AjaxResult list(SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return success(depts);
    }

    /**
     * 鏌ヨ閮ㄩ棬鍒楄〃锛堟帓闄よ妭鐐癸級
     */
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list/exclude/{deptId}")
    public AjaxResult excludeChild(@PathVariable(value = "deptId", required = false) Long deptId)
    {
        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        depts.removeIf(d -> d.getDeptId().intValue() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""));
        return success(depts);
    }

    /**
     * 鏍规嵁閮ㄩ棬缂栧彿鑾峰彇璇︾粏淇℃伅
     */
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping(value = "/{deptId}")
    public AjaxResult getInfo(@PathVariable Long deptId)
    {
        deptService.checkDeptDataScope(deptId);
        return success(deptService.selectDeptById(deptId));
    }

    /**
     * 鏂板閮ㄩ棬
     */
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @Log(title = "閮ㄩ棬绠＄悊", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysDept dept)
    {
        if (!deptService.checkDeptNameUnique(dept))
        {
            return error("鏂板閮ㄩ棬'" + dept.getDeptName() + "'澶辫触锛岄儴闂ㄥ悕绉板凡瀛樺湪");
        }
        dept.setCreateBy(getUsername());
        return toAjax(deptService.insertDept(dept));
    }

    /**
     * 淇敼閮ㄩ棬
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "閮ㄩ棬绠＄悊", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysDept dept)
    {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (!deptService.checkDeptNameUnique(dept))
        {
            return error("淇敼閮ㄩ棬'" + dept.getDeptName() + "'澶辫触锛岄儴闂ㄥ悕绉板凡瀛樺湪");
        }
        else if (dept.getParentId().equals(deptId))
        {
            return error("淇敼閮ㄩ棬'" + dept.getDeptName() + "'澶辫触锛屼笂绾ч儴闂ㄤ笉鑳芥槸鑷繁");
        }
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0)
        {
            return error("璇ラ儴闂ㄥ寘鍚湭鍋滅敤鐨勫瓙閮ㄩ棬锛?);
        }
        dept.setUpdateBy(getUsername());
        return toAjax(deptService.updateDept(dept));
    }

    /**
     * 淇濆瓨閮ㄩ棬鎺掑簭
     */
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @Log(title = "淇濆瓨閮ㄩ棬鎺掑簭", businessType = BusinessType.UPDATE)
    @PutMapping("/updateSort")
    public AjaxResult updateSort(@RequestBody Map<String, String> params)
    {
        String[] deptIds = params.get("deptIds").split(",");
        String[] orderNums = params.get("orderNums").split(",");
        deptService.updateDeptSort(deptIds, orderNums);
        return success();
    }

    /**
     * 鍒犻櫎閮ㄩ棬
     */
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @Log(title = "閮ㄩ棬绠＄悊", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public AjaxResult remove(@PathVariable Long deptId)
    {
        if (deptService.hasChildByDeptId(deptId))
        {
            return warn("瀛樺湪涓嬬骇閮ㄩ棬,涓嶅厑璁稿垹闄?);
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            return warn("閮ㄩ棬瀛樺湪鐢ㄦ埛,涓嶅厑璁稿垹闄?);
        }
        deptService.checkDeptDataScope(deptId);
        return toAjax(deptService.deleteDeptById(deptId));
    }
}
