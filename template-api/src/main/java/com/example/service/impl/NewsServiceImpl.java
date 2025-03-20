package com.example.service.impl;

import com.example.mapper.NewsMapper;
import com.example.model.News;
import com.example.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsMapper newsMapper;

    @Override
    public List<News> getNews(String title, String publisher, Date startDate, Date endDate) {
        return newsMapper.getNews(title, publisher, startDate, endDate);
    }

    @Override
    public void addNews(News news) {
        news.setId(String.valueOf(UUID.randomUUID()));
        news.setIsRecommended(0);
        newsMapper.addNews(news);
    }

    @Override
    public void updateNews(News news) {
        // 调用 Mapper 更新数据
        newsMapper.updateNews(news);
    }

    @Override
    public void batchDeleteNews(List<String> ids) {
        // 检查 ID 列表是否为空
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("ID 列表不能为空");
        }

        // 调用 Mapper 批量删除
        newsMapper.batchDeleteNews(ids);
    }


    @Override
    public int updateRecommendStatus(String id, Integer isRecommended) {
        return newsMapper.updateRecommendStatus(id, isRecommended);
    }


    @Override
    public List<News> getRecommendedNews() {
        return newsMapper.selectRecommendedNews();
    }

    @Override
    public News getNewsDetailById(String id) {
        return newsMapper.selectById(id);
    }


}
