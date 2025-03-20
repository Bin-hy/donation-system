<template>
  <div class="home">
    <!-- 第一部分：轮播图 -->
    <el-carousel :interval="3000" height="400px" arrow="always" motion-blur>
      <el-carousel-item
          v-for="(item, index) in carouselItems"
          :key="index"
          @click="goToProjectDetail(item.projectId)"
      >
      <img
          :src="item.imageUrl"
      :alt="'轮播图 ' + (index + 1)"
      class="carousel-image"
      @error="handleImageError"
      />
      </el-carousel-item>
    </el-carousel>

    <!-- 最新公告 -->
    <div class="announcement-section">
      <div class="announcement-container">
        <!-- 左边：最新公告标题 -->
        <h2 class="announcement-title">
          <el-icon class="title-icon"><Reading /></el-icon> 最新新闻
        </h2>
      </div>
    </div>

    <!-- 第二部分：新闻资讯 -->
    <div class="news-section">
      <!-- 新闻轮播图 -->
      <div class="left-carousel">
        <el-carousel :interval="3000" height="405px" arrow="always" motion-blur width="540px">
          <el-carousel-item
              v-for="(item, index) in newsList.slice(0, 4)"
              :key="index"
              @click="goToNewsDetail(item.id)"
          >
            <img
                :src="item.coverUrl"
                :alt="item.title"
                class="carousel-image"
                @error="handleImageError"
            />
            <div class="carousel-overlay">
              <h3 class="overlay-title">{{ item.title }}</h3>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>

      <!-- 新闻列表 -->
      <div class="right-news">
        <div
            v-for="(news, index) in newsList.slice(4)"
            :key="index"
            class="news-item"
            @click="goToNewsDetail(news.id)"
        >
          <h3 class="news-title">{{ news.title }}</h3>
          <p class="news-content" v-html="news.content.slice(0, 80) + '...'"></p>
        </div>
      </div>
    </div>

    <!-- 最新公告 -->
    <div class="announcement-section">
      <div class="announcement-container">
        <!-- 左边：最新公告标题 -->
        <h2 class="announcement-title">
          <el-icon class="title-icon"><HelpFilled /></el-icon> 慈善资讯
        </h2>
      </div>
    </div>

    <!-- 第三部分：慈善项目 -->
    <div class="charity-projects">
      <div
          v-for="(project, index) in charityProjects"
          :key="index"
          class="project-item"
          @click="goToProjectDetail(project.id)"
      >
        <img :src="project.image" :alt="project.title" class="project-image" />
        <h3 class="project-title">{{ project.title }}</h3>
        <p class="project-time">
          <span class="project-category">{{ getCategoryName(project.category) }}</span>
          <span class="project-date">{{ formatDate(project.startDate) }} - {{ formatDate(project.endDate) }}</span>
        </p>
      </div>
    </div>

    <!-- 最新公告 -->
    <div class="announcement-section">
      <div class="announcement-container">
        <!-- 左边：最新公告标题 -->
        <h2 class="announcement-title">
          <el-icon class="title-icon"><Document /></el-icon> 信息公开
        </h2>
      </div>
    </div>

    <!-- 信息公开列表 -->
    <div class="info-disclosure">
      <!-- 信息公开列表 -->
        <div
            v-for="(info, index) in infoList"
            :key="index"
            class="info-item"
            @click="goToFileUrl(info.fileUrl)"
        >
          <img :src="info.imageUrl" :alt="公开数据" class="info-image" />
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import {ElCarousel, ElCarouselItem, ElIcon, ElNotification} from 'element-plus';
import { Reading, HelpFilled, Document } from '@element-plus/icons-vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import LoadingIcon from '@/utils/LoadingIcon.vue'; // 引入自定义组件

const router = useRouter();

// 检查是否已登录
const isLoggedIn = () => {
  return !!localStorage.getItem('token'); // 检查是否存在 Token
};

// 轮播图数据
const carouselItems = ref([]);

// 获取轮播图数据
const fetchCarouselData = async () => {
  try {
    const response = await axios.get('/user/carousel/list'); // 调用接口
    if (response.code === 200) {
      carouselItems.value = response.data; // 绑定数据
    } else {
      console.error('获取轮播图数据失败:', response.data.msg);
    }
  } catch (error) {
    console.error('获取轮播图数据失败:', error);
  }
};


// 跳转到项目详情页
const goToProjectDetail = (projectId) => {
  if (!isLoggedIn()) {
    // 弹出通知
    ElNotification({
      title: '未登录',
      message: '请先登录以访问此功能',
      icon: LoadingIcon,
      duration: 3000, // 通知显示时间（毫秒）
    });
    router.push({ name: 'LoginRegister' }); // 跳转到登录界面
    return;
  }
  if (projectId) {
    router.push({ name: 'ProjectDetail', params: { id: projectId } });
  } else {
    console.warn('当前轮播图未绑定项目');
  }
};

const newsList = ref([]);

// 获取新闻推荐数据
const fetchNewsRecommendData = async () => {
  try {
    const response = await axios.get('/user/news/recommend'); // 调用接口
    if (response.code === 200) {
      newsList.value = response.data; // 绑定数据
      console.log(newsList.value);
    } else {
      console.error('获取新闻推荐数据失败:', response.data.msg);
    }
  } catch (error) {
    console.error('获取新闻推荐数据失败:', error);
  }
};

// 跳转到新闻详情页
const goToNewsDetail = (newsId) => {
  router.push({ name: 'NewsDetail', params: { id: newsId } });
};

// 慈善项目数据
const charityProjects = ref([]);

// 获取慈善项目数据
const fetchCharityProjects = async () => {
  try {
    const response = await axios.get('/user/projects/recommend'); // 调用接口
    if (response.code === 200) {
      charityProjects.value = response.data; // 绑定数据
      console.log('慈善项目数据:', charityProjects.value);
    } else {
      console.error('获取慈善项目数据失败:', response.msg);
    }
  } catch (error) {
    console.error('获取慈善项目数据失败:', error);
  }
};

// 信息公开数据
const infoList = ref([]);

// 获取信息公开数据
const fetchInfoDisclosureData = async () => {
  try {
    const response = await axios.get('/user/info-disclosure/list'); // 调用接口
    if (response.code === 200) {
      infoList.value = response.data; // 绑定数据
      console.log('信息公开数据:', infoList.value);
    } else {
      console.error('获取信息公开数据失败:', response.msg);
    }
  } catch (error) {
    console.error('获取信息公开数据失败:', error);
  }
};

// 跳转到文件 URL
const goToFileUrl = (fileUrl) => {
  if (fileUrl) {
    window.open(fileUrl, '_blank'); // 在新标签页中打开
  } else {
    console.warn('当前信息公开项未绑定文件 URL');
  }
};

// 图片加载失败处理
const handleImageError = (event) => {
  event.target.src = 'https://via.placeholder.com/208x136?text=图片加载失败'; // 替换为默认图片或占位符
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString(); // 根据本地化格式显示日期
};

// 获取项目类型名称
const getCategoryName = (category) => {
  const categoryMap = {
    0: '扶贫济困',
    1: '健康医疗',
    2: '助老扶幼',
    3: '文化教育',
    4: '社会服务',
    5: '科技环保',
    6: '灾难救助',
    7: '专项基金',
  };
  return categoryMap[category] || '未知类型';
};

// 组件挂载时获取数据
onMounted(() => {
  fetchInfoDisclosureData();
  fetchNewsRecommendData(); // 获取新闻推荐数据
  fetchCarouselData();
  fetchCharityProjects();
});
</script>


<style scoped>
.home {
  text-align: center;
}

.carousel-image {
  width: 100%;
  height: 110%;
  object-fit: cover;
  border-radius: 8px;
}

/* 自定义轮播图样式 */
.el-carousel {
  max-width: 1800px;
  margin-bottom: 20px;
}

.el-carousel__item {
  background-color: #f5f5f5;
  border-radius: 8px;
}

/* 新闻资讯部分 */
.news-section {
  display: flex;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 左侧轮播图 */
.left-carousel {
  width: 540px;
  height: 405px;
}

.h2-title {
  text-align: left;
  margin-left: 160px;
  margin-top: 60px;
}

/* 右侧新闻列表 */
.right-news {
  margin-top: -10px;
  width: calc(100% - 600px); /* 减去左侧轮播图宽度和间距 */
}

.news-item {
  margin-bottom: 20px;
  text-align: left;
}

.news-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.news-content {
  font-size: 14px;
  color: #666;
}

/* 轮播图覆盖层 */
.carousel-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  padding: 4px;
  background: rgba(0, 0, 0, 0.6); /* 半透明黑色背景 */
  color: white;
  text-align: left;
  border-radius: 0 0 8px 8px; /* 底部圆角 */
}

.overlay-title {
  font-size: 18px;
  font-weight: bold;
  margin: 0;
}

/* 慈善项目部分 */
.charity-projects {
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* 4列 */
  gap: 20px; /* 项目之间的间距 */
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.project-item {
  text-align: left;
  background-color: #fff; /* 背景色 */
  border-radius: 8px; /* 圆角 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 阴影效果 */
  overflow: hidden; /* 防止内容溢出 */
  transition: transform 0.3s ease, box-shadow 0.3s ease; /* 添加过渡效果 */
}

.project-item:hover {
  transform: translateY(-5px); /* 悬停时上移 */
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 悬停时阴影加深 */
}

.project-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
  border-radius: 8px 8px 0 0; /* 图片顶部圆角 */
}

.project-title {
  font-size: 16px;
  font-weight: bold;
  margin: 10px 0 5px;
  padding: 0 16px; /* 内边距 */
}

.project-time {
  font-weight: bold;
  font-size: 14px;
  color: #f10000;
  text-align: right; /* 时间靠右对齐 */
  padding: 0 16px; /* 内边距 */
}

/* 最新公告部分 */
.announcement-section {

  margin-left: 160px;
  width: 200px;
  background-color: #f10000;
  padding: 10px 0;
}

.announcement-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  max-width: 150px;
  margin: 0 auto;
  padding: 0 20px;
}

.announcement-title {
  color: white; /* 白色文字 */
  font-size: 20px;
  margin: 0;
  padding-right: 20px;
  display: flex;
  align-items: center; /* 图标与文字垂直居中 */
}

.title-icon {
  font-size: 24px; /* 图标大小 */
  margin-right: 8px; /* 图标与文字的间距 */
}

.announcement-content {
  flex: 1;
  overflow: hidden;
  white-space: nowrap;
  position: relative;
}

.announcement-text {
  position: absolute;
  white-space: nowrap;
  color: white; /* 白色文字 */
  font-size: 16px;
}

.donate-button {
  margin-left: 20px;
}

/* 信息公开部分 */
.info-disclosure {
  display: flex;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.info-item {
  width: 275px;
  height: 113px;
  text-align: center;
  background-color: #fff; /* 背景色 */
  border-radius: 8px; /* 圆角 */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); /* 阴影效果 */
  overflow: hidden; /* 防止内容溢出 */
  transition: transform 0.3s ease, box-shadow 0.3s ease; /* 添加过渡效果 */
}

.info-item:hover {
  transform: translateY(-5px); /* 悬停时上移 */
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2); /* 悬停时阴影加深 */
}

.info-image {
  width: 100%;
  height: 113px; /* 图片高度 */
  object-fit: cover;
  border-radius: 8px 8px 0 0; /* 图片顶部圆角 */
}

.info-title {
  font-size: 14px;
  font-weight: bold;
  margin: 8px 0;
  padding: 0 8px; /* 内边距 */
}

.project-time {
  display: flex;
  justify-content: space-between; /* 项目类型和起止日期分别靠左和靠右 */
  align-items: center; /* 垂直居中 */
  padding: 0 16px; /* 内边距 */
  font-size: 14px;
}

.project-category {

  color: #000; /* 项目类型为黑色 */
}

.project-date {
  color: #f10000; /* 起止日期为红色 */
}
</style>
