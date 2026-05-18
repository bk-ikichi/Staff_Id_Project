package org.example.ikichi_staffcard_project.dto;

import lombok.Data;

@Data
public class UsageRequest {
    private String staffId;      // 誰が
    private Long usedAtStoreId;  // どの店で
}
