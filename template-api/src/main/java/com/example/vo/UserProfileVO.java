package com.example.vo;

import lombok.Data;

@Data
public class UserProfileVO {
    private String nickname; // 昵称
    private String username; // 用户名
    private String email;    // 邮箱
    private String phone;    // 电话
    private String avatar;   // 头像 URL
}
