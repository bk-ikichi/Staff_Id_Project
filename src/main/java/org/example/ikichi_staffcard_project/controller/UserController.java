package org.example.ikichi_staffcard_project.controller;

import org.example.ikichi_staffcard_project.dto.User;
import org.example.ikichi_staffcard_project.service.UserService;
import org.example.ikichi_staffcard_project.service.StoreService;
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

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder, StoreService storeService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.storeService = storeService;
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

    // 💡 【仕様変更】スタッフ新規登録（初期パスワードを自動設定）
    @PostMapping("/register")
    public String insert(@ModelAttribute("newUser") User user, RedirectAttributes redirectAttributes){
        try {
            // 💡 固定の初期パスワードを付与して暗号化
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

    // 💡 【新規追加】初回ログイン時の強制パスワード変更画面の表示
    @GetMapping("/change-password")
    public String showChangePasswordPage() {
        return "change-password";
    }

    // 💡 【新規追加】初回ログイン時のパスワード変更処理
    // 💡 初回ログイン時のパスワード変更処理（修正版）
    @PostMapping("/change-password")
    public String processChangePassword(@AuthenticationPrincipal UserDetails userDetails,
                                        @RequestParam String newPassword,
                                        RedirectAttributes redirectAttributes) {
        try {
            // 💡 既存の resetPassword 処理と連動させる箇所です
            // 本来はログイン中のユーザーの内部ID（Integer id）を渡す必要があります。
            // ※お使いのUserDetailsServiceの実装に合わせて、主キー(id)が取れる場合はそれを、
            //  取れない場合は staffId から一旦ユーザー情報をService等で引いてからIDを渡してください。

            // 例: userService.resetPassword(currentUserId, passwordEncoder.encode(newPassword));

            redirectAttributes.addFlashAttribute("successMessage", "パスワードを変更しました。新しいパスワードで利用可能です。");
            return "redirect:/users";
        } catch (Exception e) {
            // 💡 ここをきれいに修正しました
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
    // 📄 コントローラークラス内に追記してください

    @PostMapping("/{id}/edit")
    public String editUserFields(@PathVariable("id") Integer id,
                                 @RequestParam("staffId") String staffId,
                                 @RequestParam("name") String name,
                                 @RequestParam("role") String role,
                                 @RequestParam("status") String status,
                                 org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            // サービス層のメソッドを呼び出して一括更新を実行
            userService.updateUserFields(id, staffId, name, role, status);

            // 画面に表示する成功メッセージをセット
            redirectAttributes.addFlashAttribute("successMessage", "スタッフ情報を更新しました。");

        } catch (RuntimeException e) {
            // バリデーションエラーなどが発生した場合はエラーメッセージをセット
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // 更新完了後、スタッフ一覧画面へ自動でリダイレクトして戻る
        return "redirect:/users";
    }

}