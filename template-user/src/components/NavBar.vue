<template>
  <div class="navbar">
    <!-- 左侧：网站 Logo 和名称 -->
    <div class="navbar-left" @click="goToHome">
      <img src="@/assets/logo.png" alt="Logo" class="logo" />
      <span class="site-name">心阳捐赠网</span>
    </div>

    <!-- 中间：导航栏目录 -->
    <div class="navbar-center">
      <ul class="nav-links">
        <li
            v-for="(link, index) in navLinks"
            :key="index"
            class="nav-item"
            :class="{ active: activeIndex === index }"
            @click="handleNavClick(link.path)"
        >
          <span class="nav-link">{{ link.name }}</span>
          <div class="underline" :class="{ 'underline-active': activeIndex === index }"></div>
        </li>
      </ul>
    </div>

    <!-- 右侧：用户头像和昵称 或 登录/注册按钮 -->
    <div class="navbar-right">
      <!-- 如果用户已登录，显示用户信息 -->
      <div
          v-if="isLoggedIn"
          class="user-info"
          @mouseenter="handleMouseEnter"
          @mouseleave="handleMouseLeave"
      >
        <img :src="user.avatar" alt="用户头像" class="user-avatar" />
        <span class="user-name">{{ user.name }}</span>

        <!-- 下拉菜单 -->
        <div
            v-if="showDropdown"
            class="dropdown-menu"
            @mouseenter="cancelCloseDropdown"
            @mouseleave="handleMouseLeave"
        >
          <div class="dropdown-arrow"></div>
          <div class="dropdown-item" @click="goToProfile">
            <el-icon><User /></el-icon>
            <span>个人中心</span>
          </div>
          <div class="dropdown-item" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出登录</span>
          </div>
        </div>
      </div>

      <!-- 如果用户未登录，显示登录/注册按钮 -->
      <button v-else class="login-register-btn" @click="goToLoginRegister">登录/注册</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router'; // 引入 useRoute
import { ElNotification } from 'element-plus';
import { User, SwitchButton } from '@element-plus/icons-vue';
import axios from 'axios';
import LoadingIcon from '@/utils/LoadingIcon.vue'; // 引入自定义组件

const router = useRouter();
const route = useRoute(); // 获取当前路由

// 导航栏目录数据
const navLinks = [
  { name: '首页', path: '/' },
  { name: '公益项目', path: '/projects' },
  { name: '新闻资讯', path: '/news' },
  { name: '捐赠公示', path: '/apply' },
  { name: '联系我们', path: '/about' },
];

// 用户信息
const user = ref({
  name: '',
  avatar: '',
});

// 当前选中的导航项索引
const activeIndex = ref(0);

// 是否显示下拉菜单
const showDropdown = ref(false);

// 用户是否已登录
const isLoggedIn = ref(false);

// 用于延迟关闭的定时器
let closeTimer = null;

// 检测 token 是否存在并获取用户信息
const checkLoginStatus = async () => {
  const token = localStorage.getItem('token');
  if (token) {
    isLoggedIn.value = true; // 用户已登录
    try {
      // 直接调用 API 获取用户信息
      const response = await axios.get('/user/info', {
        headers: {
          Authorization: `Bearer ${token}`, // 携带 token
        },
      });
      // 更新用户信息
      if (response.code === 200) {
        user.value = {
          name: response.data.nickname || '用户', // 使用昵称，如果没有则显示默认值
          avatar: response.data.avatar || 'https://pisiyi.oss-cn-beijing.aliyuncs.com/%E7%94%A8%E6%88%B7.png', // 使用头像，如果没有则显示默认值
        };
      } else {
        console.error('获取用户信息失败:', response.msg);
        logout(); // 如果获取失败，执行退出登录
      }
    } catch (error) {
      console.error('获取用户信息失败:', error);
      logout(); // 如果请求失败，执行退出登录
    }
  } else {
    isLoggedIn.value = false; // 用户未登录
  }
};

// WebSocket 实例
let socket = null;
const isConnected = ref(false);

// 连接 WebSocket
const connectWebSocket = (url, uuid) => {
  socket = new WebSocket(`${url}?uuid=${uuid}`);

  socket.onopen = () => {
    isConnected.value = true;
    console.log('WebSocket 连接已建立');
  };

  socket.onmessage = (event) => {
    console.log('收到消息:', event.data);
    // 处理收到的消息
  };

  socket.onclose = () => {
    isConnected.value = false;
    console.log('WebSocket 连接已关闭');
    // 自动重连
    setTimeout(() => {
      connectWebSocket(url, uuid);
    }, 3000); // 3 秒后重连
  };

  socket.onerror = (error) => {
    console.error('WebSocket 错误:', error);
  };
};


// 初始化时检查登录状态
onMounted(() => {
  checkLoginStatus();
  // 监听路由变化，更新 activeIndex
  watch(
      () => route.path,
      (newPath) => {
        const index = navLinks.findIndex((link) => link.path === newPath);
        if (index !== -1) {
          activeIndex.value = index;
        }
      },
      { immediate: true } // 立即执行一次
  );

  // 检测 WebSocket 连接状态
  if (!isConnected.value) {
    const uuid = localStorage.getItem('uuid'); // 从 localStorage 获取 uuid，如果没有则使用默认值
    connectWebSocket('ws://localhost:8080/websocket', uuid);
  }

  // 监听页面刷新事件
  window.addEventListener('beforeunload', () => {
    if (socket) {
      socket.close(); // 页面刷新前关闭 WebSocket
    }
  });
});

// 跳转到首页
const goToHome = () => {
  router.push('/');
};

// 处理导航栏点击事件
const handleNavClick = (path) => {
  if (path === '/') {
    // 首页可以直接跳转
    router.push(path);
  } else if (!isLoggedIn.value) {
    // 未登录时，跳转到登录界面
    ElNotification({
      title: '未登录',
      message: '请先登录以访问此功能',
      icon: LoadingIcon,
      duration: 3000,
    });
    router.push('/login-register');
  } else {
    // 已登录时，正常跳转
    router.push(path);
  }
};

// 跳转到登录注册界面
const goToLoginRegister = () => {
  router.push('/login-register');
};

// 跳转到个人中心
const goToProfile = () => {
  router.push('/profile');
};



// 退出登录
const logout = () => {
  localStorage.removeItem('token'); // 删除 token
  isLoggedIn.value = false; // 更新登录状态
  user.value = { name: '', avatar: '' }; // 清空用户信息
  router.push('/login-register'); // 跳转到登录注册页面
};

// 鼠标进入用户信息区域
const handleMouseEnter = () => {
  clearTimeout(closeTimer); // 清除关闭定时器
  showDropdown.value = true; // 显示下拉菜单
};

// 鼠标离开用户信息区域
const handleMouseLeave = () => {
  // 设置延迟关闭
  closeTimer = setTimeout(() => {
    showDropdown.value = false;
  }, 300); // 延迟 300ms 关闭
};

// 鼠标进入下拉菜单区域，取消关闭
const cancelCloseDropdown = () => {
  clearTimeout(closeTimer); // 清除关闭定时器
};
</script>

<style scoped>
/* 导航栏整体样式 */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  height: 60px;
}

/* 左侧：Logo 和网站名称 */
.navbar-left {
  display: flex;
  align-items: center;
  cursor: pointer; /* 添加鼠标指针样式 */
}

.logo {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

.site-name {
  font-size: 18px;
  font-weight: bold;
  color: #333333;
}

/* 中间：导航栏目录 */
.navbar-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-links {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
}

.nav-item {
  margin: 0 20px;
  padding: 10px 0;
  cursor: pointer;
  position: relative;
}

.nav-link {
  text-decoration: none;
  color: #333333;
  font-size: 18px;
  transition: color 0.3s ease;
}

.nav-item.active .nav-link {
  font-weight: 700;
  color: #d32f2f;
}

.nav-item:hover .nav-link {
  font-weight: 700;
  color: #d32f2f;
}

.underline {
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 0;
  height: 2px;
  background-color: #e40000;
  transition: width 0.3s ease;
}

.underline-active {
  width: 100%;
}

.nav-item:hover .underline {
  width: 100%;
}

/* 右侧：用户头像和昵称 或 登录/注册按钮 */
.navbar-right {
  display: flex;
  align-items: center;
}

/* 用户信息 */
.user-info {
  position: relative;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.user-name {
  font-size: 16px;
  color: #333333;
}

/* 下拉菜单 */
.dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background-color: #ffffff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-radius: 4px;
  padding: 8px 0;
  min-width: 120px;
  z-index: 1000;
}

.dropdown-arrow {
  position: absolute;
  top: -8px;
  right: 16px;
  width: 0;
  height: 0;
  border-left: 8px solid transparent;
  border-right: 8px solid transparent;
  border-bottom: 8px solid #ffffff;
}

.dropdown-item {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  font-size: 14px;
  color: #333333;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.dropdown-item .el-icon {
  margin-right: 8px;
}

.dropdown-item:hover {
  background-color: #f5f5f5;
}

/* 登录/注册按钮 */
.login-register-btn {
  padding: 8px 16px;
  font-size: 14px;
  color: #ffffff;
  background-color: #e40000;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.login-register-btn:hover {
  background-color: #c40000;
}
</style>
