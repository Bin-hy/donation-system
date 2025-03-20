package com.example.model;

import lombok.Data;

import java.util.UUID;

// 项目信息
@Data
public class ProjectInfo {
    private String id; // 项目信息ID
    private String projectId; // 关联的项目ID
    private String purpose; // 募捐目的
    private String cost; // 募捐成本
    private String surplusPlan; // 剩余财产处理方案
    private String invoiceMethod; // 发票开具方式
    private String contact; // 联系方式
    private String fundUsage; // 募得款物用途
    private String beneficiaries; // 受益人
    private String leader; // 活动负责人

    // 生成 UUID 的方法
    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }
}
