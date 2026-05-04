package com.qinghe.web.controller.edu;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qinghe.common.core.controller.BaseController;
import com.qinghe.common.core.domain.AjaxResult;
import com.qinghe.common.core.page.TableDataInfo;
import com.qinghe.system.domain.edu.EduAiLog;
import com.qinghe.system.service.edu.IEduAiLogService;

@RestController
@RequestMapping("/edu/aiLog")
public class EduAiLogController extends BaseController
{
    @Autowired
    private IEduAiLogService aiLogService;

    @PreAuthorize("@ss.hasPermi('edu:ai:list')")
    @GetMapping("/list")
    public TableDataInfo list(EduAiLog log)
    {
        startPage();
        List<EduAiLog> list = aiLogService.selectAiLogList(log);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('edu:ai:list')")
    @GetMapping("/myList")
    public TableDataInfo myList(EduAiLog log)
    {
        startPage();
        List<EduAiLog> list = aiLogService.selectCurrentUserLogs(log);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('edu:ai:list')")
    @GetMapping("/summary")
    public AjaxResult summary(EduAiLog log)
    {
        Map<String, Long> summary = aiLogService.getAiLogSummary(log);
        return AjaxResult.success(summary);
    }

    @PreAuthorize("@ss.hasPermi('edu:ai:list')")
    @GetMapping("/mySummary")
    public AjaxResult mySummary(EduAiLog log)
    {
        Map<String, Long> summary = aiLogService.getCurrentUserAiLogSummary(log);
        return AjaxResult.success(summary);
    }

    @PreAuthorize("@ss.hasRole('admin') or @ss.hasRole('edu_admin')")
    @DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(aiLogService.deleteAiLogByIds(logIds));
    }

    @PreAuthorize("@ss.hasPermi('edu:ai:list')")
    @DeleteMapping("/my/{logIds}")
    public AjaxResult removeMy(@PathVariable Long[] logIds)
    {
        return toAjax(aiLogService.deleteCurrentUserAiLogByIds(logIds));
    }
}
