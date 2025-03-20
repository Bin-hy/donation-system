package com.example.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectReviewDetailVO {
    private String id; // 评价ID
    private String projectId; // 项目ID
    private String userId; // 用户ID
    private String review; // 评价内容
    private Boolean isBanned; // 是否封禁
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 修改时间
    private String reviewer; // 评价人昵称
    private String reviewerAvatar; // 评价人头像
    private String projectTitle; // 项目名称
}
