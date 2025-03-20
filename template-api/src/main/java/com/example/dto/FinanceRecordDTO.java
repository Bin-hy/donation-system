package com.example.dto;

import lombok.Data;

@Data
public class FinanceRecordDTO {
    private String projectId; // 项目 ID
    private Double amount; // 捐款金额
    private Integer paymentMethod; // 支付方式（0-支付宝，1-微信）
    private Double latitude; // 纬度
    private Double longitude; // 经度
}
