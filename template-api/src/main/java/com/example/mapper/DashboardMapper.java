package com.example.mapper;

import com.example.vo.DonationRankingVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DashboardMapper {

    int countUsers();

    double sumDonations();

    int countBarrages();


    List<String> getLastSevenDaysDates();


    List<Integer> getLastSevenDaysBarrages();


    List<Integer> getLastSevenDaysNewUsers();


    List<String> getLastTenDaysDates();


    List<Double> getLastTenDaysDonations();


    List<DonationRankingVO> getDonationRanking();
}
