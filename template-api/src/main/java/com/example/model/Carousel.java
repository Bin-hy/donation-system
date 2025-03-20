package com.example.model;

import lombok.Data;

import java.util.Date;

@Data
public class Carousel {
    private String id; // 轮播图 ID
    private String imageUrl; // 图片 URL
    private String projectId; // 绑定项目 ID
    private Integer sort; // 排序号
    private Date createdAt; // 创建时间
    private Date updatedAt; // 更新时间
}
