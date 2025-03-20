package com.example.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Feedback {
    private String id;
    private String userId;
    private String projectId;
    private String name; // 姓名
    private String phone; // 电话
    private String message; // 举报内容
    private LocalDateTime createTime;
}
