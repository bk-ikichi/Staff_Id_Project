package org.example.ikichi_staffcard_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * デフォルトルート用のコントローラー
 */
@Controller
public class HomeController {

    /**
     * ルートへのアクセスをログイン画面へリダイレクト
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/auth/login";
    }
}
