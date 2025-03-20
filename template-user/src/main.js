import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs';
import axios from 'axios'; // 引入 Axios

// 创建 Vue 应用
const app = createApp(App);

// 配置 Axios 全局默认值
axios.defaults.baseURL = 'http://localhost:8080'; // 后端 API 地址
axios.defaults.timeout = 5000; // 请求超时时间

// 请求拦截器：在每次请求前将 Token 添加到请求头中
axios.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`; // 添加 Token 到请求头
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器：统一处理错误响应
axios.interceptors.response.use(
    (response) => {
        return response.data; // 直接返回响应数据
    },
    (error) => {
        // 处理错误响应
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // Token 无效或过期，跳转到登录页
                    localStorage.removeItem('token');
                    router.push('/login');
                    break;
                case 404:
                    console.error('请求的资源不存在');
                    break;
                case 500:
                    console.error('服务器内部错误');
                    break;
                default:
                    console.error('请求失败', error.response.status);
            }
        }
        return Promise.reject(error);
    }
);

// 将 Axios 挂载到全局属性，方便在组件中使用
app.config.globalProperties.$axios = axios;

// 使用路由和 Element Plus
app.use(router);
app.use(ElementPlus, {
    locale: zhCn, // 配置 Element Plus 的中文语言包
});

// 定义全局方法
app.config.globalProperties.$connectWebSocket = (url, uuid) => {
    const socket = new WebSocket(`${url}?uuid=${uuid}`);

    socket.onopen = () => {
        console.log('WebSocket 连接已建立');
    };

    socket.onmessage = (event) => {
        console.log('收到消息:', event.data);
        // 处理收到的消息
    };

    socket.onclose = () => {
        console.log('WebSocket 连接已关闭');
    };

    socket.onerror = (error) => {
        console.error('WebSocket 错误:', error);
    };
};

// 挂载应用
app.mount('#app');


