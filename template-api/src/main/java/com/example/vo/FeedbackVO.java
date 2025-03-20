package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackVO {
    private String id; // 反馈ID
    private String nickname; // 反馈人昵称
    private String realName; // 反馈人真实姓名
    private String phone; // 反馈人电话
    private String avatar; // 反馈人头像
    private String message; // 反馈内容
    private LocalDateTime createTime; // 发送时间
    private String projectTitle; // 项目名称（新增）
    private String projectImage; // 项目封面图片（新增）
}
