package org.example.ikichi_staffcard_project.repository;

import org.example.ikichi_staffcard_project.dto.User;
import org.example.ikichi_staffcard_project.mapper.UserMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository {

    private final UserMapper userMapper;

    public UserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public List<User> findAll(){
        return userMapper.findAll();
    }

    public int insertUser(User user){
        return  userMapper.insertUser(user);
    }

    public void updateUserName(Integer id, String name){
        userMapper.updateUserName(id, name);
    }

    public void updateStatus(Integer id, String status) {
        userMapper.upDateStatus(id, status);
    }

    public void updatePassword(Integer id, String password) {
        userMapper.upDatePassword(id, password);
    }

    public void updateRole(Integer id, String role) {
        userMapper.updateRole(id, role);
    }
}
