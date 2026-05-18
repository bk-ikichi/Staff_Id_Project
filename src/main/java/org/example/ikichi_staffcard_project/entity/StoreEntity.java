package org.example.ikichi_staffcard_project.entity;

import lombok.Data;

@Data
public class StoreEntity {
    private Integer id;
    private String comId;
    private String comName;
    private String location;
    private boolean isActive;
}
