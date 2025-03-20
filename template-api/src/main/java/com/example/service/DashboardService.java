package com.example.service;

import com.example.vo.DashboardDonationTrendVO;
import com.example.vo.DashboardStatsVO;
import com.example.vo.DashboardTrendVO;
import com.example.vo.DonationRankingVO;

import java.util.List;

public interface DashboardService {
    DashboardStatsVO getDashboardStats();

    DashboardTrendVO getDashboardTrend();

    DashboardDonationTrendVO getDashboardDonationTrend();

    List<DonationRankingVO> getDonationRanking();
}
