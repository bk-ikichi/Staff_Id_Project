package org.example.ikichi_staffcard_project.auth;

import org.example.ikichi_staffcard_project.dto.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {
    private final UserAuthRepository userAuthRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAuthService(UserAuthRepository userAuthRepository, PasswordEncoder passwordEncoder) {
        this.userAuthRepository = userAuthRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User login(String staffId,String password){
        // 1. ユーザーを検索
        User request = userAuthRepository.findByStaffId(staffId);

        // 2. 存在チェック & 退職者チェック
        if (request == null || "INACTIVE".equals(request.getStatus())) {
            throw new RuntimeException("ユーザーが見つからないか、既に退職しています。");
        }

        // 3. パスワード照合 (入力された生PW, DBのハッシュ化PW)
        if (!passwordEncoder.matches(password, request.getPassword())) {
            throw new RuntimeException("パスワードが一致しません。");
        }

        // 4. 成功したらパスワードを隠して返す
//        user.setPassword("********");
        return request;
    }
}
