package org.example.ikichi_staffcard_project.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityConfig() {
        // JwtAuthenticationFilterのDIは不要になったため削除
    }

    /**
     * パスワードエンコーダー
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 認証マネージャー
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * セキュリティフィルターチェーン（Thymeleaf・セッション管理用）
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // HTMLフォームからの通常アクセスになるため、CORS設定は不要（削除）

                // CSRF対策：今回は独自コントローラーでログイン処理を行うため一旦無効化
                .csrf(csrf -> csrf.disable())

                // セッション管理：STATELESSから、通常のセッション利用（状態保持）へ変更
                .sessionManagement(session -> session
                        .maximumSessions(1) // 同一ユーザーの重複ログイン制限（必要に応じて）
                )

                // 認可設定
                .authorizeHttpRequests(auth -> auth
                        // ログイン画面、ログアウト処理、エラー画面、およびルートURLは認証なしでアクセス可能
                        .requestMatchers("/", "/auth/login", "/auth/logout", "/error").permitAll()
                        // 静的ファイル（CSS/JS/画像等）を追加した際のための解放設定
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // 一般スタッフ用デジタル社員証、およびクーポン利用のPOSTはログインしていれば全ロールに許可
                        .requestMatchers("/users/card").authenticated()
                        .requestMatchers("/usage/use-coupon").authenticated()
                        // スタッフ管理、店舗管理、利用ログ一覧などの管理者用画面はADMIN、MANAGER、またはSTORE_MANAGERのみ許可
                        .requestMatchers("/users/**", "/store/**", "/usage/**").hasAnyRole("ADMIN", "MANAGER", "STORE_MANAGER")
                        // それ以外の全ての画面はログイン必須
                        .anyRequest().authenticated()
                )

                // 独自コントローラー（AuthController）でセッション制御するため標準フォームは無効化
                .formLogin(form -> form.disable())

                // ログアウトの設定
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                );

        // JWT フィルター（jwtAuthenticationFilter）の追加処理を削除

        return http.build();
    }
}