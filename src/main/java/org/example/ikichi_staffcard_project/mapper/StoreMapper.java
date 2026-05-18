package org.example.ikichi_staffcard_project.mapper;

import org.apache.ibatis.annotations.*;
import org.example.ikichi_staffcard_project.dto.Store;

import java.util.List;

@Mapper
public interface StoreMapper {

//    店の一覧取得
    @Select("SELECT id,com_id,com_name,is_active,location FROM stores")
    List<Store> findAll();
//  　店の追加
    @Insert("INSERT INTO stores (com_id,com_name,location,is_active) VALUES (#{comId},#{comName},#{location},true)")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    int insert(Store storeDTO);

//    店に所属しているユーザーの取得
    @Select("SELECT * FROM stores WHERE id = #{id}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "comId", column = "com_id"),
            @Result(property = "comName", column = "com_name"),
            @Result(property = "location", column = "location"),
            // usersフィールドに、UserMapperのfindByStoreIdの結果を詰め込む指示
            @Result(property = "users", column = "id",
                    many = @Many(select = "org.example.ikichi_staffcard_project.mapper.UserMapper.findByStoreId"))
    })
    Store findByIdWithUsers(Integer id);

    @Update("UPDATE stores SET is_active = #{isActive} WHERE id = #{id}")
    void updateActiveStatus(@Param("id") Integer id, @Param("isActive") boolean isActive);
}
