package org.example.ikichi_staffcard_project.auth.config;

import org.example.ikichi_staffcard_project.auth.LoginRequest;
import org.example.ikichi_staffcard_project.auth.UserAuthRepository;
import org.example.ikichi_staffcard_project.dto.LoginResponse;
import org.example.ikichi_staffcard_project.dto.User;
import org.example.ikichi_staffcard_project.exception.LoginArgumentNotValidException;
import org.example.ikichi_staffcard_project.exception.UserRegistrationException;
import org.example.ikichi_staffcard_project.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthRepository userAuthRepository;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder, UserAuthRepository userAuthRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAuthRepository = userAuthRepository;
    }

    // 認証してレスポンスを返す
    public LoginResponse authentication(LoginRequest loginRequest) {
        if (loginRequest.getStaffId() == null || loginRequest.getStaffId().isEmpty()) {
            throw new LoginArgumentNotValidException("username is empty");
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new LoginArgumentNotValidException("password is empty");
        }

        // 認証
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getStaffId(),
                        loginRequest.getPassword()
                )
        );

        User user = userAuthRepository.findByStaffId(loginRequest.getStaffId());
        if (user == null) {
            throw new LoginArgumentNotValidException("user is not found");
        }

        // JWTトークンの生成
        String token = jwtService.generateToken(user.getStaffId(), user.getRole());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(token);
        return loginResponse;
    }

    public LoginResponse registerUser(UserResisterRequest userResisterRequest){
        if(userResisterRequest.getStaffId() == null || userResisterRequest.getStaffId().isEmpty()){
            throw new UserRegistrationException("username is empty");
        }

        if(userResisterRequest.getPassword() == null || userResisterRequest.getPassword().isEmpty()){
            throw new UserRegistrationException("password is empty");
        }

        if(userResisterRequest.getName() == null || userResisterRequest.getName().isEmpty()){
            throw new UserRegistrationException("name is empty");
        }

        if(userResisterRequest.getRole() == null || userResisterRequest.getRole().isEmpty()){
            throw new UserRegistrationException("role is empty");
        }

        if(userAuthRepository.findByStaffId(userResisterRequest.getStaffId())!=null){
            throw new UserRegistrationException("username is exist");
        }

        String hashedPassword = passwordEncoder.encode(userResisterRequest.getPassword());
        User user = new User();
        user.setStaffId(userResisterRequest.getStaffId());
        user.setPassword(hashedPassword);
        user.setName(userResisterRequest.getName());
        user.setRole(userResisterRequest.getRole());
        user.setStoreId(userResisterRequest.getStoreId());
        userRepository.insertUser(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtService.generateToken(user.getStaffId(), user.getRole()));
        return loginResponse;
    }
}
