import { createApp } from 'vue';
import App from './App.vue';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import 'element-plus/theme-chalk/dark/css-vars.css';
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'; // 更新后的路径
import router from './router'; // 引入路由配置

const app = createApp(App);

// 使用 ElementPlus 并设置语言为中文
app.use(ElementPlus, {
    locale: zhCn,
});

app.use(router); // 使用路由

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

app.mount('#app');
