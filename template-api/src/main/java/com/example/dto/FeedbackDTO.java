package com.example.dto;

import lombok.Data;

@Data
public class FeedbackDTO {
    private String projectId;
    private String name; // 姓名
    private String phone; // 电话
    private String content; // 举报内容
}
