package org.example.ikichi_staffcard_project.entity;

import lombok.Data;
import org.apache.tomcat.util.codec.binary.StringUtils;

import java.time.LocalDateTime;

@Data
public class UserEntity {
    private Integer id;
    private String staffId;
    private String name;
    private String password;
    private String role;
    private Long storeId;
    private String status;
//    private String deviceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
