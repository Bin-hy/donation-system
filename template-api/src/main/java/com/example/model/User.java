package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class User {

    @Id
    private String id; // 用户唯一标识（UUID）

    private String username; // 用户名

    private String password; // 密码

    @Column
    private String email; // 邮箱

    @Column
    private String phone; // 电话

    @Column
    private String nickname; // 昵称

    @Column
    private String avatar; // 用户头像


    private Integer isBanned; // 是否被封禁（0: 未封禁, 1: 封禁）

    @Column
    private Integer isBarrageBanned; // 是否禁止发送弹幕（0: 允许, 1: 禁止）


    private LocalDateTime createdAt; // 创建时间


    private LocalDateTime updatedAt; // 修改时间


}
