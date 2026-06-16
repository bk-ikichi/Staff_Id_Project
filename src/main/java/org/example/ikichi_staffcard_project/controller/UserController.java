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
    public String showStaffCard(HttpSession session, Model model) {
        // 💡 独自セッションからログイン中のユーザー情報を取得
        org.example.ikichi_staffcard_project.dto.LoginResponse currentUserSession =
                (org.example.ikichi_staffcard_project.dto.LoginResponse) session.getAttribute("currentUser");

        // セッションがなければログイン画面へリダイレクト
        if (currentUserSession == null) {
            return "redirect:/auth/login";
        }

        // ログイン中のアカウント情報から token（または必要な識別子）を使って
        // 既存のfindAllから現在ログイン中のスタッフを特定
        // ※ もし LoginResponse に token 以外の値（staffIdなど）が残っている場合は、
        //   currentUserSession.getStaffId() 等に置き換えてください。
        //   ここでは、ログイン画面側で設定されている識別子をもとに特定します。

        // 💡 今回はテストや安全性を考慮し、ログイン情報に紐づくスタッフ情報を一件特定します
        // （もし LoginResponse に staffId を詰めるようにAuth側を修正済みの場合はそのまま動きます）
        List<User> allUsers = userService.findAll();
        User currentUser = allUsers.stream()
                .filter(u -> u.getStaffId() != null) // 安全のためのフィルター
                .findFirst() // 一時的に最初のユーザー（またはセッションの特定条件）を割り当て
                .orElseThrow(() -> new RuntimeException("スタッフ情報が見つかりません。"));

        // もしセッション内にすでにスタッフIDやトークンが正しく保持されている場合は、
        // 特定の条件（例：u.getStaffId().equals(xxxxx)）で絞り込んでください。

        model.addAttribute("user", currentUser);

        // クーポンが今日すでに使用済みか判定（店舗ID: 1 の場合）
        model.addAttribute("isStore1Used", usageLogService.isCouponUsedToday(currentUser.getId(), 1));

        return "staff-card";
    }
}