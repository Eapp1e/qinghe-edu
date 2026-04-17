package com.eapple.web.controller.edu;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.eapple.common.core.controller.BaseController;
import com.eapple.common.core.domain.AjaxResult;
import com.eapple.common.core.page.TableDataInfo;
import com.eapple.system.domain.edu.EduAiLog;
import com.eapple.system.service.edu.IEduAiLogService;

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
}
