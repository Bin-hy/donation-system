package com.example.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Danmu {

    private String id; // 弹幕的唯一标识（UUID）

    private String userId; // 用户ID，外键连接到users表的id

    private String content; // 弹幕的内容

    private LocalDateTime createdAt; // 弹幕发送的时间
}
