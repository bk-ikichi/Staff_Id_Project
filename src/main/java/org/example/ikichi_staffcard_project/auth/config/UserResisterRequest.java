package org.example.ikichi_staffcard_project.auth.config;

import lombok.Data;

@Data
public class UserResisterRequest {
    private String staffId;
    private String password;
    private String name;
    private String role;
    private Integer storeId;
}
