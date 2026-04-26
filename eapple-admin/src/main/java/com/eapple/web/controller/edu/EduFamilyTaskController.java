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
import com.eapple.system.domain.edu.EduFamilyTask;
import com.eapple.system.service.edu.IEduFamilyTaskService;

@RestController
@RequestMapping("/edu/familyTask")
public class EduFamilyTaskController extends BaseController
{
    @Autowired
    private IEduFamilyTaskService taskService;

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin') or @ss.hasRole('edu_parent') or @ss.hasRole('edu_student')")
    @GetMapping("/list")
    public TableDataInfo list(EduFamilyTask task)
    {
        startPage();
        List<EduFamilyTask> list = taskService.selectTaskList(task);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin') or @ss.hasRole('edu_parent') or @ss.hasRole('edu_student')")
    @GetMapping("/summary")
    public AjaxResult summary()
    {
        return success(taskService.getTaskSummary());
    }

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin') or @ss.hasRole('edu_parent') or @ss.hasRole('edu_student')")
    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable Long taskId)
    {
        return success(taskService.selectTaskById(taskId));
    }

    @PreAuthorize("@ss.hasRole('edu_parent')")
    @Log(title = "家庭任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EduFamilyTask task)
    {
        return toAjax(taskService.insertTask(task));
    }

    @PreAuthorize("@ss.hasRole('edu_parent')")
    @Log(title = "家庭任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody EduFamilyTask task)
    {
        return toAjax(taskService.updateTask(task));
    }

    @PreAuthorize("@ss.hasRole('edu_student')")
    @Log(title = "家庭任务提交", businessType = BusinessType.UPDATE)
    @PutMapping("/submit")
    public AjaxResult submit(@RequestBody EduFamilyTask task)
    {
        return toAjax(taskService.submitTask(task));
    }

    @PreAuthorize("@ss.hasRole('edu_parent')")
    @Log(title = "家庭任务确认", businessType = BusinessType.UPDATE)
    @PutMapping("/review")
    public AjaxResult review(@RequestBody EduFamilyTask task)
    {
        return toAjax(taskService.reviewTask(task));
    }

    @PreAuthorize("@ss.hasRole('edu_parent')")
    @DeleteMapping("/{taskIds}")
    public AjaxResult remove(@PathVariable Long[] taskIds)
    {
        return toAjax(taskService.deleteTaskByIds(taskIds));
    }
}
