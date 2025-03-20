<template>
  <div class="news-detail">
    <!-- 标题 -->
    <h2 class="news-title">{{ newsDetail.title }}</h2>

    <!-- 发布信息 -->
    <div class="news-meta">
      <span class="publish-time">发布时间：{{ newsDetail.date }}</span>
      <span class="publisher">发布者：{{ newsDetail.publisher }}</span>
    </div>

    <!-- 内容 -->
    <div class="news-content" v-html="newsDetail.content"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const newsId = route.params.id; // 获取路由中的 id 参数

// 新闻详情数据
const newsDetail = ref({
  title: '',
  date: '',
  publisher: '',
  content: '',
});

// 格式化日期
const formatDate = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return date.toLocaleDateString(); // 根据本地化格式显示日期
};

// 获取新闻详情数据
// 获取新闻详情数据
const fetchNewsDetail = async () => {
  try {
    const response = await axios.get('/user/news/detail', {
      params: { id: newsId }, // 传递 id 参数
    });
    if (response.code === 200) {
      // 绑定数据，并格式化日期
      newsDetail.value = {
        ...response.data,
        date: formatDate(response.data.date), // 格式化时间戳
      };
    } else {
      console.error('获取新闻详情失败:', response.msg);
    }
  } catch (error) {
    console.error('获取新闻详情失败:', error);
  }
};

// 组件挂载时获取数据
onMounted(() => {
  fetchNewsDetail();
});
</script>

<style scoped>
.news-detail {
  max-width: 925px;
  margin: 0 auto;
  text-align: left;
  background-color: #fff;
  border-radius: 8px;
}

.news-title {
  font-size: 20px;
  color: #333;
  text-align: center;
  line-height: 50px;
  border-bottom: 1px solid #e5e5e5;
  margin-bottom: 10px;
}

.news-meta {
  margin-left: 290px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
}

.publish-time {
  margin-right: 16px;
}

.news-content {
  font-size: 16px;
  line-height: 1.6;
  color: #444;
}

.news-content img {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 10px 0;
}

.news-content ul {
  padding-left: 20px;
  margin: 10px 0;
}

.news-content li {
  margin-bottom: 8px;
}
</style>
