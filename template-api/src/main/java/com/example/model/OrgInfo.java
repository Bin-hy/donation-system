package com.example.model;

import lombok.Data;

import java.util.UUID;

@Data
public class OrgInfo {
    private String id; // 机构信息ID
    private String projectId; // 关联的项目ID
    private String initiator; // 发起机构
    private String receiver; // 善款接收机构
    private String executor; // 执行机构

    // 生成 UUID 的方法
    public void generateId() {
        this.id = UUID.randomUUID().toString();
    }
}
