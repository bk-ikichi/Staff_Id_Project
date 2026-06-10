package org.example.ikichi_staffcard_project.mapper;

import org.apache.ibatis.annotations.*;
import org.example.ikichi_staffcard_project.dto.Store;

import java.util.List;

@Mapper
public interface StoreMapper {

    // 店の一覧取得（修正：@Results と @Many を追加してユーザー情報も一緒に引き抜く）
    @Select("SELECT id, com_id, com_name, is_active, location FROM stores")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "comId", column = "com_id"),
            @Result(property = "comName", column = "com_name"),
            @Result(property = "location", column = "location"),
            @Result(property = "isActive", column = "is_active"),
            // 各店舗の id を使って、UserMapper から所属スタッフを芋づる式に自動取得
            @Result(property = "users", column = "id",
                    many = @Many(select = "org.example.ikichi_staffcard_project.mapper.UserMapper.findByStoreId"))
    })
    List<Store> findAll();

    // 店の追加
    @Insert("INSERT INTO stores (com_id,com_name,location,is_active) VALUES (#{comId},#{comName},#{location},true)")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insert(Store storeDTO);

    // 店に所属しているユーザーの取得（個別取得用）
    @Select("SELECT * FROM stores WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "comId", column = "com_id"),
            @Result(property = "comName", column = "com_name"),
            @Result(property = "location", column = "location"),
            @Result(property = "users", column = "id",
                    many = @Many(select = "org.example.ikichi_staffcard_project.mapper.UserMapper.findByStoreId"))
    })
    Store findByIdWithUsers(Integer id);

    // StoreMapper.java の一番下に以下を追加してください
    @Update("UPDATE stores SET com_id = #{comId}, com_name = #{comName}, location = #{location} WHERE id = #{id}")
    void updateStoreFields(@Param("id") Integer id,
                           @Param("comId") String comId,
                           @Param("comName") String comName,
                           @Param("location") String location);
}