<template>
  <div class="news-page">
    <!-- 左边侧边栏 -->
    <div class="sidebar">
      <div class="leftNavBar">新闻资讯</div>
      <ul>
        <li
            :class="{ active: currentCategory === '全部' }"
            @click="switchCategory('全部')"
        >
          总会资讯
        </li>
        <li
            v-for="category in categories"
            :key="category.value"
            :class="{ active: currentCategory === category.value }"
            @click="switchCategory(category.value)"
        >
          {{ category.label }}
        </li>
      </ul>
      <div class="leftNavBar">项目推荐</div>
      <ul class="project-list">
        <li
            v-for="project in randomProjects"
            :key="project.id"
            class="project-item"
            @click="goToProjectDetail(project.id)"
        >
          <img :src="project.image" alt="项目图片" class="project-image" />
          <div class="project-title">{{ project.title }}</div>
        </li>
      </ul>
      <div class="wechat-qr">
        <img src="@/assets/wx_user.png" alt="微信二维码" />
        <p>欢迎关注心阳捐赠网官方微信</p>
      </div>
    </div>

    <!-- 右边新闻列表 -->
    <div class="main-content">
      <!-- 搜索栏 -->
      <div class="search-bar">
        <input
            type="text"
            v-model="searchKeyword"
            placeholder="请输入新闻标题关键字"
            class="search-input"
        />
        <button class="search-button" @click="searchNews">查找</button>
      </div>

      <div class="news-list">
        <div
            v-for="newsItem in paginatedNews"
            :key="newsItem.id"
            class="news-item"
            @click="goToNewsDetail(newsItem.id)"
        >
          <img :src="newsItem.coverUrl" alt="新闻封面" class="news-cover" />
          <div class="news-content">
            <div
                class="news-title"
                @mouseover="hoverTitle(newsItem.id)"
                @mouseleave="resetTitle"
                :class="{ hovered: hoveredTitleId === newsItem.id }"
            >
              {{ newsItem.title }}
            </div>
            <div class="news-meta">
              <span class="news-date">{{ formatDate(newsItem.date) }}</span>
              <span class="news-publisher">发布者：{{ newsItem.publisher }}</span>
            </div>
            <p v-html="truncateContent(newsItem.content)"></p>
          </div>
        </div>
      </div>
      <div class="pagination">
        <button @click="goToFirstPage" :disabled="currentPage === 1">首页</button>
        <button @click="prevPage" :disabled="currentPage === 1">上一页</button>
        <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
        <button @click="nextPage" :disabled="currentPage === totalPages">下一页</button>
        <button @click="goToLastPage" :disabled="currentPage === totalPages">尾页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import axios from 'axios';

const router = useRouter();
const token = ref(localStorage.getItem('token'));

// 新闻分类
const categories = [
  { label: "通知公告", value: "通知公告" },
  { label: "媒体报道", value: "媒体报道" },
  { label: "慈善人物", value: "慈善人物" },
];

// 当前选中的分类，默认选中“全部”
const currentCategory = ref("全部");

// 搜索关键字
const searchKeyword = ref("");

// 是否触发搜索
const isSearchTriggered = ref(false);

// 鼠标悬浮的标题ID
const hoveredTitleId = ref(null);

// 新闻数据
const news = ref([]);

// 慈善项目数据
const charityProjects = ref([]);

// 获取新闻列表数据
const fetchNews = async () => {
  try {
    const response = await axios.get('/user/news', {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    });
    news.value = response.data;
  } catch (error) {
    console.error('获取新闻数据失败:', error);
  }
};

// 获取项目推荐数据
const fetchProjects = async () => {
  try {
    const response = await axios.get('/user/projects/recommend', {
      headers: {
        Authorization: `Bearer ${token.value}`
      }
    });
    charityProjects.value = response.data;
  } catch (error) {
    console.error('获取项目数据失败:', error);
  }
};

// 在组件挂载时调用
onMounted(() => {
  fetchNews();
  fetchProjects();
});

// 随机选取 4 个项目
const randomProjects = computed(() => {
  // eslint-disable-next-line vue/no-side-effects-in-computed-properties
  const shuffled = charityProjects.value.sort(() => 0.5 - Math.random());
  return shuffled.slice(0, 4);
});

// 格式化日期
const formatDate = (timestamp) => {
  if (!timestamp) return ''; // 如果时间戳为空，返回空字符串
  const date = new Date(timestamp); // 将时间戳转换为 Date 对象
  const year = date.getFullYear(); // 获取年份
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 获取月份（补零）
  const day = String(date.getDate()).padStart(2, '0'); // 获取日期（补零）
  return `${year}-${month}-${day}`; // 返回格式化后的日期
};

// 根据当前分类和搜索关键字过滤新闻数据
const filteredNews = computed(() => {
  let filtered = news.value;

  // 根据分类过滤
  if (currentCategory.value !== "全部") {
    // 将分类名称映射为对应的 type 值
    const categoryToTypeMap = {
      "通知公告": 1,
      "媒体报道": 2,
      "慈善人物": 3,
    };
    const type = categoryToTypeMap[currentCategory.value];
    filtered = filtered.filter((item) => item.type === type);
  }

  // 根据搜索关键字过滤（仅在点击查找按钮后触发）
  if (isSearchTriggered.value && searchKeyword.value) {
    filtered = filtered.filter((item) =>
        item.title.includes(searchKeyword.value)
    );
  }

  return filtered;
});

// 分页相关逻辑
const itemsPerPage = 7;
const currentPage = ref(1);

const totalPages = computed(() =>
    Math.ceil(filteredNews.value.length / itemsPerPage)
);

const paginatedNews = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredNews.value.slice(start, end);
});

// 上一页
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
  }
};

// 下一页
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++;
  }
};

// 首页
const goToFirstPage = () => {
  currentPage.value = 1;
};

// 尾页
const goToLastPage = () => {
  currentPage.value = totalPages.value;
};

// 切换分类
const switchCategory = (category) => {
  currentCategory.value = category;
  currentPage.value = 1; // 切换分类时重置到第一页
  isSearchTriggered.value = false; // 重置搜索状态
};

// 查找新闻
const searchNews = () => {
  isSearchTriggered.value = true; // 触发搜索
  currentPage.value = 1; // 重置到第一页
};

// 鼠标悬浮标题
const hoverTitle = (id) => {
  hoveredTitleId.value = id;
};

// 重置标题样式
const resetTitle = () => {
  hoveredTitleId.value = null;
};

// 截取内容
const truncateContent = (content) => {
  const text = content.replace(/<[^>]+>/g, ""); // 去除HTML标签
  if (text.length > 100) {
    return text.slice(0, 100) + "...";
  }
  return text;
};

// 跳转到项目详情页
const goToProjectDetail = (projectId) => {
  router.push({ name: 'ProjectDetail', params: { id: projectId } });
};

// 跳转到新闻详情页
const goToNewsDetail = (newsId) => {
  router.push({ name: 'NewsDetail', params: { id: newsId } });
};
</script>




<style scoped>
.news-page {
  margin-left: 10%;
  width: 80%;
  display: flex;
  gap: 20px;
  padding: 20px;
}

/* 左边侧边栏样式 */
.sidebar {
  text-align: center;
  width: 250px;
  padding: 20px;
  border-right: 1px solid #ddd;
}

.leftNavBar {
  width: 210px;
  height: 42px;
  line-height: 42px;
  background-color: #dd2020;
  border-radius: 4px;
  text-align: center;
  font-size: 16px;
  color: #fff;
  margin-left: 20px;
}

.sidebar ul {
  list-style: none;
  padding: 0;
  margin-bottom: 20px;
}

.sidebar ul li {
  margin-left: 35px;
  width: 150px;
  padding: 15px;
  color: #333;
  font-size: 14px;
  border-bottom: solid 1px #e5e5e5;
  cursor: pointer;
  transition: color 0.3s ease;
}

.sidebar ul li:hover {
  color: #dd2020;
}

.sidebar ul li.active {
  color: #dd2020; /* 选中时字体变红 */
  font-weight: bold; /* 可选：加粗选中项 */
}

/* 项目推荐样式 */
.project-list {
  list-style: none;
  padding: 0;
  margin-bottom: 20px;
}

.project-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.project-image {
  width: 80px;
  object-fit: cover;
}

.project-title {
  font-size: 12px;
  color: #333;
}

.wechat-qr {
  margin-left: 33px;
  width: 180px;
  height: 205px;
  border: 1px solid #cccccc;
  text-align: center;
}

.wechat-qr img {
  width: 170px;
  height: 170px;
}

.wechat-qr p {
  color: #666;
  font-size: 12px;
}

/* 右边新闻列表样式 */
.main-content {
  flex: 1;
}

/* 搜索栏样式 */
.search-bar {
  width: 100%;
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.search-input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 300px;
  transition: border-color 0.3s ease;
}

/* 搜索框获取焦点时的样式 */
.search-input:focus {
  border-color: #dd2020; /* 边框变红 */
  outline: none; /* 去除默认的蓝色外边框 */
}

.search-button {
  padding: 10px 20px;
  background-color: #dd2020;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.search-button:hover {
  background-color: #c40000;
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.news-item {
  display: flex;
  gap: 20px;
  border-bottom: 1px solid #ddd;
  border-radius: 8px;
  padding-bottom: 10px;
}

.news-cover {
  width: 220px;
  object-fit: cover;
}

.news-content {
  flex: 1;
}

.news-title {
  font-size: 20px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.news-title.hovered {
  color: #dd2020; /* 悬浮时标题变红 */
}

.news-meta {
  display: flex;
  margin-bottom: 10px;
  gap: 10px;
  color: #999;
  margin-top: 10px;
  font-size: 14px;
}

.news-date {
  align-self: center;
}

.news-content p {
  color: #666;
  margin-bottom: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 16px;
  background-color: #e40000;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.pagination button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.pagination button:hover:not(:disabled) {
  background-color: #c40000;
}
</style>
