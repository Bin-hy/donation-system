<template>
  <div>
    <!-- 上方：项目详情 -->
    <div class="project-detail">
      <!-- 左边：项目图片 -->
      <div class="left-section">
        <img :src="project.image" :alt="project.title" class="project-image" />
        <!-- 状态标签 -->
        <div class="status-tag" :class="project.status === '进行中' ? 'ongoing' : 'ended'">
          {{ project.status }}
        </div>
      </div>

      <!-- 右边：项目信息 -->
      <div class="right-section">
        <!-- 项目名称 -->
        <h1 class="project-title">{{ project.title }}</h1>

        <!-- 备案编号 -->
        <div class="info-item">
          <span class="info-label">备案编号：</span>
          <span class="info-value">{{ project.recordNumber }}</span>
        </div>

        <!-- 目标金额 -->
        <div class="info-item">
          <span class="info-label">目标金额：</span>
          <span class="info-value" style="font-style: italic;font-size: 20px">￥{{ project.targetAmount }} 元</span>
        </div>

        <!-- 已筹金额（含线下） -->
        <div class="info-item">
          <span class="info-label">已筹金额（含线下）：</span>
          <span class="info-value">￥{{ project.raisedAmount }} 元</span>
        </div>

        <!-- 爱心次数 -->
        <div class="info-item">
          <span class="info-label">爱心次数：</span>
          <span class="info-value">{{ project.donationCount }} 次</span>
        </div>

        <!-- 起止日期 -->
        <div class="info-item">
          <span class="info-label">起止日期：</span>
          <span class="info-value">{{ formatDate(project.startDate) }} 至 {{ formatDate(project.endDate) }}</span>
        </div>

        <!-- 我要捐赠按钮 -->
        <div class="donate-button-container">
          <el-button
              type="danger"
              class="donate-button"
              :disabled="project.status === '已结束'"
              @click="goToDonationPage"
          >
            我要捐赠
          </el-button>
        </div>
      </div>
    </div>

    <!-- 下方：左右分栏模块 -->
    <div class="bottom-section">
      <!-- 左边：切换模块 -->
      <div class="left-module">
        <!-- 切换按钮 -->
        <div class="tab-buttons">
          <el-button
              v-for="tab in tabs"
              :key="tab.name"
              :class="{ active: activeTab === tab.name }"
              @click="handleTabChange(tab.name)"
          >
            {{ tab.label }}
          </el-button>
        </div>

        <!-- 切换内容 -->
        <div class="tab-content">
          <!-- 基本信息 -->
          <div v-show="activeTab === 'basic'">
            <!-- 项目详情 -->
            <div class="info-section">
              <h2>项目详情</h2>
              <p style="font-family: 仿宋,serif;">{{ project.details }}</p>
            </div>

            <!-- 慈善机构信息 -->
            <div class="info-section" v-if="project.orgInfo">
              <h2>慈善机构信息</h2>
              <ul class="info-list">
                <li><strong>发起机构：</strong>{{ project.orgInfo.initiator }}</li>
                <li><strong>善款接收机构：</strong>{{ project.orgInfo.receiver }}</li>
                <li><strong>执行机构：</strong>{{ project.orgInfo.executor }}</li>
              </ul>
            </div>

            <!-- 项目信息 -->
            <div class="info-section" v-if="project.projectInfo">
              <h2>项目信息</h2>
              <ul class="info-list">
                <li><strong>募捐目的：</strong>{{ project.projectInfo.purpose }}</li>
                <li><strong>募捐成本：</strong>{{ project.projectInfo.cost }}</li>
                <li><strong>剩余财产处理方案：</strong>{{ project.projectInfo.surplusPlan }}</li>
                <li><strong>发票开具方式：</strong>{{ project.projectInfo.invoiceMethod }}</li>
                <li><strong>联系方式：</strong>{{ project.projectInfo.contact }}</li>
                <li><strong>募得款物用途：</strong>{{ project.projectInfo.fundUsage }}</li>
                <li><strong>受益人：</strong>{{ project.projectInfo.beneficiaries }}</li>
                <li><strong>活动负责人：</strong>{{ project.projectInfo.leader }}</li>
              </ul>
            </div>
          </div>

          <!-- 收支明细 -->
          <div v-show="activeTab === 'finance'">
            <!-- 搜索表单 -->
            <el-form :model="searchForm" label-width="80px" class="search-form">
              <el-row :gutter="20">
                <el-col :span="6">
                  <el-form-item label="昵称">
                    <el-input v-model="searchForm.username" placeholder="请输入昵称" clearable />
                  </el-form-item>
                </el-col>
                <el-col :span="6">
                  <el-form-item label="支付方式">
                    <el-select v-model="searchForm.paymentMethod" placeholder="请选择支付方式" clearable>
                      <el-option label="全部" value="" />
                      <el-option label="支付宝" value="0" />
                      <el-option label="微信" value="1" />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="9">
                  <el-form-item label="时间范围">
                    <el-date-picker
                        v-model="searchForm.timeRange"
                        type="datetimerange"
                        range-separator="至"
                        start-placeholder="开始时间"
                        end-placeholder="结束时间"
                        value-format="YYYY-MM-DD HH:mm:ss"
                    />
                  </el-form-item>
                </el-col>
                <el-col :span="3">
                  <el-button style="background-color: #ff0000;color: white;margin-left: 16px;" @click="handleSearch">搜索</el-button>
                </el-col>
              </el-row>
            </el-form>

            <!-- 表格 -->
            <el-table
                v-if="financeList.length > 0"
                :data="paginatedFinanceList"
                border
                :header-cell-style="{background:'rgb(245, 247, 250)',color:'#666666'}"
                style="width: 99.5%;text-align: center"
                size="large"
            >
              <el-table-column prop="username" label="爱心人士" width="100" align="center"/>
              <el-table-column prop="amount" label="金额" width="70" align="center"/>
              <el-table-column prop="time" label="时间" width="180" align="center">
                <template #default="{ row }">
                  {{ formatDate(row.time) }}
                </template>
              </el-table-column>
              <el-table-column prop="category" label="分类" width="120" align="center"/>
              <el-table-column prop="paymentMethod" label="支付方式" width="120" align="center">
                <template #default="{ row }">
                  {{ row.paymentMethod === 0 ? '支付宝' : '微信' }}
                </template>
              </el-table-column>
              <el-table-column label="捐赠是否来自境外" width="150" align="center">
                <template #default="{ row }">
                  <el-tag :type="row.isForeign ? 'danger' : 'success'">
                    {{ row.isForeign ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>

            <!-- 分页 -->
            <el-pagination
                v-if="financeList.length > 0"
                class="pagination"
                :current-page="currentPage"
                :page-size="pageSize"
                :total="financeList.length"
                layout="prev, pager, next, jumper, ->, total"
                @current-change="currentPage = $event"
            />
          </div>

          <!-- 投诉反馈 -->
          <div v-show="activeTab === 'feedback'">
            <!-- 表单区域 -->
            <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef">
              <!-- 提示信息 -->
              <div class="feedback-tips">
                <p>温馨提示：</p>
                <ul>
                  <li>请确保填写的信息真实有效，以便我们及时与您联系。</li>
                  <li>举报内容需具体、清晰，避免使用模糊或不当语言。</li>
                  <li>我们将在收到举报后的 3 个工作日内处理并反馈结果。</li>
                </ul>
              </div>
              <!-- 举报人姓名 -->
              <el-form-item label="举报人姓名" prop="name" width="0px">
                <el-input
                    v-model="feedbackForm.name"
                    placeholder="请输入您的真实姓名"
                    clearable
                ></el-input>
              </el-form-item>

              <!-- 举报人联系方式 -->
              <el-form-item label="联系方式" prop="phone" width="0px" class="mix">
                <el-input
                    v-model="feedbackForm.phone"
                    placeholder="请输入您的手机号或邮箱"
                    clearable
                ></el-input>
              </el-form-item>

              <!-- 举报内容 -->
              <el-form-item label="举报内容" prop="content" width="0px">
                <el-input
                    type="textarea"
                    v-model="feedbackForm.content"
                    placeholder="请详细描述您的问题或建议（最多200字）"
                    maxlength="200"
                    show-word-limit
                    :rows="4"
                    clearable
                ></el-input>
              </el-form-item>

              <!-- 举报按钮 -->
              <el-form-item style="margin-left: 360px">
                <el-button type="danger" @click="submitFeedback">提交举报</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 项目留言 -->
          <div v-show="activeTab === 'message'">
            <!-- 留言列表 -->
            <div class="message-list">
              <div v-for="(message, index) in paginatedMessages" :key="index" class="message-item">
                <img :src="message.reviewerAvatar" alt="头像" class="message-avatar" />
                <div class="message-content">
                  <div class="message-info">
                    <span class="message-username">{{ message.reviewer }}</span>
                    <span class="message-time">{{ message.createdAt }}</span>
                  </div>
                  <p class="message-text">{{ message.review }}</p>
                </div>
              </div>
            </div>

            <!-- 分页 -->
            <el-pagination
                class="message-pagination"
                :current-page="messageCurrentPage"
                :page-size="messagePageSize"
                :total="messages.length"
                layout="prev, pager, next"
                @current-change="messageCurrentPage = $event"
            />

            <!-- 留言输入框 -->
            <div class="message-input-container">
              <el-input
                  v-model="newMessageText"
                  type="textarea"
                  placeholder="请输入您的留言（最多20字）"
                  :rows="2"
                  maxlength="20"
                  show-word-limit
                  class="message-input"
              ></el-input>
              <el-button type="danger" @click="submitMessage" class="submit-button">提交</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 右边：推荐模块 -->
      <div class="right-module">
        <!-- 项目推荐 -->
        <div class="project-recommendation">
          <h2>项目推荐</h2>
          <div class="project-list">
            <div
                v-for="item in recommendedProjects"
                :key="item.id"
                class="project-item"
                @click="goToProjectDetail(item.id)"
            >
              <img :src="item.image" :alt="item.title" class="project-thumbnail" />
              <div class="project-info">
                <h3 class="project-name">{{ item.title }}</h3>
              </div>
            </div>
          </div>
        </div>

        <!-- 新闻推荐 -->
        <div class="news-recommendation">
          <h2>新闻推荐</h2>
          <ul class="news-list">
            <li
                v-for="(news, index) in newsList"
                :key="index"
                class="news-item"
                @click="goToNewsDetail(news.id)"
            >
              {{ news.title }}
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import axios from 'axios';

const route = useRoute();
const router = useRouter();

// 从 localStorage 中获取 token
const token = localStorage.getItem('token');

// 配置 axios 请求头
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;

// 跳转到捐赠页面
const goToDonationPage = () => {
  router.push({
    name: 'DonationPage',
    params: { id: project.value.id },
  });
};

// 项目数据
const project = ref({
  id: '',
  image: '',
  title: '',
  recordNumber: '',
  targetAmount: '',
  raisedAmount: '',
  donationCount: '',
  startDate: '',
  endDate: '',
  status: '',
  details: '',
  orgInfo: {
    initiator: '',
    receiver: '',
    executor: '',
  },
  projectInfo: {
    purpose: '',
    cost: '',
    surplusPlan: '',
    invoiceMethod: '',
    contact: '',
    fundUsage: '',
    beneficiaries: '',
    leader: '',
  },
});

// 切换模块
const tabs = [
  { name: 'basic', label: '基本信息' },
  { name: 'finance', label: '收支明细' },
  { name: 'message', label: '项目留言' },
  { name: 'feedback', label: '投诉反馈' },

];
const activeTab = ref('basic');

// 处理 tab 切换
const handleTabChange = (tabName) => {
  try {
    activeTab.value = tabName;
  } catch (error) {
    console.error('切换标签失败:', error);
  }
};


let globalProjectName = null;

// 获取项目详情
const fetchProjectDetail = async (id) => {
  try {
    const response = await axios.get(`/user/projects/${id}`);
    if (response.data && response.code === 200) {
      const data = response.data;
      data.status = checkProjectStatus(data.startDate, data.endDate);
      project.value = data;

      // 设置全局项目名称
      globalProjectName = data.title;
      console.log('项目名称:', globalProjectName); // 打印项目名称

      // 调用 fetchFinanceList，这里假设它只需要项目名称作为参数
      if (globalProjectName) {
        await fetchFinanceList({ projectName: globalProjectName }); // 传递项目名称
      } else {
        console.error('项目名称为空，无法获取收支明细');
      }
    } else {
      ElMessage.error('获取项目详情失败');
    }
  } catch (error) {
    console.error('请求失败:', error);
    handleApiError(error); // 处理 API 错误的一个通用函数
  }
};

// 处理 API 错误的通用函数
const handleApiError = (error) => {
  if (error.response && error.response.status === 401) {
    ElMessage.error('登录已过期，请重新登录');
    router.push('/login');
  } else {
    ElMessage.error('请求失败，请检查网络');
  }
};

// 判断项目状态
const checkProjectStatus = (startDate, endDate) => {
  const currentDate = new Date();
  const start = new Date(startDate);
  const end = new Date(endDate);
  return currentDate >= start && currentDate <= end ? '进行中' : '已结束';
};

const goToProjectDetail = (projectId) => {
  router.push({
    name: 'ProjectDetail',
    params: { id: projectId },
  }).then(() => {
    router.go(0); // 强制刷新页面
  });
};


const goToNewsDetail = (newsId) => {
  router.push({
    name: 'NewsDetail', // 替换为新闻详情页的路由名称
    params: { id: newsId }, // 传递新闻 ID
  });
};

// 获取推荐项目
const recommendedProjects = ref([]);
const fetchRecommendedProjects = async () => {
  try {
    const response = await axios.get('/user/projects/recommend');
    if (response.data && response.code === 200) {
      recommendedProjects.value = response.data
          .sort(() => Math.random() - 0.5) // 随机排序
          .slice(0, 4); // 截取前 4 个
    }
  } catch (error) {
    console.error('获取推荐项目失败:', error);
  }
};

// 获取新闻推荐
const newsList = ref([]);
const fetchNewsList = async () => {
  try {
    const response = await axios.get('/user/news/recommend');
    if (response.data && response.code === 200) {
      newsList.value = response.data
          .sort(() => Math.random() - 0.5) // 随机排序
          .slice(0, 4); // 截取前 4 个
    }
  } catch (error) {
    console.error('获取新闻推荐失败:', error);
  }
};

// 获取收支明细
const financeList = ref([]);
// 获取收支明细
const fetchFinanceList = async (params = {}) => {
  try {
    // 打印请求参数
    console.log('请求参数:', params);

    const response = await axios.get('/user/finance-records', { params });
    if (response.data && response.code === 200) {
      financeList.value = response.data;
    } else {
      console.error('获取收支明细失败:', response.msg || '未知错误');
    }
  } catch (error) {
    console.error('获取收支明细失败:', error);
  }
};

// 分页相关
const pageSize = 10;
const currentPage = ref(1);
const paginatedFinanceList = computed(() => {
  const start = (currentPage.value - 1) * pageSize;
  const end = start + pageSize;
  return financeList.value.slice(start, end);
});

// 搜索表单
const searchForm = ref({
  username: '',
  paymentMethod: '',
  timeRange: [],
});

// 搜索方法
const handleSearch = () => {
  const projectName= globalProjectName;
  const { username, paymentMethod, timeRange } = searchForm.value;
  const params = {
    projectName, // 加入 projectName
    username,
    paymentMethod: paymentMethod ? parseInt(paymentMethod) : undefined,
    startDate: timeRange && timeRange[0] ? formatDate(timeRange[0]) : undefined,
    endDate: timeRange && timeRange[1] ? formatDate(timeRange[1]) : undefined,
  };
  console.log('Search Params:', params); // 打印参数
  fetchFinanceList(params);
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

// 表单数据
const feedbackForm = ref({
  name: '',
  phone: '',
  content: '',
});

// 表单验证规则
const feedbackRules = {
  name: [
    { required: true, message: '请输入举报人姓名', trigger: 'blur' },
    { min: 2, max: 10, message: '姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系方式', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$|^\w+@\w+\.\w+$/, message: '请输入正确的手机号或邮箱', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入举报内容', trigger: 'blur' },
    { min: 10, max: 200, message: '举报内容长度在 10 到 200 个字符', trigger: 'blur' }
  ]
};

// 表单引用
const feedbackFormRef = ref(null);

// 提交举报
const submitFeedback = () => {
  feedbackFormRef.value.validate((valid) => {
    if (valid) {
      console.log('提交的举报信息：', feedbackForm.value);

      // 获取 token
      const token = localStorage.getItem('token');
      if (!token) {
        ElMessage.error('用户未登录，请先登录');
        return;
      }

      // 获取项目 ID
      const projectId = project.value.id;

      // 构造请求数据，包含项目 ID
      const requestData = {
        ...feedbackForm.value, // 包含表单数据
        projectId, // 添加项目 ID
      };

      // 发送 HTTP 请求
      axios.post('/user/feedback', requestData, {
        headers: {
          Authorization: `Bearer ${token}`, // 携带 token
        },
      })
          .then(response => {
            if (response.code === 200) {
              ElMessage.success('举报提交成功！我们将尽快处理您的反馈。');
              feedbackFormRef.value.resetFields(); // 清空表单
            } else {
              ElMessage.error(response.msg || '提交失败，请稍后重试');
            }
          })
          .catch(error => {
            console.error('提交失败:', error);
            ElMessage.error('提交失败，请稍后重试');
          });
    } else {
      ElMessage.error('请填写完整信息');
    }
  });
};


// 留言数据
const messages = ref([]);

// 新留言内容
const newMessageText = ref('');

const submitMessage = async () => {
  if (newMessageText.value.trim()) {
    try {
      // 从本地存储或状态管理中获取 token
      const token = localStorage.getItem('token'); // 假设 token 存储在 localStorage 中
      // 提交留言到服务器
      const response = await axios.post(
          '/user/project/reviews',
          {
            projectId: project.value.id, // 项目 ID
            review: newMessageText.value, // 留言内容
          },
          {
            headers: {
              Authorization: `Bearer ${token}`, // 将 token 放在请求头中
            },
          }
      );

      // 如果提交成功
      if (response.data && response.code === 200) {
        ElMessage.success('留言提交成功！'); // 提示提交成功
        newMessageText.value = ''; // 清空输入框
        fetchMessages(project.value.id); // 重新获取留言列表
      } else {
        ElMessage.error('留言提交失败，请重试'); // 提示提交失败
      }
    } catch (error) {
      console.error('提交留言失败:', error);
      ElMessage.error('留言提交失败，请重试'); // 提示提交失败
    }
  } else {
    ElMessage.warning('请输入留言内容'); // 提示输入内容为空
  }
};

const fetchMessages = async (projectId) => {
  try {
    const response = await axios.get(`/user/messages/${projectId}`);
    if (response.data && response.code === 200) {
      // 处理时间数据
      const formattedMessages = response.data.map(message => {
        const [year, month, day, hour, minute] = message.createdAt;
        const createdAtDate = new Date(year, month - 1, day, hour, minute);
        return {
          ...message,
          createdAt: createdAtDate.toLocaleString() // 转换为本地时间字符串
        };
      });

      messages.value = formattedMessages;
    }
  } catch (error) {
    console.error('获取留言失败:', error);
  }
};

// 留言分页相关
const messagePageSize = 5; // 每页显示 5 条留言
const messageCurrentPage = ref(1); // 当前页码

// 分页后的留言数据
const paginatedMessages = computed(() => {
  const start = (messageCurrentPage.value - 1) * messagePageSize;
  const end = start + messagePageSize;
  return messages.value.slice(start, end);
});

// 在组件挂载时获取数据
onMounted(() => {
  const projectId = route.params.id;
  fetchProjectDetail(projectId);
  fetchRecommendedProjects();
  fetchNewsList();
  fetchFinanceList(); // 初始化获取收支明细
  fetchMessages(projectId);
});
</script>

<style scoped>
/* 上方：项目详情 */
.project-detail {
  display: flex;
  max-width: 1200px;
  margin: 30px auto;
  gap: 20px;
  background-color: #70777d;
  color: #fff;
  border-radius: 8px;
}

.left-section {
  flex: 1;
  position: relative;
}

.project-image {
  width: 100%;
  height: 100%;
  max-height: 500px;
  border-radius: 8px;
  object-fit: cover;
}

.status-tag {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 14px;
  font-weight: bold;
  color: #fff;
}

.status-tag.ongoing {
  background-color: #ff0000;
}

.status-tag.ended {
  background-color: #000;
}

.right-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-around;
}

.project-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.info-item {
  margin-bottom: 16px;
  font-size: 16px;
}

.info-label {
  font-weight: bold;
}

.info-value {
  color: #ffffff;
}

.donate-button-container {
  display: flex;
  justify-content: flex-start;
}

.donate-button {
  border-radius: 20px;
  background-color: #ff0000;
  border: none;
  color: #fff;
  width: 200px;
  height: 40px;
  font-size: 14px;
  padding: 10px 0;
}

.donate-button:hover {
  background-color: #cc0000;
}

.donate-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 下方：左右分栏模块 */
.bottom-section {
  display: flex;
  max-width: 1200px;
  margin: 30px auto;
  gap: 20px;
}

.left-module {
  flex: 2;
  border: 1px solid #e0e0e0; /* 添加边框 */
  border-radius: 8px; /* 圆角 */
  overflow: hidden; /* 防止内容溢出 */
}

/* 切换按钮容器样式 */
.tab-buttons {
  display: flex;
  background-color: #f5f5f5; /* 灰色背景 */
  border-right: 1px solid #e0e0e0; /* 右边框 */
}

/* 按钮样式 */
.tab-buttons .el-button {
  width: 97.6px; /* 固定宽度 */
  height: 40px; /* 固定高度 */
  margin: 0; /* 去掉间距 */
  border-radius: 0; /* 去掉圆角 */
  background-color: #f5f5f5; /* 未选中时灰色背景 */
  color: #909399; /* 未选中时字体颜色 */
  border: none; /* 去掉默认边框 */
  border-bottom: 2px solid transparent; /* 底部边框透明 */
}

/* 选中按钮样式 */
.tab-buttons .el-button.active {
  background-color: #fff; /* 选中时白色背景 */
  color: #000; /* 选中时黑色字体 */
  border-bottom: 2px solid #fff; /* 选中时白色底部边框 */
}

/* 切换内容样式 */
.tab-content {
  padding: 20px;
  background-color: #fff; /* 白色背景 */
  border-radius: 0 0 8px 8px; /* 底部圆角 */
  color: #333;
}

.tab-content h2 {
  font-size: 20px;
  margin-bottom: 10px;
}

.tab-content p {
  font-size: 16px;
  line-height: 1.6;
}

.right-module {
  flex: 1;
}

.project-recommendation,
.news-recommendation {
  margin-bottom: 20px;
}

.project-recommendation h2,
.news-recommendation h2 {
  font-size: 20px;
  margin-bottom: 20px;
  color: #333;
  margin-top: -1px;
}

.project-list {
  display: grid;
  grid-template-columns: repeat(1, 1fr);
  gap: 10px;
}

.project-item {
  display: flex;
  align-items: center;
  gap: 10px;
  border-radius: 8px;
  cursor: pointer; /* 添加手指效果 */
  margin-bottom: 20px;
}

.project-thumbnail {
  width: 140px;
  height: 100px;
  border-radius: 8px;
}

.project-name {
  font-size: 16px;
  margin: 0;
  color: #333;
}

.news-list {
  list-style: none;
  padding: 0;
}

.news-item {
  border-radius: 8px;
  margin-bottom: 20px;
  cursor: pointer;
  color: #6e76f9;
}

/* 基本信息模块样式 */
.info-section {
  margin-bottom: 30px;
}

.info-section h2 {
  font-size: 20px;
  margin-bottom: 10px;
  color: #333;
}

.info-section p {
  font-size: 20px;
  line-height: 1.6;
  color: #666;
}

.info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.info-list li {
  margin-bottom: 10px;
  font-size: 16px;
  line-height: 1.6;
  color: #666;
}

.info-list strong {
  color: #333;
  font-weight: bold;
}

/* 搜索表单样式 */
.search-form {
  margin-top: 20px;
  margin-left: -40px;
  margin-bottom: 20px;
}

/* 分页样式 */
.pagination {
  margin-top: 20px;
  text-align: right;
}

/* 投诉反馈模块样式 */
.feedback-tips {
  margin-bottom: 50px;
  padding: 5px;
  background-color: #f9f9f9;
  border-radius: 4px;
  color: #666;
}

.feedback-tips p {
  margin-top: -2px;
  font-weight: bold;
  margin-bottom: 10px;
}

.feedback-tips ul {
  padding-left: 20px;
  margin: 0;
}

.feedback-tips li {
  margin-bottom: 5px;
  font-size: 14px;
}

:deep(.el-textarea__inner){
  width: 655px;
  margin-left: 13px;
}

:deep(.mix .el-input__wrapper){
  margin-left: 13px;
}

/* 留言列表样式 */
.message-list {
  max-height: 600px;
  overflow-y: auto;
  margin-bottom: 20px;
}

.message-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20px;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
}

.message-content {
  flex: 1;
}

.message-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 5px;
}

.message-username {
  font-weight: bold;
  color: #333;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-text {
  font-size: 14px;
  color: #666;
  line-height: 1.5;
}

/* 分页样式 */
.message-pagination {
  margin-top: 20px;
  text-align: center;
}

/* 留言输入框样式 */
.message-input-container {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.message-input {
  flex: 1;
}

.submit-button {
  width: 80px;
  height: 40px;
}
</style>
