package org.example.ikichi_staffcard_project.mapper;

import org.apache.ibatis.annotations.*;
import org.example.ikichi_staffcard_project.dto.User;

import java.util.List;

@Mapper
public interface UserMapper {

//    ユーザーの一覧
    @Select("SELECT id, staff_id AS staffId, name, role, store_id AS storeId, " +
            "status, created_at AS createdAt, updated_at AS updatedAt FROM users")
    List<User> findAll();

//    ユーザーのインサート
    @Insert("INSERT INTO users (staff_id, password, name, role, store_id) " +
            "VALUES (#{staffId}, #{password}, #{name}, #{role}, #{storeId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);

//    store側でユーザー一覧を呼び出す
    @Select("SELECT id,staff_id AS staffId,name,role,store_id AS storeId,status FROM users WHERE store_id = #{storeId}")
    List<User> findByStoreId(Integer storeId);

//    名前の変更
    @Update("UPDATE users SET name = #{name} WHERE id = #{id}")
    void updateUserName(@Param("id") Integer id, @Param("name") String name);

//    ステータス更新
    @Update("UPDATE users SET status = #{status} WHERE id = #{id}")
    void upDateStatus(@Param("id") Integer id, @Param("status") String status);

//    パスワードリセット
    @Update("UPDATE users SET password = #{password} WHERE id = #{id}")
    void upDatePassword(@Param("id") Integer id, @Param("password") String password);

//    ロールの更新
    @Update("UPDATE users SET role = #{role} WHERE id = #{id}")
    void updateRole(@Param("id") Integer id, @Param("role") String role);

}
