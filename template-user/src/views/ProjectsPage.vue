<template>
  <div class="projects-page">
    <!-- 标签分类 -->
    <div class="tags-container">
      <div class="tags">
        <div
            v-for="(tag, index) in tags"
            :key="index"
            class="tag"
            :class="{ active: activeTag === tag }"
            @click="setActiveTag(tag)"
            @mouseenter="hoverTag = tag"
            @mouseleave="hoverTag = null"
        >
          {{ tag }}
        </div>
      </div>

      <!-- 搜索框 -->
      <div class="search-box">
        <input
            type="text"
            v-model="searchQuery"
            placeholder="请输入项目关键字"
        />
        <button @click="handleSearch">搜索</button>
      </div>
    </div>

    <!-- 项目列表 -->
    <div class="project-list">
      <div
          v-for="project in paginatedProjects"
          :key="project.id"
          class="project-item"
          @click="goToProjectDetail(project.id)"
      >
        <img :src="project.image" :alt="project.title" class="project-image" />
        <div class="project-info">
          <div class="project-header">
            <h2>{{ project.title }}</h2>
            <button @click.stop="goToDonationPage(project.id)">我要捐款</button>
          </div>
          <div class="project-meta">
            <div class="meta-item">
              <span class="meta-label">已筹金额：</span>
              <span class="meta-value" style="color: #d32f2f;font-weight: 700;font-size: 20px;margin-top: -5px">￥{{ project.raisedAmount }}</span>元
            </div>
            <div class="meta-item">
              <span class="meta-label">爱心人次：</span>
              <span class="meta-value">{{ project.donationCount }} 次</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">项目类别：</span>
              <span class="meta-value">{{ getCategoryName(project.category) }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">有效期：</span>
              <span class="meta-value">{{ formatDate(project.startDate) }} 至 {{ formatDate(project.endDate) }}</span>
            </div>
          </div>
          <p class="project-description">{{ project.details }}</p>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <button
          :disabled="currentPage === 1"
          @click="goToPage(1)"
      >
        第一页
      </button>
      <button
          :disabled="currentPage === 1"
          @click="goToPage(currentPage - 1)"
      >
        上一页
      </button>
      <button
          v-for="page in visiblePages"
          :key="page"
          :class="{ active: currentPage === page }"
          @click="goToPage(page)"
      >
        {{ page }}
      </button>
      <button
          :disabled="currentPage === totalPages"
          @click="goToPage(currentPage + 1)"
      >
        下一页
      </button>
      <button
          :disabled="currentPage === totalPages"
          @click="goToPage(totalPages)"
      >
        最后一页
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElNotification } from 'element-plus';

const router = useRouter();

// 标签分类
const tags = ref([
  '全部',
  '扶贫济困',
  '健康医疗',
  '助老扶幼',
  '文化教育',
  '社会服务',
  '科技环保',
  '灾难救助',
  '专项基金',
]);

// 当前选中的标签
const activeTag = ref('全部');

// 鼠标悬浮的标签
const hoverTag = ref(null);

// 搜索关键字
const searchQuery = ref('');

// 是否触发搜索
const isSearchTriggered = ref(false);

// 当前页码
const currentPage = ref(1);

// 每页显示的项目数量
const itemsPerPage = 5;

// 慈善项目数据
const charityProjects = ref([]);

// 分类映射
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

// 获取分类名称
const getCategoryName = (category) => {
  return categoryMap[category] || '未知分类';
};

// 格式化日期
const formatDate = (timestamp) => {
  if (!timestamp) return ''; // 如果时间戳为空，返回空字符串
  const date = new Date(timestamp); // 将时间戳转换为 Date 对象
  const year = date.getFullYear(); // 获取年份
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 获取月份（补零）
  const day = String(date.getDate()).padStart(2, '0'); // 获取日期（补零）
  return `${year}-${month}-${day}`; // 返回格式化后的日期
};

// 从后端获取项目数据
const fetchProjects = async () => {
  const token = localStorage.getItem('token'); // 从本地存储获取令牌
  if (!token) {
    ElNotification({
      title: '未登录',
      message: '请先登录以访问此功能',
      type: 'warning',
      duration: 3000,
    });
    await router.push('/login-register'); // 跳转到登录界面
    return;
  }

  try {
    const response = await axios.get('/user/projects', {
      headers: {
        Authorization: `Bearer ${token}`, // 在请求头中携带令牌
      },
    });

    if (response.code === 200) {
      charityProjects.value = response.data; // 更新项目数据
    } else {
      ElNotification({
        title: '获取数据失败',
        message: response.msg || '未知错误',
        type: 'error',
        duration: 3000,
      });
    }
  } catch (error) {
    ElNotification({
      title: '请求失败',
      message: error.message || '网络错误',
      type: 'error',
      duration: 3000,
    });
  }
};

// 组件挂载时获取数据
onMounted(() => {
  fetchProjects();
});

// 根据选中的标签和搜索关键字过滤项目
const filteredProjects = computed(() => {
  let filtered = charityProjects.value;

  // 根据标签过滤
  if (activeTag.value !== '全部') {
    filtered = filtered.filter(
        (project) => getCategoryName(project.category) === activeTag.value
    );
  }

  // 根据搜索关键字过滤
  if (isSearchTriggered.value && searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(
        (project) =>
            project.title.toLowerCase().includes(query) ||
            project.details.toLowerCase().includes(query)
    );
  }

  return filtered;
});

// 分页后的项目数据
const paginatedProjects = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredProjects.value.slice(start, end);
});

// 总页数
const totalPages = computed(() => {
  return Math.ceil(filteredProjects.value.length / itemsPerPage);
});

// 可见的页码（最多显示 5 个页码）
const visiblePages = computed(() => {
  const pages = [];
  const maxVisiblePages = 5;
  let startPage = Math.max(1, currentPage.value - Math.floor(maxVisiblePages / 2));
  let endPage = Math.min(totalPages.value, startPage + maxVisiblePages - 1);

  if (endPage - startPage + 1 < maxVisiblePages) {
    startPage = Math.max(1, endPage - maxVisiblePages + 1);
  }

  for (let i = startPage; i <= endPage; i++) {
    pages.push(i);
  }

  return pages;
});

// 设置当前选中的标签
const setActiveTag = (tag) => {
  activeTag.value = tag;
  isSearchTriggered.value = false;
  currentPage.value = 1;
};

// 处理搜索
const handleSearch = () => {
  isSearchTriggered.value = true;
  currentPage.value = 1;
};

// 跳转到指定页码
const goToPage = (page) => {
  currentPage.value = page;
};

// 跳转到项目详情页
const goToProjectDetail = (projectId) => {
  router.push({ path: `/project/${projectId}` });
};

// 跳转到捐赠页
const goToDonationPage = (projectId) => {
  router.push({ path: `/donation/${projectId}` });
};
</script>

<style scoped>
.projects-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

h1 {
  text-align: center;
  margin-bottom: 20px;
  color: #666;
}

/* 标签容器 */
.tags-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ccc;
}

/* 标签分类样式 */
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  justify-content: center;
  margin-bottom: 20px;
}

.tag {
  padding: 8px 16px;
  cursor: pointer;
  color: #666;
  transition: all 0.3s ease;
}

.tag:hover,
.tag.active {
  background-color: #e40000;
  color: #fff;
}

/* 搜索框样式 */
.search-box {
  margin-left: 700px;
  display: flex;
  align-items: center;
  gap: 10px;
  width: 100%;
  max-width: 400px;
  margin-top: 10px;
}

.search-box input {
  flex: 1;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  outline: none;
  transition: border-color 0.3s ease;
}

.search-box input:focus {
  border-color: #e40000;
}

.search-box button {
  padding: 10px 20px;
  background-color: #e40000;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.search-box button:hover {
  background-color: #c40000;
}

/* 项目列表样式 */
.project-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.project-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  border-bottom: 1px solid #ccc;
  transition: all 0.3s ease; /* 添加过渡效果 */
}


/* 鼠标悬停时，图片放大 */
.project-item:hover .project-image {
  transform: scale(1.04); /* 图片放大 1.1 倍 */
  transition: transform 0.3s ease; /* 添加过渡效果 */
}

/* 鼠标悬停时，标题和按钮变为红色 */
.project-item:hover .project-header h2 {
  color: #e40000; /* 标题变为红色 */
}

.project-item:hover .project-header button {
  background-color: #e40000; /* 按钮背景变为红色 */
  color: #fff; /* 按钮文字变为白色 */
}

.project-image {
  width: 380px;
  height: 210px;
  object-fit: cover;
  border-radius: 8px;
  transition: transform 0.3s ease; /* 添加过渡效果 */
  overflow: hidden; /* 防止图片放大时溢出 */
}

.project-info {
  flex: 1;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.project-header h2 {
  margin: 0;
  color: #333;
  transition: color 0.3s ease; /* 添加过渡效果 */
}

.project-header button {
  padding: 8px 16px;
  background-color: #e40000;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.project-header button:hover {
  background-color: #c40000;
}

.project-meta {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 10px;
}

.meta-item {
  display: flex;
  gap: 5px;
  color: #666;
}

.meta-label {
  color: #666;
}

.meta-value {
  color: #666;
}

.project-description {
  color: #666;
  margin-bottom: 10px;
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.pagination button.active {
  background-color: #e40000;
  color: #fff;
  border-color: #e40000;
}

.pagination button:hover {
  background-color: #e40000;
  color: #fff;
}

.pagination button:disabled {
  background-color: #ddd;
  color: #999;
  cursor: not-allowed;
}
</style>
