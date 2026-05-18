package org.example.ikichi_staffcard_project.auth;

import org.example.ikichi_staffcard_project.dto.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserAuthRepository {
    private final UserAuthMapper userAuthMapper;

    public UserAuthRepository(UserAuthMapper userAuthMapper) {
        this.userAuthMapper = userAuthMapper;
    }

    public User findByStaffId(String staffId){
        return userAuthMapper.findById(staffId);
    }
}
