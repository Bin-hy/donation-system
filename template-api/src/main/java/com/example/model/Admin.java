package com.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Admin {

    @Id
    private String id; // 用户唯一标识（UUID）

    private String username; // 用户名

    private String password; // 密码

    @Column
    private String nickname; // 昵称

    @Column
    private String avatarUrl; // 用户头像

    private LocalDateTime createdAt; // 创建时间

    private LocalDateTime updatedAt; // 修改时间
}
