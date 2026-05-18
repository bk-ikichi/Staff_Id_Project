package org.example.ikichi_staffcard_project.auth;

import org.apache.ibatis.annotations.*;
import org.example.ikichi_staffcard_project.dto.User;

@Mapper
public interface UserAuthMapper {

    @Select("SELECT * FROM users WHERE staff_id = #{staffId}")
    @Results({
            @Result(column = "staff_id", property = "staffId"),
            @Result(column = "store_id", property = "storeId")
    })
    User findById(@Param("staffId") String staffId);
}
