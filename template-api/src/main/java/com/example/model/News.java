package com.example.model;

import lombok.Data;

import java.util.Date;

@Data
public class News {
    private String id;          // 新闻ID
    private String title;       // 新闻标题
    private Date date;          // 发布日期
    private String publisher;   // 发布者
    private String coverUrl;    // 封面图片URL
    private Integer type;       // 新闻类型（1-通知公告，2-媒体报道，3-慈善人物）
    private String content;     // 新闻内容
    private Integer isRecommended; // 是否推荐：0-未推荐，1-推荐
}
