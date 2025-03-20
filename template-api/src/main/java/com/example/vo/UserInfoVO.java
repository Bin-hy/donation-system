package com.example.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    private String username;    // 用户名
    private String password;    // 密码
    private String nickname;    // 用户昵称
    private String avatar;      // 用户头像
    private String email;       // 邮箱
    private String phone;       // 电话
}
