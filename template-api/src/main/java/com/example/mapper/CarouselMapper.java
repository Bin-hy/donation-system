package com.example.mapper;

import com.example.model.Carousel;
import com.example.vo.CarouselVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CarouselMapper {
    // 获取轮播图列表
    List<CarouselVO> getCarouselList();

    // 添加轮播图
    void addCarousel(Carousel carousel);

    // 编辑轮播图
    void updateCarousel(Carousel carousel);

    // 删除轮播图
    void deleteCarousel(String id);

    // 批量删除轮播图
    void batchDeleteCarousels(List<String> ids);
}
