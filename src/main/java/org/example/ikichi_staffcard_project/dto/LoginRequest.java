package org.example.ikichi_staffcard_project.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String staffId;
    private String password;
}
