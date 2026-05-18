package org.example.ikichi_staffcard_project.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String staffId;
    private String password;
}
