package com.example.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardDonationTrendVO {
    private List<String> dates;
    private List<Double> donations;
}
