package com.example.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Component
public class BaiduMapUtil {

    @Value("${baidu.map.api.key}") // 从配置文件中读取百度地图的 AK
    private String baiduMapApiKey;

    @Autowired
    private RestTemplate restTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据经纬度获取地点信息（返回市 + 区）
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return 地点信息（格式：市 + 区，如：天津市西青区）
     */
    public String getLocationFromCoordinates(Double latitude, Double longitude) {
        // 构建请求 URL
        String url = String.format(
                "http://api.map.baidu.com/reverse_geocoding/v3/?ak=%s&output=json&coordtype=wgs84ll&location=%s,%s",
                baiduMapApiKey, latitude, longitude
        );

        // 发送请求
        String response = restTemplate.getForObject(url, String.class);

        try {
            // 解析 JSON 响应
            JsonNode jsonNode = objectMapper.readTree(response);

            // 检查响应状态
            if (jsonNode.get("status").asInt() == 0) {
                // 提取市和区信息
                JsonNode addressComponent = jsonNode.get("result").get("addressComponent");
                String city = addressComponent.get("city").asText(); // 市
                String district = addressComponent.get("district").asText(); // 区

                // 拼接市 + 区
                return city + district;
            } else {
                // 如果请求失败，返回默认值
                return "未知地点";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "未知地点";
        }
    }

    /**
     * 判断地址是否属于中国境内
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return true-境内，false-境外
     */
    public boolean isDomestic(Double latitude, Double longitude) {
        // 构建请求 URL
        String url = String.format(
                "http://api.map.baidu.com/reverse_geocoding/v3/?ak=%s&output=json&coordtype=wgs84ll&location=%s,%s",
                baiduMapApiKey, latitude, longitude
        );

        // 发送请求
        String response = restTemplate.getForObject(url, String.class);

        try {
            // 解析 JSON 响应
            JsonNode jsonNode = objectMapper.readTree(response);

            // 检查响应状态
            if (jsonNode.get("status").asInt() == 0) {
                // 提取国家信息
                String country = jsonNode.get("result").get("addressComponent").get("country").asText();

                // 判断是否为中国
                return "中国".equals(country);
            } else {
                // 如果请求失败，默认返回境外
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
