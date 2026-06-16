package org.example.ikichi_staffcard_project.controller;

import jakarta.servlet.http.HttpSession;
import org.example.ikichi_staffcard_project.dto.LoginResponse;
import org.example.ikichi_staffcard_project.dto.UsageResponse;
import org.example.ikichi_staffcard_project.service.UsageLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        List<UsageResponse> logs = usageLogService.getAllLogs();
        model.addAttribute("logs", logs);
        return "usage-logs";
    }

    /**
     * クーポン使用時のPOSTリクエスト処理
     */
    @PostMapping("/use-coupon")
    public String processUseCoupon(@RequestParam("userId") Integer userId,
                                   @RequestParam("storeId") Integer storeId,
                                   @RequestParam("storeName") String storeName,
                                   HttpSession session,
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        LoginResponse currentUser = (LoginResponse) session.getAttribute("currentUser");

        if (currentUser == null) {
            return "redirect:/auth/login";
        }

        try {
            // 画面から送られてきた userId を使ってログを保存
            usageLogService.processCouponUsage(userId, storeId);

            long expireTime = System.currentTimeMillis() + (5 * 60 * 1000);

            // フラッシュ属性を使ってリダイレクト先にパラメータを渡す
            redirectAttributes.addFlashAttribute("showTimer", true);
            redirectAttributes.addFlashAttribute("targetStoreId", storeId);
            redirectAttributes.addFlashAttribute("targetStoreName", storeName);
            redirectAttributes.addFlashAttribute("expireTime", expireTime);

            return "redirect:/users/card";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/users/card";
        }
    }
}