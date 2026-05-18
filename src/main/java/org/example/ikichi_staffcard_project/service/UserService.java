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

    public void changeUserName(Integer id, String name){
        if(name == null || name.trim().isEmpty()){
            throw new RuntimeException("名前を入力してください。");
        }
        userRepository.updateUserName(id, name);
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
}
