package com.example.vo;

import lombok.Data;

@Data
public class DonationRankingVO {
    private int userRank;   // 排名（避免使用 rank 作为字段名）
    private String name;    // 用户昵称
    private String avatar;  // 用户头像
    private double amount;  // 总捐款金额
}
