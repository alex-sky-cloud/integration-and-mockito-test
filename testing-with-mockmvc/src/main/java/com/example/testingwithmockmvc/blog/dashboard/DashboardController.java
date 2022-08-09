package com.example.testingwithmockmvc.blog.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public String getDashboardView(Model model) {

        model
                .addAttribute("user", "Duke");

        Integer[] analyticsGraphData = dashboardService.getAnalyticsGraphData();
        model
                .addAttribute("analyticsGraph", analyticsGraphData);

        model.addAttribute("quickNote", new QuickNote());

        return "dashboard";
    }
}
