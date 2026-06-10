package org.example.ikichi_staffcard_project.service;

import org.example.ikichi_staffcard_project.dto.User;
import org.example.ikichi_staffcard_project.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User insert(User user){
        userRepository.insertUser(user);
        return user;
    }

    public void changeUserStatus(Integer id,String status){
        userRepository.updateStatus(id,status);
    }

    public void resetPassword(Integer id,String password){
        userRepository.updatePassword(id, password);
    }

    public void changeUserRole(Integer id,String role){
        if (!role.equals("admin") && !role.equals("manager") && !role.equals("staff")) {
            throw new RuntimeException("無効なロール名です");
        }
        userRepository.updateRole(id, role);
    }
    // 📄 UserService.java の一番下（クラスの閉じカッコ } の直前）に追記してください

    public void updateUserFields(Integer id, String staffId, String name, String role, String status) {
        // スタッフIDのバリデーションチェック
        if (staffId == null || staffId.trim().isEmpty()) {
            throw new RuntimeException("スタッフIDを入力してください。");
        }

        // 氏名のバリデーションチェック
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("名前を入力してください。");
        }

        // 大文字小文字の表記揺れを防ぐため、念のため大文字に揃えてリポジトリに渡す
        String formattedRole = role != null ? role.toUpperCase() : "STAFF";
        String formattedStatus = status != null ? status.toUpperCase() : "ACTIVE";

        // リポジトリ層のメソッドを呼び出す
        userRepository.updateUserFields(id, staffId, name, formattedRole, formattedStatus);
    }
}
