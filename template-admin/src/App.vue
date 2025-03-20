<template>
  <!-- 如果当前路由是登录页面，则显示登录组件 -->
  <LoginPage v-if="isLoginPage" />
  <!-- 否则显示导航栏和路由内容 -->
  <template v-else>
    <NavBar msg="Welcome to Your Vue.js App" />
  </template>
</template>

<script setup>
import { computed } from 'vue';
import { useRoute } from 'vue-router';
import NavBar from './components/NavBar.vue';
import LoginPage from './views/LoginPage.vue'; // 引入登录页面组件

// 获取当前路由
const route = useRoute();

// 判断当前路由是否为登录页面
const isLoginPage = computed(() => route.path === '/login');
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  margin: 0;
  padding: 0;
}
</style>
