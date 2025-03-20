package com.example.service.impl;

import com.example.mapper.DashboardMapper;
import com.example.service.DashboardService;
import com.example.vo.DashboardDonationTrendVO;
import com.example.vo.DashboardStatsVO;
import com.example.vo.DashboardTrendVO;
import com.example.vo.DonationRankingVO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    @Override
    public DashboardStatsVO getDashboardStats() {
        DashboardStatsVO stats = new DashboardStatsVO();

        // 获取用户总数
        stats.setUserCount(dashboardMapper.countUsers());

        // 获取捐款总额
        stats.setDonationTotal(dashboardMapper.sumDonations());

        // 获取弹幕总数
        stats.setBarrageCount(dashboardMapper.countBarrages());

        return stats;
    }

    @Override
    public DashboardTrendVO getDashboardTrend() {
        DashboardTrendVO trendData = new DashboardTrendVO();

        // 获取近七天的日期列表
        List<String> dates = dashboardMapper.getLastSevenDaysDates();
        trendData.setDates(dates);

        // 获取近七天每天的新增弹幕数
        List<Integer> barrages = dashboardMapper.getLastSevenDaysBarrages();
        trendData.setBarrages(barrages);

        // 获取近七天每天的新增用户数
        List<Integer> newUsers = dashboardMapper.getLastSevenDaysNewUsers();
        trendData.setNewUsers(newUsers);

        return trendData;
    }

    @Override
    public DashboardDonationTrendVO getDashboardDonationTrend() {
        DashboardDonationTrendVO donationTrendData = new DashboardDonationTrendVO();

        // 获取近十天的日期列表
        List<String> dates = dashboardMapper.getLastTenDaysDates();
        donationTrendData.setDates(dates);

        // 获取近十天每天的捐赠金额
        List<Double> donations = dashboardMapper.getLastTenDaysDonations();
        donationTrendData.setDonations(donations);

        return donationTrendData;
    }

    @Override
    public List<DonationRankingVO> getDonationRanking() {
        try {
            // 获取捐款排行榜数据
            return dashboardMapper.getDonationRanking();
        } catch (Exception e) {
            // 异常处理
            throw new RuntimeException("获取捐款排行榜失败: " + e.getMessage());
        }
    }
}
