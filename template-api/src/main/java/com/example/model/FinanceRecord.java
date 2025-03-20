package com.example.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FinanceRecord {
    private String id; // 收支记录ID，使用UUID
    private String projectId; // 关联的项目ID
    private String userId; // 关联的用户ID
    private Double amount; // 金额
    private LocalDateTime time; // 时间
    private String category; // 分类
    private Integer paymentMethod; // 支付方式（0-支付宝，1-微信）
    private Integer isForeign; // 是否来自境外（0-否，1-是）
    private String address; // 地址
}
