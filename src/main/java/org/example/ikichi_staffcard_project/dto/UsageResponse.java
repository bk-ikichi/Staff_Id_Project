package org.example.ikichi_staffcard_project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsageResponse {
    private Integer id;
    private Integer userId;
    private String userName;      // JOINで取得した名前
    private Integer usedAtStoreId;
    private String storeName;     // JOINで取得した店舗名
    private LocalDateTime usedAt;
}
