package com.campus.scems.web.admin;

import com.campus.scems.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/faculty")
public class AdminDashboardController {

    private final StatisticsService statisticsService;

    public AdminDashboardController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Model model) {
        var byDept = statisticsService.registrationsByDepartment();
        var byType = statisticsService.registrationsByType();
        model.addAttribute("totalActiveEvents", statisticsService.totalActiveEvents());
        model.addAttribute("totalRegistrations", statisticsService.totalRegistrations());
        model.addAttribute("byDepartment", byDept);
        model.addAttribute("byType", byType);
        model.addAttribute("perEvent", statisticsService.registrationCountsPerEvent());
        model.addAttribute("maxDept", byDept.values().stream().mapToLong(Long::longValue).max().orElse(1L));
        model.addAttribute("maxType", byType.values().stream().mapToLong(Long::longValue).max().orElse(1L));
        return "faculty/dashboard";
    }
}
