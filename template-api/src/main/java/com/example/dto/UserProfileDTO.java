package com.example.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private String nickname;     // 昵称
    private String email;        // 邮箱
    private String phone;        // 电话
    private String avatar;       // 头像 URL
    private String password;  // 新密码（用于修改密码）

}
