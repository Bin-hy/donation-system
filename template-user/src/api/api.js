// src/api/api.js
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080', // 后端 API 地址
    timeout: 5000, // 请求超时时间
});

export default api;
