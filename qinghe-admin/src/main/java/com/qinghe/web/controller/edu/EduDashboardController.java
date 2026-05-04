package com.qinghe.web.controller.edu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.qinghe.common.core.controller.BaseController;
import com.qinghe.common.core.domain.AjaxResult;
import com.qinghe.system.service.edu.IEduDashboardService;

@RestController
@RequestMapping("/edu/dashboard")
public class EduDashboardController extends BaseController
{
    @Autowired
    private IEduDashboardService dashboardService;

    @PreAuthorize("@ss.hasPermi('edu:dashboard:view')")
    @GetMapping
    public AjaxResult getDashboard()
    {
        return success(dashboardService.getDashboard());
    }
}
