<template>
  <div class="container">
    <!-- 左侧四分之一圆圈和图片 -->
    <div class="circle-container">
      <div class="circle">
        <img src="@/assets/login.png" alt="登录图片" class="image" />
      </div>
    </div>

    <!-- 右侧登录/注册表单 -->
    <div class="forms-container">
      <!-- 登录表单 -->
      <form v-if="isLogin" class="sign-in-form" @submit.prevent="handleLogin">
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
        <p class="toggle-text">
          没有账号？
          <span class="toggle-link" @click="toggleForm">立即注册</span>
        </p>
      </form>

      <!-- 注册表单 -->
      <form v-else class="sign-up-form" @submit.prevent="handleRegister">
        <h2 class="title">注册</h2>
        <div class="input-field">
          <img src="@/assets/username.png" alt="用户图标" class="icon" />
          <input type="text" placeholder="用户名" v-model="registerUsername" />
        </div>
        <div class="input-field">
          <img src="@/assets/password.png" alt="密码图标" class="icon" />
          <input type="password" placeholder="密码" v-model="registerPassword" />
        </div>
        <div class="input-field">
          <img src="@/assets/password.png" alt="确认密码图标" class="icon" />
          <input type="password" placeholder="确认密码" v-model="confirmPassword" />
        </div>
        <input type="submit" value="立即注册" class="btn solid" />
        <p class="toggle-text">
          已有账号？
          <span class="toggle-link" @click="toggleForm">立即登录</span>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElNotification } from 'element-plus';
import api from '@/api/api';
import { getCurrentInstance } from 'vue';

// 获取当前组件实例
// eslint-disable-next-line no-unused-vars
const { proxy } = getCurrentInstance();

const router = useRouter();

// 控制登录/注册界面切换
const isLogin = ref(true);

// 登录表单数据
const loginUsername = ref('');
const loginPassword = ref('');

// 注册表单数据
const registerUsername = ref('');
const registerPassword = ref('');
const confirmPassword = ref('');

// 处理登录
const handleLogin = async () => {
  // 检查用户名和密码是否为空
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
    // 发起登录请求
    const response = await api.post('/user/login', {
      username: loginUsername.value,
      password: loginPassword.value,
    });

    // 登录成功
    if (response.data.code === 200) {
      const { token, isBanned , uuid} = response.data.data;

      // 将 isBanned 转换为数字类型
      const isBannedNumber = Number(isBanned);

      // 检查用户是否被封禁
      if (isBannedNumber === 1) {
        ElNotification({
          title: '警告',
          message: '您的账号已被封禁，请联系管理员',
          type: 'warning',
          position: 'top-right',
          duration: 5000, // 提示时间稍长
        });
        return; // 停止后续逻辑
      }

      // 保存 token 到本地存储
      localStorage.setItem('token', token);
      localStorage.setItem('uuid', uuid);

      // 连接 WebSocket，uuid 写死为 12138
      proxy.$connectWebSocket('ws://localhost:8080/websocket', uuid);

      // 登录成功，跳转到首页
      await router.push('/');
      ElNotification({
        title: '成功',
        message: '登录成功！',
        type: 'success',
        position: 'top-right',
        duration: 1000,
      });
    } else {
      // 登录失败
      ElNotification({
        title: '错误',
        message: response.data.msg || '登录失败',
        type: 'error',
        position: 'top-right',
        duration: 3000,
      });
    }
  } catch (error) {
    // 网络错误
    ElNotification({
      title: '网络错误',
      message: '请稍后重试',
      type: 'error',
      position: 'top-right',
      duration: 3000,
    });
  }
};

// 处理注册
const handleRegister = async () => {
  // 验证输入
  if (!registerUsername.value || !registerPassword.value || !confirmPassword.value) {
    ElNotification({
      title: '提示',
      message: '请填写所有字段',
      type: 'warning',
      position: 'top-right',
      duration: 3000,
    });
    return;
  }

  // 验证账号是否为纯数字的九位数
  const usernameRegex = /^\d{10}$/;
  if (!usernameRegex.test(registerUsername.value)) {
    ElNotification({
      title: '提示',
      message: '账号必须是纯数字的十位数',
      type: 'warning',
      position: 'top-right',
      duration: 3000,
    });
    return;
  }

  // 验证密码是否为字母和数字的组合
  const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/;
  if (!passwordRegex.test(registerPassword.value)) {
    ElNotification({
      title: '提示',
      message: '密码必须是字母和数字的组合，且至少8位',
      type: 'warning',
      position: 'top-right',
      duration: 3000,
    });
    return;
  }

  // 验证密码和确认密码是否一致
  if (registerPassword.value !== confirmPassword.value) {
    ElNotification({
      title: '提示',
      message: '两次输入的密码不一致',
      type: 'warning',
      position: 'top-right',
      duration: 3000,
    });
    return;
  }

  try {
    // 发起注册请求
    const response = await api.post('/user/register', {
      username: registerUsername.value,
      password: registerPassword.value,
    });
    // 注册成功
    if (response.data.code === 200) {
      ElNotification({
        title: '成功',
        message: '注册成功！',
        type: 'success',
        position: 'top-right',
        duration: 3000,
      });

      // 清空注册表单
      registerUsername.value = '';
      registerPassword.value = '';
      confirmPassword.value = '';

      // 切换回登录表单
      isLogin.value = true;
    } else {
      // 注册失败
      ElNotification({
        title: '错误',
        message: response.data.msg || '注册失败',
        type: 'error',
        position: 'top-right',
        duration: 3000,
      });
    }
  } catch (error) {
    // 请求失败
    ElNotification({
      title: '错误',
      message: '网络错误，请稍后重试',
      type: 'error',
      position: 'top-right',
      duration: 3000,
    });
  }
};

// 切换登录/注册表单
const toggleForm = () => {
  isLogin.value = !isLogin.value;
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

/* 左侧四分之一圆圈容器 */
.circle-container {
  flex: 1;
  position: relative;
  background: linear-gradient(-45deg, #ff4b4b 0%, #ff7f7f 100%);
  clip-path: ellipse(100% 100% at 0% 50%);
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

/* 右侧登录/注册表单容器 */
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
  border: 1px solid #ddd;
  border-radius: 10px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  background-color: #fff;
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
  background-color: #ff4b4b; /* 红色按钮 */
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
  background-color: #e63c3c; /* 深红色悬停效果 */
}

/* 切换文本 */
.toggle-text {
  margin-top: 10px;
  font-size: 0.9rem;
  color: #666;
}

.toggle-link {
  color: #ff4b4b;
  cursor: pointer;
  text-decoration: underline;
}

.toggle-link:hover {
  color: #e63c3c;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    flex-direction: column; /* 改为垂直布局 */
  }

  .circle-container {
    flex: none; /* 取消 flex 占据的空间 */
    height: 200px; /* 设置固定高度 */
    clip-path: ellipse(100% 100% at 50% 0%); /* 圆圈移动到顶部 */
    border-radius: 0 0 50% 50%; /* 底部圆角 */
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
