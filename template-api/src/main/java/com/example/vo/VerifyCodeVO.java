package com.example.vo;

import lombok.Data;
// 验证码
@Data
public class VerifyCodeVO {
    //验证码id
    private String code;
    //验证码图片base64编码
    private String ImageBase64;
}
