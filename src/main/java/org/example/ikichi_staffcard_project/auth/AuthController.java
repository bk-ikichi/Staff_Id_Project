package org.example.ikichi_staffcard_project.auth;

import jakarta.servlet.http.HttpSession;
import org.example.ikichi_staffcard_project.auth.config.AuthenticationService;
import org.example.ikichi_staffcard_project.dto.LoginResponse;
import org.example.ikichi_staffcard_project.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.logging.Logger;

/**
 * 認証コントローラー（Thymeleaf・セッション管理版）
 * ログイン画面の表示、ログイン・ログアウト処理の制御
 */
@Controller
@RequestMapping("/auth")
public class AuthController {
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    private final AuthenticationService authenticationService;
    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthController(AuthenticationService authenticationService, UserDetailsService userDetailsService) {
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * ログイン画面の表示
     * URL: http://localhost:8080/auth/login
     */
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }
        return "login"; // templates/login.html を描画して返す
    }

    /**
     * ログイン実行処理
     */
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest, HttpSession session, Model model) {
        try {
            // バリデーションロジック
            if (loginRequest.getStaffId() == null || loginRequest.getStaffId().trim().isEmpty()) {
                throw new BadRequestException("スタッフIDが入力されていません", "STAFF_ID_REQUIRED");
            }
            if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
                throw new BadRequestException("パスワードが入力されていません", "PASSWORD_REQUIRED");
            }

            logger.info("Login attempt for staffId: " + loginRequest.getStaffId());

            // 認証処理の実行
            LoginResponse loginResponse = authenticationService.authentication(loginRequest);

            logger.info("Successful login for staffId: " + loginRequest.getStaffId());

            // 💡 認証済みユーザーのロール情報をUserDetailsから正しく引き出して設定します
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getStaffId());
            // @AuthenticationPrincipal でコントローラ側が UserDetails 型として受け取れるよう、principalにオブジェクトそのものを渡します
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            // セキュリティ管理領域（Context）に証拠をセット
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // セッションにセキュリティ情報を紐付ける（これで別画面移動時も認証が維持される）
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            // 既存のアプリケーション用セッションオブジェクトも保存
            session.setAttribute("currentUser", loginResponse);

            // ログイン成功時はデジタル社員証画面（/users/card）へ遷移
            return "redirect:/users/card";

        } catch (BadRequestException ex) {
            logger.warning("Bad request: " + ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("loginRequest", loginRequest);
            return "login";
        } catch (Exception ex) {
            logger.warning("Authentication failed: " + ex.getMessage());
            model.addAttribute("errorMessage", "ユーザーIDまたはパスワードが正しくありません");
            model.addAttribute("loginRequest", loginRequest);
            return "login";
        }
    }

    /**
     * ログアウト処理
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        logger.info("User logged out");
        session.invalidate(); // セッションを完全に破棄
        return "redirect:/auth/login";
    }
}