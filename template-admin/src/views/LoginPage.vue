<template>
  <div class="container">
    <!-- 左侧登录表单 -->
    <div class="forms-container">
      <form class="sign-in-form" @submit.prevent="handleLogin">
        <h2 class="title">登录</h2>
        <div class="input-field">
          <img src="@/assets/username.png" alt="用户图标" class="icon" />
          <input type="text" placeholder="用户名" v-model="loginUsername" />
        </div>
        <div class="input-field">
          <img src="@/assets/password.png" alt="密码图标" class="icon" />
          <input type="password" placeholder="密码" v-model="loginPassword" />
        </div>
        <input type="submit" value="立即登录" class="btn solid" />
      </form>
    </div>

    <!-- 右侧四分之一圆圈和图片 -->
    <div class="circle-container">
      <div class="circle">
        <img src="@/assets/login.png" alt="登录图片" class="image" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElNotification } from 'element-plus';
import axios from 'axios'; // 引入 axios 用于发送请求
import { getCurrentInstance } from 'vue';

// 获取当前组件实例
// eslint-disable-next-line no-unused-vars
const { proxy } = getCurrentInstance();

const router = useRouter();

// 登录表单数据
const loginUsername = ref('');
const loginPassword = ref('');

// 处理登录
const handleLogin = async () => {
  if (!loginUsername.value || !loginPassword.value) {
    ElNotification({
      title: '提示',
      message: '请输入用户名和密码',
      type: 'warning',
      position: 'top-right',
      duration: 3000,
    });
    return;
  }

  try {
    const response = await axios.post('/admin/login', {
      username: loginUsername.value,
      password: loginPassword.value,
    });

    // 解析返回的数据
    const { code, message, data } = response.data;

    // 登录成功处理
    if (code === 200) {
      // 存储 token 到本地
      localStorage.setItem('token', data.token); // 使用 localStorage 持久化存储

      ElNotification({
        title: '成功',
        message: '登录成功！',
        type: 'success',
        position: 'top-right',
        duration: 1000,
      });

      // 连接 WebSocket，uuid 写死为 12138
      proxy.$connectWebSocket('ws://localhost:8080/websocket', '12138');
      // 跳转到首页
      await router.push('/');
    } else {
      // 登录失败处理
      ElNotification({
        title: '错误',
        message: message || '登录失败，请检查用户名和密码',
        type: 'error',
        position: 'top-right',
        duration: 3000,
      });
    }
  } catch (error) {
    // 请求失败处理
    ElNotification({
      title: '错误',
      message: '请求失败，请检查网络或稍后重试',
      type: 'error',
      position: 'top-right',
      duration: 3000,
    });
    console.error('登录请求失败:', error);
  }
};
</script>

<style scoped>
/* 引入字体 */
@import url("https://fonts.googleapis.com/css2?family=Poppins:wght@200;300;400;500;600;700;800&display=swap");

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body,
input {
  font-family: "Poppins", sans-serif;
}

.container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 左侧登录表单容器 */
.forms-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

/* 表单样式 */
form {
  width: 100%;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

/* 标题 */
.title {
  font-size: 2.2rem;
  color: #444;
  margin-bottom: 20px;
}

/* 输入框 */
.input-field {
  width: 100%;
  background-color: #f0f0f0;
  margin: 10px 0;
  height: 55px;
  border-radius: 55px;
  display: grid;
  grid-template-columns: 15% 85%;
  padding: 0 0.4rem;
  position: relative;
}

.input-field .icon {
  width: 30px;
  height: 30px;
  margin-top: 12px;
  margin-left: 15px;
}

.input-field input {
  background: none;
  outline: none;
  border: none;
  line-height: 1;
  font-weight: 600;
  font-size: 1.1rem;
  color: #333;
}

.input-field input::placeholder {
  color: #aaa;
  font-weight: 500;
}

/* 按钮 */
.btn {
  width: 100%;
  background-color: #409eff; /* 蓝色按钮 */
  border: none;
  outline: none;
  height: 49px;
  border-radius: 49px;
  color: #fff;
  text-transform: uppercase;
  font-weight: 600;
  margin: 10px 0;
  cursor: pointer;
  transition: 0.5s;
}

.btn:hover {
  background-color: #337ecc; /* 深蓝色悬停效果 */
}

/* 右侧四分之一圆圈容器 */
.circle-container {
  flex: 1;
  position: relative;
  background: linear-gradient(-45deg, #409eff 0%, #79bbff 100%); /* 蓝色渐变 */
  clip-path: ellipse(100% 100% at 100% 50%); /* 桌面端：右侧半圆 */
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 圆圈 */
.circle {
  width: 80%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 图片 */
.image {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    flex-direction: column; /* 改为垂直布局 */
  }

  .circle-container {
    flex: none; /* 取消 flex 占据的空间 */
    height: 200px; /* 设置固定高度 */
    clip-path: ellipse(100% 100% at 50% 100%); /* 移动端：底部半圆 */
    border-radius: 50% 50% 0 0; /* 顶部圆角 */
  }

  .circle {
    width: 100%;
    height: 100%;
  }

  .image {
    width: 80%;
    max-width: 200px; /* 限制图片大小 */
  }

  .forms-container {
    flex: 1;
    padding: 20px;
  }

  form {
    padding: 20px; /* 减少内边距 */
  }

  .title {
    font-size: 1.8rem; /* 缩小标题 */
  }
}
</style>


