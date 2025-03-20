package com.example.service.impl;

import com.example.mapper.CarouselMapper;
import com.example.model.Carousel;
import com.example.model.Project;
import com.example.service.CarouselService;
import com.example.vo.CarouselVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
public class CarouselServiceImpl implements CarouselService {
    @Autowired
    private CarouselMapper carouselMapper;

    @Override
    public List<CarouselVO> getCarouselList() {
        return carouselMapper.getCarouselList();
    }

    @Override
    public void addCarousel(Carousel carousel) {
        carousel.setId(String.valueOf(UUID.randomUUID()));
        carousel.setCreatedAt(new Timestamp(System.currentTimeMillis())); // 设置创建时间
        carousel.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // 设置更新时间

        carouselMapper.addCarousel(carousel);
    }

    @Override
    public void updateCarousel(Carousel carousel) {
        carousel.setUpdatedAt(new Timestamp(System.currentTimeMillis())); // 设置更新时间
        carouselMapper.updateCarousel(carousel);
    }

    @Override
    public void deleteCarousel(String id) {
        carouselMapper.deleteCarousel(id);
    }

    @Override
    public void batchDeleteCarousels(List<String> ids) {
        carouselMapper.batchDeleteCarousels(ids);
    }

}
