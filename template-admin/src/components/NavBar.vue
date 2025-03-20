<template>
  <div class="admin-container">
    <!-- 左侧导航栏 -->
    <div class="left-nav">
      <!-- 头部：Logo 和项目名称 -->
      <div class="nav-header">
        <img class="logo" src="@/assets/logo.png" alt="Logo" />
        <span class="project-name" v-if="!isCollapse">{{ projectName }}</span>
      </div>

      <!-- 菜单 -->
      <el-menu
          default-active="/"
          class="el-menu-vertical"
          :collapse="isCollapse"
          @open="handleOpen"
          @close="handleClose"
          router
          background-color="#1f2d3d"
          text-color="#fff"
          active-text-color="#ffd04b"
      >
        <!-- 首页 -->
        <el-menu-item index="/">
          <el-icon><homeFilled /></el-icon>
          <span v-if="!isCollapse">首页</span>
        </el-menu-item>

        <!-- 用户管理 -->
        <el-sub-menu index="user">
          <template #title>
            <el-icon><userFilled /></el-icon>
            <span v-if="!isCollapse">用户管理</span>
          </template>
          <!-- 二级菜单 -->
          <el-menu-item index="/user/list">用户列表</el-menu-item>
          <el-menu-item index="/user/messages">用户消息</el-menu-item>
        </el-sub-menu>

        <!-- 新闻管理 -->
        <el-sub-menu index="news">
          <template #title>
            <el-icon><document /></el-icon>
            <span v-if="!isCollapse">新闻管理</span>
          </template>
          <!-- 二级菜单 -->
          <el-menu-item index="/news/list">新闻列表</el-menu-item>
        </el-sub-menu>

        <!-- 项目管理 -->
        <el-sub-menu index="project">
          <template #title>
            <el-icon><MessageBox /></el-icon>
            <span v-if="!isCollapse">项目管理</span>
          </template>
          <!-- 二级菜单 -->
          <el-menu-item index="/project/list">项目列表</el-menu-item>
          <el-menu-item index="/project/income">捐款明细</el-menu-item> <!-- 新增 -->
          <el-menu-item index="/project/review">项目评价</el-menu-item> <!-- 新增 -->
        </el-sub-menu>

        <!-- 项目管理 -->
        <el-sub-menu index="barrage">
          <template #title>
            <el-icon><DataLine /></el-icon>
            <span v-if="!isCollapse">弹幕管理</span>
          </template>
          <!-- 二级菜单 -->
          <el-menu-item index="/project/barrage">弹幕消息</el-menu-item>
        </el-sub-menu>


        <!-- 新增：其他配置 -->
        <el-sub-menu index="other-config">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span v-if="!isCollapse">其他配置</span>
          </template>
          <!-- 二级菜单 -->
          <el-menu-item index="/other-config/carousel">轮播管理</el-menu-item>
          <el-menu-item index="/other-config/announcement">公示信息管理</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </div>

    <!-- 右侧内容区域 -->
    <div class="right-content">
      <!-- 顶部导航栏 -->
      <div class="top-navbar">
        <!-- 折叠按钮 -->
        <div class="collapse-btn" @click="toggleCollapse">
          <el-icon :size="20">
            <expand v-if="isCollapse" />
            <fold v-else />
          </el-icon>
        </div>

        <!-- 面包屑 -->
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
          <el-breadcrumb-item>{{ currentRouteTitle }}</el-breadcrumb-item>
        </el-breadcrumb>

        <!-- 右侧用户信息 -->
        <div class="right-section">
          <!-- 主题切换 -->
          <div class="theme-switch">
            <el-switch
                v-model="isDark"
                :active-icon="Moon"
                :inactive-icon="Sunny"
                inline-prompt
                @change="toggleDark"
            />
          </div>

          <!-- 铃铛图标 -->
          <el-badge :value="unreadCount" :max="99" :hidden="unreadCount === 0" style="margin-right: 10px">
            <el-icon :size="25" class="bell-icon" @click="handleBellClick">
              <bell />
            </el-icon>
          </el-badge>

          <!-- 用户头像和气泡菜单 -->
          <el-popover
              placement="bottom"
              trigger="hover"
              :width="100"
              popper-class="user-popover"
          >
            <template #reference>
              <div class="user-info">
                <el-avatar :size="40" :src="user.avatar" />
                <span class="username">{{ user.name }}</span>
                <el-icon :size="16" class="arrow-icon">
                  <arrow-down />
                </el-icon>
              </div>
            </template>

            <!-- 气泡菜单内容 -->
            <div class="popover-content">
<!--              <div class="popover-item" @click="handleProfile">-->
<!--                <el-icon><userFilled /></el-icon>-->
<!--                <span>个人中心</span>-->
<!--              </div>-->
              <div class="popover-item" @click="handleMessages">
                <el-icon><message /></el-icon>
                <span>消息列表</span>
              </div>
              <div class="popover-item" @click="handleLogout">
                <el-icon><switch-button /></el-icon>
                <span>退出登录</span>
              </div>
            </div>
          </el-popover>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="main-content">
        <router-view></router-view> <!-- 路由内容 -->
      </div>
    </div>
  </div>
</template>

<script setup>
// eslint-disable-next-line no-unused-vars
import { Sunny, Moon, Bell, UserFilled, Message, SwitchButton, ArrowDown } from "@element-plus/icons-vue";
// eslint-disable-next-line no-unused-vars
import { HomeFilled, MessageBox, Document, Expand, Fold , DataLine,Setting } from "@element-plus/icons-vue";
import { useDark, useToggle } from "@vueuse/core";
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import {ElNotification} from "element-plus"; // 引入 axios
import LoadingIcon from '@/utils/LoadingIcon.vue'; // 引入自定义组件


// 主题切换
const isDark = useDark();
const toggleDark = useToggle(isDark);

// 模拟项目名称
const projectName = "心阳管理系统";

// 用户数据
const user = ref({
  name: "",
  avatar: "",
});

// 左侧导航栏是否折叠
const isCollapse = ref(false);

// 切换折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value;
};

// 菜单展开/折叠事件
const handleOpen = (key, keyPath) => {
  console.log("菜单展开:", key, keyPath);
};
const handleClose = (key, keyPath) => {
  console.log("菜单折叠:", key, keyPath);
};

// 获取当前路由的 meta.title
const route = useRoute();
const currentRouteTitle = computed(() => {
  return route.meta?.title || "未知页面"; // 如果 meta.title 不存在，返回默认值
});

// // 气泡菜单点击事件
// const handleProfile = () => {
//   console.log("点击了个人中心");
//   // 跳转到个人中心页面
// };

const handleMessages = () => {
  router.push("/user/messages");
};

const handleLogout = () => {
  // 清除用户登录状态
  localStorage.removeItem("token");
  // 跳转到登录页面
  router.push("/login");
};

// 未读消息数量
const unreadCount = ref(0);

// 处理铃铛点击事件
const handleBellClick = () => {
  // 跳转到消息列表页面
  router.push("/user/messages");
  // 清除未读消息数量
  unreadCount.value = 0;
};

// 检测 token 并获取用户数据
const router = useRouter();

const checkTokenAndFetchUser = async () => {
  const token = localStorage.getItem('token');
  if (token) {
    try {
      // 直接调用 API 获取用户信息
      const response = await axios.get('/admin/info', {
        headers: {
          Authorization: `Bearer ${token}`, // 携带 token
        },
      });
      console.log(response.data.code)
      // 更新用户信息
      if (response.data.code === 200) {
        user.value = {
          name: response.data.data.nickname || '用户', // 使用昵称，如果没有则显示默认值
          avatar: response.data.data.avatarUrl || 'https://pisiyi.oss-cn-beijing.aliyuncs.com/%E7%94%A8%E6%88%B7.png', // 使用头像，如果没有则显示默认值
        };
      } else {
        console.error('获取用户信息失败:', response.data.msg);
        handleLogout(); // 如果获取失败，执行退出登录
      }
    } catch (error) {
      console.error('获取用户信息失败:', error);
      handleLogout(); // 如果请求失败，执行退出登录
    }
  } else {
    isLoggedIn.value = false; // 用户未登录
  }
};

// 用户是否已登录
const isLoggedIn = ref(false);

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
    const message = JSON.parse(event.data);
    if (message.type === "Feedback") {
      unreadCount.value += 1; // 未读消息数量加 1

      // 显示通知
      ElNotification({
        title: '新消息',
        message: '接收到新的反馈消息',
        icon: LoadingIcon,
        duration: 3000, // 通知显示时间（毫秒）
      });

      // 播放提示音
      const audio = new Audio(require('@/assets/notification-sound.mp3')); // 使用 @/assets 路径
      audio.play().catch(error => {
        console.error('播放提示音失败:', error);
      });
    }
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

// 组件挂载时执行
onMounted(() => {
  checkTokenAndFetchUser();

  // 检测 WebSocket 连接状态
  if (!isConnected.value) {
    const uuid = 12138; // 从 localStorage 获取 uuid，如果没有则使用默认值
    connectWebSocket('ws://localhost:8080/websocket', uuid);
  }

  // 监听页面刷新事件
  window.addEventListener('beforeunload', () => {
    if (socket) {
      socket.close(); // 页面刷新前关闭 WebSocket
    }
  });
});
</script>

<style scoped>
.admin-container {
  display: flex;
  height: 100vh;
}

.left-nav {
  flex: 0 0 auto; /* 不伸缩，宽度由内容决定 */
  transition: width 0.3s ease;
  background-color: #1f2d3d; /* 固定深色背景 */
  border-right: 1px solid #1f2d3d; /* 固定深色边框 */
}

.admin-container {
  display: flex;
  height: 100vh;
}


.right-content {
  flex: 1; /* 占据剩余空间 */
  overflow: hidden; /* 防止内容溢出 */
}

.nav-header {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #1f2d3d; /* 固定深色边框 */
}

.logo {
  width: 30px;
  height: 30px;
  margin-right: 10px;
}

.project-name {
  font-size: 18px;
  font-weight: bold;
  color: #fff; /* 固定白色文字 */
  white-space: nowrap; /* 防止文字换行 */
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 200px;
}

:deep(.el-menu) {
  border-right: 1px solid #1f2d3d;
}

.right-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.top-navbar {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  background-color: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color);
}

.collapse-btn {
  margin-right: 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.right-section {
  display: flex;
  align-items: center;
  margin-left: auto; /* 将右侧内容推到最右边 */
}

.theme-switch {
  margin-right: 20px;
}

.bell-icon {
  margin-right: -2px;
  cursor: pointer;
}

.user-info {
  margin-left: 10px;
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 10px;
  font-size: 16px;
  font-weight: bold;
  color: var(--el-text-color-primary);
}

.arrow-icon {
  margin-left: 8px;
  transition: transform 0.2s;
}

.user-info:hover .arrow-icon {
  transform: rotate(180deg);
}

.main-content {
  flex: 1;
  padding: 20px;
  overflow-y: auto; /* 允许滚动 */
}

/* 隐藏滚动条 */
.main-content::-webkit-scrollbar {
  display: none;
}
</style>

<style>
/* 全局样式 */
body {
  transition: background-color 0.3s ease, color 0.3s ease;
}

body.dark {
  background-color: #1e1e1e;
  color: #fff;
}

/* 气泡菜单样式 */
.user-popover {
  padding: 0 !important;
}

.popover-content {
  padding: 8px 0;
}

.popover-item {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
}

.popover-item:hover {
  background-color: var(--el-bg-color-page);
}

.popover-item .el-icon {
  margin-right: 8px;
}
</style>
