package com.example.model;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class Project {
    private String id; // 项目 ID
    private String title; // 项目名称
    private String recordNumber; // 项目编号
    private String targetAmount; // 目标金额
    private String raisedAmount; // 已筹金额
    private String donationCount; // 爱心次数
    private Date startDate; // 开始日期
    private Date endDate; // 结束日期
    private String details; // 项目详情
    private String image; // 封面图片
    private OrgInfo orgInfo; // 机构信息
    private ProjectInfo projectInfo; // 项目信息
    private Integer category; // 项目分类（0: 扶贫济困, 1: 健康医疗, 2: 助老扶幼, 3: 文化教育, 4: 社会服务, 5: 科技环保, 6: 灾难救助, 7: 专项基金）
    private Integer isRecommended; // 使用 Integer 类型映射 TINYINT(1)

    // 生成 UUID 的方法
    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }
}
