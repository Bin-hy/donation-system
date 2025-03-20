package com.example.service;

import com.example.model.News;

import java.util.Date;
import java.util.List;

public interface NewsService {
    List<News> getNews(String title, String publisher, Date startDate, Date endDate);

    void addNews(News news);

    void updateNews(News news);

    void batchDeleteNews(List<String> ids);

    // 新增：更新推荐状态
    int updateRecommendStatus(String id, Integer isRecommended);

    List<News> getRecommendedNews();

    News getNewsDetailById(String id);
}
