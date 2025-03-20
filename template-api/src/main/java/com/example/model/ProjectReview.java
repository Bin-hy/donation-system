package com.example.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectReview {
    private String id; // 评价ID，使用UUID
    private String projectId; // 关联的项目ID
    private String userId; // 关联的用户ID
    private String review; // 评价内容
    private Integer isBanned; // 是否封禁
    private LocalDateTime createdAt; // 创建时间
    private LocalDateTime updatedAt; // 修改时间
}
