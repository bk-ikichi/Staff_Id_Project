package org.example.ikichi_staffcard_project.controller;

import org.example.ikichi_staffcard_project.dto.Store;
import org.example.ikichi_staffcard_project.service.StoreService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/store")
public class StoreController {
    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    // 一覧取得・画面表示
    @GetMapping
    public String getAll(Model model) {
        List<Store> stores = storeService.findAll();
        model.addAttribute("stores", stores);

        // 💡 【追加】HTMLフォーム用の空オブジェクトを「newStore」という名前で引き渡す
        if (!model.containsAttribute("newStore")) {
            model.addAttribute("newStore", new Store());
        }

        return "store-manager";
    }

    // 店の追加（HTML側のパスに合わせて /register を追加）
    @PostMapping("/register")
    public String insert(@ModelAttribute("newStore") Store store) {
        storeService.insert(store);
        return "redirect:/store";
    }

    // 指定店舗の所属ユーザー一覧表示
    @GetMapping("/{id}/all")
    public String getAllByStoreWithUsers(@PathVariable Integer id, Model model) {
        Store findStoreWithUsers = storeService.findByStoreWithUsers(id);
        model.addAttribute("storeWithUsers", findStoreWithUsers);
        return "store-detail";
    }

    // StoreController.java 内に追記してください
    @PostMapping("/{id}/edit")
    public String editStoreFields(@PathVariable("id") Integer id,
                                  @RequestParam("comId") String comId,
                                  @RequestParam("comName") String comName,
                                  @RequestParam("location") String location,
                                  org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            storeService.updateStoreFields(id, comId, comName, location);
            redirectAttributes.addFlashAttribute("successMessage", "店舗情報を更新しました。");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // 🔄 店舗一覧画面のURL（/stores など）に合わせて適宜リダイレクト先を調整してください
        return "redirect:/store";
    }
}