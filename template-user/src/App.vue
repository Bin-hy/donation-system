<template>
  <div>
    <!-- 导航栏 -->
    <NavBar v-if="showNavBar" />

    <!-- 页面内容 -->
    <router-view />

    <!-- 底部备案栏 -->
    <div v-if="showFooter" class="footer">
      <!-- 左边部分 -->
      <div class="footer-column">
        <p>Copyright © 2012-2025 xinYang. All Rights Reserved.</p>
        <p>广州星城科技有限公司 版权所有</p>
        <p>京ICP证160478号</p>
        <p>地址：北京市西城区西环广场A座12层1201</p>
        <p>
          <img src="@/assets/police-icon.png" alt="公安图标" class="police-icon" />
          豫公网安备 41010402002001号 豫ICP备11032585号-5
        </p>
      </div>

      <!-- 右边部分 -->
      <div class="footer-column">
        <img src="@/assets/logo.png" alt="系统 Logo" class="system-logo" />
        <p>联系电话：400-123-4567</p>
        <p>微信号：xinchengwenhua</p>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import NavBar from '@/components/NavBar.vue';

const route = useRoute();
const showNavBar = ref(true);
const showFooter = ref(true);

// 监听路由变化，动态控制导航栏和底部备案栏的显示
watch(
    () => route.path,
    (newPath) => {
      // 如果当前路由是登录注册界面，隐藏导航栏和底部备案栏
      if (newPath === '/login-register') {
        showNavBar.value = false;
        showFooter.value = false;
      } else {
        showNavBar.value = true;
        showFooter.value = true;
      }
    },
    { immediate: true }
);
</script>

<style>
/* 全局样式 */
body {
  margin: 0;
  font-family: Avenir, Helvetica, Arial, sans-serif;
}

/* 底部备案栏 */
.footer {
  display: flex;
  justify-content: space-between; /* 左右两部分分开 */
  align-items: center; /* 垂直居中 */
  padding: 20px;
  background-color: #f8f9fa;
  border-top: 1px solid #e9ecef;
}

.footer-column {
  text-align: center;
  margin-left: 200px;
  flex: 1;
}

.footer-column p {
  margin: 8px 0;
  color: #666666;
  font-size: 14px;
  line-height: 1.5;
}

.system-logo {
  width: 80px;
  height: auto;
  margin-bottom: 10px;
}

.police-icon {
  width: 16px; /* 根据实际图标大小调整 */
  height: 16px; /* 根据实际图标大小调整 */
  vertical-align: middle; /* 与文字垂直对齐 */
  margin-right: 5px; /* 图标与文字的间距 */
}
</style>
