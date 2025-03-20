<template>
  <div class="news-container">
    <!-- 新闻列表模块 -->
    <div class="news-list">
      <!-- 查询表单 -->
      <div class="query-form-container">
        <el-form :inline="true" :model="queryForm" class="query-form">
          <el-row :gutter="20">
            <!-- 左侧表单内容 -->
            <el-col :span="18">
              <el-row :gutter="10">
                <!-- 新闻标题 -->
                <el-col :span="8">
                  <el-form-item label="新闻标题">
                    <el-input
                        v-model="queryForm.title"
                        placeholder="请输入新闻标题"
                        clearable
                    />
                  </el-form-item>
                </el-col>

                <!-- 发布者 -->
                <el-col :span="8">
                  <el-form-item label="发布者">
                    <el-input
                        v-model="queryForm.publisher"
                        placeholder="请输入发布者"
                        clearable
                    />
                  </el-form-item>
                </el-col>

                <!-- 时间范围 -->
                <el-col :span="8">
                  <el-form-item label="发布日期">
                    <el-date-picker
                        v-model="queryForm.dateRange"
                        type="daterange"
                        range-separator="至"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期"
                        value-format="YYYY-MM-DD"
                    />
                  </el-form-item>
                </el-col>
              </el-row>
            </el-col>

            <!-- 右侧按钮 -->
            <el-col :span="6" class="button-group">
              <el-form-item>
                <el-button type="primary" @click="handleQuery">查询</el-button>
                <el-button @click="handleReset">重置</el-button>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 操作栏 -->
      <div class="action-bar">
        <!-- 添加按钮 -->
        <el-button type="primary" @click="handleAdd">添加</el-button>
        <!-- 删除按钮 -->
        <el-button
            type="danger"
            :disabled="selectedNews.length === 0"
            @click="handleBatchDelete"
        >
          一键删除
        </el-button>
      </div>

      <!-- 新闻表格 -->
      <el-table
          :data="paginatedNews"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
          :header-cell-style="{ background: 'var(--el-bg-color-page)', color: 'var(--el-text-color-primary)' }"
      >
        <!-- 多选列 -->
        <el-table-column type="selection" width="55" />

        <!-- 新闻标题列 -->
        <el-table-column prop="title" label="新闻标题" width="300" />



        <!-- 封面图片列 -->
        <el-table-column label="封面" width="250">
          <template #default="{ row }">
            <el-image
                style="width: 200px; height: 120px; border-radius: 4px"
                :src="row.coverUrl"
                fit="cover"
            />
          </template>
        </el-table-column>

        <el-table-column label="是否推荐" width="120">
          <template #default="{ row }">
            <el-switch
                inline-prompt
                active-text="是"
                inactive-text="否"
                v-model="row.isRecommended"
                :active-value="1"
                :inactive-value="0"
                :disabled="
        row.isRecommended === 0 &&
        newsList.filter((news) => news.isRecommended === 1).length > 8
      "
                @change="handleRecommendChange(row)"
            />
          </template>
        </el-table-column>

        <!-- 发布日期列 -->
        <el-table-column prop="date" label="发布日期" width="150">
          <template #default="{ row }">
            {{ formatDate(row.date) }}
          </template>
        </el-table-column>

        <!-- 类型列 -->
        <el-table-column prop="type" label="类型" width="150">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)">
              {{ getTypeLabel(row.type) }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 发布者列 -->
        <el-table-column prop="publisher" label="发布者" width="150" />



        <!-- 操作列（固定在右侧） -->
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="default" @click="handleView(row)">预览</el-button>
            <el-button type="warning" size="default" @click="handleEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination">
        <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="newsList.length"
            :page-size="pageSize"
            :current-page="currentPage"
            :page-sizes="[5, 10, 20, 50]"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 预览弹窗 -->
    <el-dialog
        v-if="currentNews"
        v-model="viewDialogVisible"
        title="新闻详情"
        width="50%"
        center
    >
      <div>
        <!-- 标题居中 -->
        <h2 style="text-align: center; margin-bottom: 20px;">{{ currentNews.title }}</h2>

        <!-- 发布日期和发布者平行显示 -->
        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :span="12">
            <div class="info-item" style="margin-left: 210px;">
              <span class="label">发布日期：</span>
              <span class="value">{{ formatDate(currentNews.date) }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="info-item">
              <span class="label">发布者：</span>
              <span class="value">{{ currentNews.publisher }}</span>
            </div>
          </el-col>
        </el-row>

        <!-- 新闻内容 -->
        <div class="info-section">
          <div v-html="currentNews.content"></div>
        </div>
      </div>
    </el-dialog>

    <!-- 编辑/添加抽屉 -->
    <el-drawer
        v-model="drawerVisible"
        :title="isEdit ? '编辑新闻' : '添加新闻'"
        direction="rtl"
        size="40%"
    >
      <el-form :model="formData" label-width="120px">
        <el-form-item label="新闻标题">
          <el-input v-model="formData.title" placeholder="请输入新闻标题" />
        </el-form-item>
        <el-form-item label="发布日期">
          <el-date-picker
              v-model="formData.date"
              type="date"
              placeholder="选择发布日期"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="发布者">
          <el-input v-model="formData.publisher" placeholder="请输入发布者" />
        </el-form-item>
        <el-form-item label="封面图片">
          <div class="image-upload">
            <!-- 图片预览 -->
            <el-image
                v-if="formData.coverUrl"
                style="width: 150px; height: 100px; border-radius: 4px; cursor: pointer"
                :src="formData.coverUrl"
                fit="cover"
                @click="triggerFileInput"
            />
            <!-- 默认占位图 -->
            <div
                v-else
                class="image-placeholder"
                @click="triggerFileInput"
            >
              <span>点击上传封面</span>
            </div>
            <!-- 文件选择按钮 -->
            <input
                type="file"
                ref="fileInput"
                style="display: none"
                @change="handleFileChange"
                accept="image/*"
            />
          </div>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="formData.type" placeholder="请选择类型">
            <el-option label="通知公告" :value="1" />
            <el-option label="媒体报道" :value="2" />
            <el-option label="慈善人物" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="新闻内容">
          <quill-editor
              v-model:content="formData.content"
              contentType="html"
              theme="snow"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存' : '添加' }}</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import axios from 'axios';
import { QuillEditor } from '@vueup/vue-quill';
import '@vueup/vue-quill/dist/vue-quill.snow.css';

// 新闻列表数据
const newsList = ref([]);

// 查询表单数据
const queryForm = ref({
  title: '', // 新闻标题
  publisher: '', // 发布者
  dateRange: [], // 发布日期范围
});

// 分页相关状态
const currentPage = ref(1); // 当前页码
const pageSize = ref(5); // 每页显示条数

// 选中的新闻
const selectedNews = ref([]);

// 预览弹窗相关状态
const viewDialogVisible = ref(false);
const currentNews = ref(null);

// 编辑/添加抽屉相关状态
const drawerVisible = ref(false);
const isEdit = ref(false); // 是否为编辑模式
const formData = ref({
  title: '',
  date: '',
  publisher: '',
  coverUrl: '',
  type: 1, // 1-通知公告，2-媒体报道，3-慈善人物
  content: '',
});

// 分页后的新闻列表
const paginatedNews = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return newsList.value.slice(start, end);
});

// 获取新闻数据
const fetchNews = async () => {
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const params = {
        title: queryForm.value.title,
        publisher: queryForm.value.publisher,
        startDate: queryForm.value.dateRange?.[0],
        endDate: queryForm.value.dateRange?.[1],
      };

      const response = await axios.get('/admin/news', {
        params,
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.data.code === 200) {
        newsList.value = response.data.data.map((news) => ({
          ...news,
          isRecommended: news.isRecommended || 0, // 确保 isRecommended 字段存在
        }));
      } else {
        console.error('获取新闻列表失败:', response.data.msg);
        ElMessage.error('获取新闻列表失败');
      }
    } catch (error) {
      console.error('获取新闻列表失败:', error);
      ElMessage.error('获取新闻列表失败：' + error.message);
    }
  } else {
    ElMessage.error('用户未登录，请先登录');
  }
};


const handleRecommendChange = async (row) => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    // 检查当前推荐的新闻数量
    const recommendedCount = newsList.value.filter(
        (news) => news.isRecommended === 1
    ).length;

    // 如果当前新闻已经是推荐状态，且推荐数量已经达到八个，阻止操作
    if (row.isRecommended === 1 && recommendedCount > 8) {
      ElMessage.warning('推荐数量已达到上限（8个），无法继续推荐');
      row.isRecommended = 0; // 恢复为未推荐状态
      return;
    }

    // 调用接口更新推荐状态
    const response = await axios.put(
        `/admin/news/${row.id}/recommend`,
        { isRecommended: row.isRecommended }, // 传递推荐状态
        {
          headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
          },
        }
    );

    if (response.data.code === 200) {
      ElMessage.success('推荐状态更新成功');
    } else {
      ElMessage.error('推荐状态更新失败');
    }
  } catch (error) {
    ElMessage.error('推荐状态更新失败：' + error.message);
  }
};

// 时间格式化函数
const formatDate = (timestamp) => {
  if (!timestamp) return ''; // 如果时间戳为空，返回空字符串
  const date = new Date(timestamp); // 将时间戳转换为 Date 对象
  const year = date.getFullYear(); // 获取年份
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 获取月份，补零
  const day = String(date.getDate()).padStart(2, '0'); // 获取日期，补零
  return `${year}-${month}-${day}`; // 返回格式化后的日期字符串
};

// 预览新闻详情
const handleView = (news) => {
  currentNews.value = news;
  viewDialogVisible.value = true;
};

// 编辑新闻
const handleEdit = (news) => {
  isEdit.value = true;
  formData.value = { ...news };
  drawerVisible.value = true;
};

// 添加新闻
const handleAdd = () => {
  isEdit.value = false;
  formData.value = {
    title: '',
    date: '',
    publisher: '',
    coverUrl: '',
    type: 1,
    content: '',
  };
  drawerVisible.value = true;
};

// 提交表单（添加或编辑）
const handleSubmit = async () => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    if (isEdit.value) {
      // 编辑逻辑
      const response = await axios.put(`/admin/news/${formData.value.id}`, formData.value, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.data.code === 200) {
        await fetchNews(); // 重新获取数据
        ElMessage.success('编辑成功');
      }
    } else {
      // 添加逻辑
      const response = await axios.post('/admin/news', formData.value, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.data.code === 200) {
        await fetchNews(); // 重新获取数据
        ElMessage.success('添加成功');
      }
    }

    // 清空表单数据
    formData.value = {
      title: '',
      date: '',
      publisher: '',
      coverUrl: '',
      type: 1,
      content: '',
    };

    drawerVisible.value = false; // 关闭抽屉
  } catch (error) {
    ElMessage.error('操作失败：' + error.message);
  }
};

// 处理选中新闻
const handleSelectionChange = (selection) => {
  selectedNews.value = selection;
};

// 批量删除新闻
const handleBatchDelete = async () => {
  try {
    // 确认删除操作
    await ElMessageBox.confirm('确定要删除选中的新闻吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 获取选中的新闻 ID 列表
    const selectedIds = selectedNews.value.map(news => news.id);

    // 从 localStorage 中获取令牌
    const token = localStorage.getItem('token');
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    // 发送批量删除请求，直接传递数组
    await axios.post('/admin/news/batch-delete', selectedIds, {
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
        'Content-Type': 'application/json',
      },
    });

    // 重新获取新闻列表
    await fetchNews();
    ElMessage.success('删除成功');
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message);
    }
  }
};

// 处理查询
const handleQuery = () => {
  currentPage.value = 1;
  fetchNews(); // 根据查询条件获取数据
};

// 处理重置
const handleReset = () => {
  queryForm.value = {
    title: '',
    publisher: '',
    dateRange: [],
  };
  currentPage.value = 1;
  fetchNews(); // 重置后获取全部数据
};

// 处理每页条数变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

// 处理页码变化
const handlePageChange = (page) => {
  currentPage.value = page;
};

// 文件输入框的引用
const fileInput = ref(null);

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value.click(); // 模拟点击文件输入框
};

// 文件选择后的处理
const handleFileChange = async (event) => {
  const file = event.target.files[0]; // 获取选择的文件
  if (!file) return;

  // 校验文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件！');
    return;
  }

  // 校验文件大小（限制为 10MB）
  const maxSize = 10 * 1024 * 1024; // 10MB
  if (file.size > maxSize) {
    ElMessage.error('文件大小不能超过 10MB');
    return;
  }

  // 上传文件
  try {
    const uploadFormData = new FormData(); // 使用 uploadFormData 避免冲突
    uploadFormData.append('file', file); // 将文件添加到 FormData

    // 发送上传请求
    const response = await axios.post('/files/upload', uploadFormData, {
      headers: {
        'Content-Type': 'multipart/form-data', // 设置请求头
      },
    });

    // 处理上传成功
    if (response.data.code === 200) {
      formData.value.coverUrl = response.data.data; // 更新封面图片 URL
      ElMessage.success('上传成功');
    } else {
      ElMessage.error(response.data.message || '上传失败');
    }
  } catch (error) {
    ElMessage.error('上传失败：' + error.message);
  }
};

// 获取类型标签样式
const getTypeTag = (type) => {
  switch (type) {
    case 1:
      return 'primary'; // 通知公告
    case 2:
      return 'success'; // 媒体报道
    case 3:
      return 'warning'; // 慈善人物
    default:
      return 'info';
  }
};

// 获取类型标签文本
const getTypeLabel = (type) => {
  switch (type) {
    case 1:
      return '通知公告';
    case 2:
      return '媒体报道';
    case 3:
      return '慈善人物';
    default:
      return '未知类型';
  }
};

// 组件挂载时初始化数据
onMounted(() => {
  fetchNews(); // 页面加载时获取全部数据
});
</script>

<style scoped>
/* 外层容器样式 */
.news-container {
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 20px;
}

/* 新闻列表模块样式 */
.news-list {
  width: 100%;
}

/* 查询表单容器样式 */
.query-form-container {
  background-color: var(--el-bg-color-page);
  padding: 20px;
  border-radius: 4px;
  margin-bottom: 20px;
}

/* 按钮组样式 */
.button-group {
  display: flex;
  justify-content: flex-end;
}

/* 操作栏样式 */
.action-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

/* 设置表格容器的宽度和溢出滚动 */
.el-table {
  width: 100%;
  overflow-x: auto;
}

/* 分页样式 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 图片上传区域样式 */
.image-upload {
  display: flex;
  align-items: center;
  gap: 16px;
}

.image-placeholder {
  width: 150px;
  height: 100px;
  border: 1px dashed #ccc;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #999;
}

.image-placeholder:hover {
  border-color: #409eff;
  color: #409eff;
}

/* 新闻信息样式 */
.news-info {
  display: flex;
  flex-direction: column;
  gap: 1em; /* 10px / 16px = 0.625em */
}

.info-item {
  display: flex;
  align-items: center;
}

.info-item .label {
  font-weight: bold;
}

.info-item .value {
  color: var(--el-text-color-regular);
}

/* 信息部分样式 */
.info-section {
  margin-top: 20px;
}

.info-section h3 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
}

/* 弹窗标题居中 */
:deep(.el-dialog__header ){
  text-align: left;
}

:deep(.el-dialog--center img){
  width: 100%;
}
</style>
