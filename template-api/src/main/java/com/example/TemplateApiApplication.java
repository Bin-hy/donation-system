package com.example;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching//开启缓存注释功能
@EnableScheduling//开启定时任务
@MapperScan("com.example.mapper")
public class TemplateApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TemplateApiApplication.class, args);
    }

}
