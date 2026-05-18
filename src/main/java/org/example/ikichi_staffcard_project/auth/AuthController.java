package org.example.ikichi_staffcard_project.auth;

import org.example.ikichi_staffcard_project.auth.config.UserResisterRequest;
import org.example.ikichi_staffcard_project.dto.LoginResponse;
import org.example.ikichi_staffcard_project.dto.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserAuthService userAuthService;

    @Autowired
    public AuthController(UserAuthService userAuthService){
        this.userAuthService = userAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        try{
            User request = userAuthService.login(loginRequest.getStaffId(), loginRequest.getPassword());
            return ResponseEntity.ok("ログイン成功しました");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

//    @PostMapping("/register")
//    public ResponseEntity<LoginResponse> register(@RequestBody UserResisterRequest userResisterRequest){
//        LoginResponse loginResponse = userAuthService.registerUser(userResisterRequest);
//        return ResponseEntity.ok(loginResponse);
//    }
}
