package org.example.ikichi_staffcard_project.mapper;

import org.apache.ibatis.annotations.*;
import org.example.ikichi_staffcard_project.dto.User;

import java.util.List;

@Mapper
public interface UserMapper {

//    ユーザーの一覧
    @Select("SELECT u.id, u.staff_id AS staffId, u.name, u.role, u.store_id AS storeId, " +
            "u.status, u.created_at AS createdAt, u.updated_at AS updatedAt, " +
            "s.id AS \"store.id\", " +
            "s.com_id AS \"store.comId\", " +
            "s.com_name AS \"store.comName\", " +
            "s.location AS \"store.location\", " +
            "s.is_active AS \"store.active\" " +
            "FROM users u " +
            "LEFT JOIN stores s ON u.store_id = s.id")
    List<User> findAll();

//    ユーザーのインサート
    @Insert("INSERT INTO users (staff_id, password, name, role, store_id) " +
            "VALUES (#{staffId}, #{password}, #{name}, #{role}, #{storeId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertUser(User user);

//    store側でユーザー一覧を呼び出す
    @Select("SELECT id,staff_id AS staffId,name,role,store_id AS storeId,status FROM users WHERE store_id = #{storeId}")
    List<User> findByStoreId(Integer storeId);

// 📄 UserMapper.java の一番下に以下のメソッドを追加してください

    @Update("UPDATE users SET staff_id = #{staffId}, name = #{name}, role = #{role}, status = #{status} WHERE id = #{id}")
    void updateUserFields(@Param("id") Integer id,
                          @Param("staffId") String staffId,
                          @Param("name") String name,
                          @Param("role") String role,
                          @Param("status") String status);

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
