package com.example.service;

public interface EmailService {

    // 注册发送邮箱验证码
    public void sendRegistEmail(String emailTo) ;
    // 找回密码邮箱验证
    public void sendPasswordFindEmail(String emailTo) ;
    public boolean checkEmailCode(String email, String code);
}
