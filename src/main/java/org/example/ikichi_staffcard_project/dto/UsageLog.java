package org.example.ikichi_staffcard_project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsageLog {
    private Integer id;
    private Integer userId;
    private Integer usedAtStoreId;
    private LocalDateTime usedAt;
}
