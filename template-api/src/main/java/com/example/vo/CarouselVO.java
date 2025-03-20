package com.example.vo;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class CarouselVO {
    private String id; // 轮播图 ID (UUID)
    private String imageUrl; // 轮播图 URL
    private String projectId; // 绑定的项目 ID (UUID)
    private String projectName; // 项目名称
    private Integer sort; // 排序号
    private Timestamp createdAt; // 创建时间
    private Timestamp updatedAt; // 更新时间
}
