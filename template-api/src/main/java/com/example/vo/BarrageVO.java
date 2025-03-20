package com.example.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BarrageVO {
    private String id; // 弹幕 ID
    private String userId; // 用户 ID
    private String username; // 用户账号
    private String nickname; // 用户昵称
    private String avatar; // 用户头像
    private String content; // 弹幕内容
    private Date createdAt; // 弹幕发送时间
    private Boolean isBarrageBanned; // 是否禁止发送弹幕
}
