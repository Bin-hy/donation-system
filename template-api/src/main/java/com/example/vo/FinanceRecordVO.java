package com.example.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FinanceRecordVO {
    private String id;
    private String projectId;
    private String projectName; // 项目名称
    private String userId;
    private String username; // 用户名称
    private String userAvatar; // 用户头像地址
    private BigDecimal amount;
    private Date time;
    private String category;
    private Integer paymentMethod;
    private Boolean isForeign;
    private String address;
}
