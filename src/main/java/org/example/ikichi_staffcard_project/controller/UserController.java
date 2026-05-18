package org.example.ikichi_staffcard_project.controller;

import org.apache.ibatis.annotations.Param;
import org.example.ikichi_staffcard_project.dto.User;
import org.example.ikichi_staffcard_project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public List<User> findAll(){
        return userService.findAll();
    }

    @PostMapping
    public ResponseEntity<User> insert(@RequestBody User user, UriComponentsBuilder ucBuilder){
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userService.insert(user);
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<String> updateUserName(@PathVariable Integer id, @RequestBody String newName){
        try{
            userService.changeUserName(id, newName);
            return ResponseEntity.ok().body(newName + "に変更しました");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("");
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<String> upDateStatus(@PathVariable Integer id, @RequestParam String status){
        userService.changeUserStatus(id, status);
        return ResponseEntity.ok("id "+ id + " 番の"+"ステータスを" +  status + "に更新しました");
    }

    @PutMapping("/{id}/passwordReset")
    public ResponseEntity<String> resetPassword(@PathVariable Integer id,@RequestBody String password){
        String encodedPassword = passwordEncoder.encode(password);
        userService.resetPassword(id, encodedPassword);
        return ResponseEntity.ok("id " + id + " 番の" + "パスワードをリセットしました。");
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<String> updateRole(@PathVariable Integer id, @RequestBody String role){
        try{
            userService.changeUserRole(id, role);
            return ResponseEntity.ok("id "+ id + " 番のロールを " + role + " に変更しました");
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
