package org.example.ikichi_staffcard_project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;        // SERIAL (自動採番)
    private String staffId;    // staff_id (業務用ID: s001など)
    private String password;   // password (ハッシュ化された文字列)
    private String name;       // name
    private String role;       // role (高校生, 社員など)
    private Integer storeId;   // store_id (店舗マスタのidと紐付け)
    private String status;     // status (DEFAULT 'ACTIVE')
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Store store;
}
