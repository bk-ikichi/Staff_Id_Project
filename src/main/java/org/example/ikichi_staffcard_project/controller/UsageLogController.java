package org.example.ikichi_staffcard_project.controller;

import org.example.ikichi_staffcard_project.dto.UsageResponse;
import org.example.ikichi_staffcard_project.service.UsageLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/usage")
public class UsageLogController {

    private final UsageLogService usageLogService;

    public UsageLogController(UsageLogService usageLogService) {
        this.usageLogService = usageLogService;
    }

    /**
     * 利用ログ一覧画面を表示
     */
    @GetMapping
    public String showUsageLogPage(Model model) {
        // 💡 戻り値の型を UsageResponse に修正してエラーを解消
        List<UsageResponse> logs = usageLogService.getAllLogs();

        // Thymeleaf側に「logs」という名前で引き渡す
        model.addAttribute("logs", logs);

        return "usage-logs";
    }
}