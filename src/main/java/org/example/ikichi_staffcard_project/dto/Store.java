package org.example.ikichi_staffcard_project.dto;

import lombok.Data;

import java.util.List;

@Data
public class Store {
    private Integer id;
    private String comId;
    private String comName;
    private String location;
    private boolean isActive;

    private List<User> users;
}
