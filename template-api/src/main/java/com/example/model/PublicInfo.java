package com.example.model;

import lombok.Data;

import java.util.Date;

@Data
public class PublicInfo {
    private String id; // 公示信息唯一标识（UUID）
    private Integer type; // 公示信息类型（1: 年会报告, 2: 审计报告, 3: 慈善年鉴, 4: 专享报告）
    private String fileUrl; // 文件 URL
    private String imageUrl; // 图片 URL（新增字段）
    private Date createdAt; // 创建时间
    private Date updatedAt; // 更新时间
}
