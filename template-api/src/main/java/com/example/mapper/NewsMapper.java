package com.example.mapper;

import com.example.model.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

@Mapper
public interface NewsMapper {

    List<News> getNews(
            @Param("title") String title,
            @Param("publisher") String publisher,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );


    void addNews(News news);

    void updateNews(News news);

    void batchDeleteNews(List<String> ids);

    // 新增：更新推荐状态
    int updateRecommendStatus(
            @Param("id") String id,
            @Param("isRecommended") Integer isRecommended
    );

    List<News> selectRecommendedNews();

    @Select("SELECT * FROM news WHERE id = #{id}")
    News selectById(String id);
}
