package com.example.vo;

import lombok.Data;

import java.util.List;

@Data
public class DashboardTrendVO {
    private List<String> dates;
    private List<Integer> barrages;
    private List<Integer> newUsers;
}
