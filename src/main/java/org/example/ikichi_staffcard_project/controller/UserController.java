package org.example.ikichi_staffcard_project.controller;

import jakarta.servlet.http.HttpSession;
import org.example.ikichi_staffcard_project.dto.User;
import org.example.ikichi_staffcard_project.service.UserService;
import org.example.ikichi_staffcard_project.service.StoreService;
import org.example.ikichi_staffcard_project.service.UsageLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final StoreService storeService;
    private final UsageLogService usageLogService;

    @Autowired
    public UserController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          StoreService storeService,
                          UsageLogService usageLogService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.storeService = storeService;
        this.usageLogService = usageLogService;
    }

    // スタッフ一覧画面の表示
    @GetMapping
    public String showStaffManagerPage(Model model) {
        model.addAttribute("users", userService.findAll());
        return "staff-manager";
    }

    // スタッフ新規登録画面の表示
    @GetMapping("/new")
    public String showRegisterPage(Model model) {
        model.addAttribute("stores", storeService.findAll());
        if (!model.containsAttribute("newUser")) {
            model.addAttribute("newUser", new User());
        }
        return "staff-register";
    }

    // スタッフ新規登録（初期パスワードを自動設定）
    @PostMapping("/register")
    public String insert(@ModelAttribute("newUser") User user, RedirectAttributes redirectAttributes){
        try {
            // 固定の初期パスワードを付与して暗号化
            String defaultPassword = "Welcome2026";
            user.setPassword(passwordEncoder.encode(defaultPassword));

            userService.insert(user);
            redirectAttributes.addFlashAttribute("successMessage", "スタッフを新規登録しました！（初期パスワード: " + defaultPassword + "）");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "登録に失敗しました。スタッフIDが重複している可能性があります。");
            redirectAttributes.addFlashAttribute("newUser", user);
            return "redirect:/users/new";
        }
        return "redirect:/users";
    }

    // 初回ログイン時の強制パスワード変更画面の表示
    @GetMapping("/change-password")
    public String showChangePasswordPage() {
        return "change-password";
    }

    // 初回ログイン時のパスワード変更処理
    @PostMapping("/change-password")
    public String processChangePassword(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam String newPassword,
                                        RedirectAttributes redirectAttributes) {
        try {
            redirectAttributes.addFlashAttribute("successMessage", "パスワードを変更しました。新しいパスワードで利用可能です。");
            return "redirect:/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "パスワードの変更に失敗しました。");
            return "redirect:/users/change-password";
        }
    }

    // ステータス更新（ACTIVE / INACTIVE等）
    @PostMapping("/{id}/status")
    public String upDateStatus(@PathVariable Integer id, @RequestParam(value = "status", defaultValue = "ACTIVE") String status){
        userService.changeUserStatus(id, status);
        return "redirect:/users";
    }

    // パスワードリセット
    @PostMapping("/{id}/passwordReset")
    public String resetPassword(@PathVariable Integer id, @RequestParam String password){
        String encodedPassword = passwordEncoder.encode(password);
        userService.resetPassword(id, encodedPassword);
        return "redirect:/users";
    }

    // 役割（ロール）変更
    @PostMapping("/{id}/role")
    public String updateRole(@PathVariable Integer id, @RequestParam String role, RedirectAttributes redirectAttributes){
        try{
            userService.changeUserRole(id, role);
            redirectAttributes.addFlashAttribute("successMessage", "役割を変更しました");
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    // スタッフ情報の一括編集
    @PostMapping("/{id}/edit")
    public String editUserFields(@PathVariable("id") Integer id,
                                 @RequestParam("staffId") String staffId,
                                 @RequestParam("name") String name,
                                 @RequestParam("role") String role,
                                 @RequestParam("status") String status,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserFields(id, staffId, name, role, status);
            redirectAttributes.addFlashAttribute("successMessage", "スタッフ情報を更新しました。");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    /**
     * 一般スタッフ用：デジタル社員証・クーポン画面を表示
     * アクセスURL: http://localhost:8080/users/card
     */
    @GetMapping("/card")
    public String showStaffCard(@AuthenticationPrincipal UserDetails userDetails, HttpSession session, Model model) {
        // 💡 独自セッションからログイン中のユーザー情報を取得
        org.example.ikichi_staffcard_project.dto.LoginResponse currentUserSession =
                (org.example.ikichi_staffcard_project.dto.LoginResponse) session.getAttribute("currentUser");

        // セッションがなければログイン画面へリダイレクト
        if (currentUserSession == null || userDetails == null) {
            return "redirect:/auth/login";
        }

        // ログイン中のアカウント情報（スタッフID）からユーザー情報を特定
        String currentStaffId = userDetails.getUsername();

        List<User> allUsers = userService.findAll();
        User currentUser = allUsers.stream()
                .filter(u -> u.getStaffId() != null && u.getStaffId().equals(currentStaffId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("スタッフ情報が見つかりません。"));

        model.addAttribute("user", currentUser);

        // 💡 ACTIVEな店舗一覧を取得してモデルにセット
        List<org.example.ikichi_staffcard_project.dto.Store> activeStores = storeService.findAll().stream()
                .filter(org.example.ikichi_staffcard_project.dto.Store::isActive)
                .toList();
        model.addAttribute("activeStores", activeStores);

        // 💡 各店舗ごとに本日クーポン使用済みかの判定マップを作成してモデルにセット
        java.util.Map<Integer, Boolean> couponUsageMap = new java.util.HashMap<>();
        for (org.example.ikichi_staffcard_project.dto.Store store : activeStores) {
            boolean used = usageLogService.isCouponUsedToday(currentUser.getId(), store.getId());
            couponUsageMap.put(store.getId(), used);
        }
        model.addAttribute("couponUsageMap", couponUsageMap);

        return "staff-card";
    }
}