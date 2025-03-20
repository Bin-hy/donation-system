<template>
  <div class="public-info-container">
    <!-- 公示信息列表模块 -->
    <div class="public-info-list">
      <!-- 公示信息表格 -->
      <el-table
          :data="publicInfoList"
          style="width: 100%"
          border
          :header-cell-style="{ background: 'var(--el-bg-color-page)', color: 'var(--el-text-color-primary)' }"
      >
        <!-- 类型列 -->
        <el-table-column prop="type" label="类型" width="120">
          <template #default="{ row }">
            {{ getTypeText(row.type) }}
          </template>
        </el-table-column>

        <!-- 文件 URL 列 -->
        <el-table-column label="文件 URL" width="300">
          <template #default="{ row }">
            <el-link type="primary" @click="handleViewPdf(row.fileUrl)">查看文件</el-link>
          </template>
        </el-table-column>

        <!-- 图片列 -->
        <el-table-column label="图片" width="300">
          <template #default="{ row }">
            <el-image
                style="width: 275px; height: 113px"
                :src="row.imageUrl"
                fit="cover"
                :preview-src-list="[row.imageUrl]"
            />
          </template>
        </el-table-column>

        <!-- 创建时间列 -->
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <!-- 更新时间列 -->
        <el-table-column prop="updatedAt" label="上次更新时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.updatedAt) }}
          </template>
        </el-table-column>

        <!-- 操作列 -->
        <el-table-column label="操作" width="134">
          <template #default="{ row }">
            <el-button type="primary" size="default" @click="handleEdit(row)">更新</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑抽屉 -->
    <el-drawer
        v-model="drawerVisible"
        :title="'更新公示信息'"
        direction="rtl"
        size="40%"
    >
      <el-form :model="formData" label-width="120px">
        <!-- 文件上传 -->
        <el-form-item label="文件">
          <div class="custom-upload">
            <!-- 上传按钮 -->
            <div v-if="!formData.fileUrl" class="upload-area" @click="triggerFileInput">
              <el-icon><Plus /></el-icon>
              <span>点击上传文件</span>
            </div>

            <!-- 文件预览 -->
            <div v-else class="file-preview">
              <el-link type="primary" @click="handleViewPdf(formData.fileUrl)">查看文件</el-link>
              <div class="file-actions">
                <!-- 删除图标 -->
                <el-icon class="delete-icon" @click.stop="handleDeleteFile">
                  <Delete />
                </el-icon>
              </div>
            </div>

            <!-- 文件选择输入框 -->
            <input
                ref="fileInput"
                type="file"
                accept=".pdf"
                style="display: none"
                @change="handleFileChange"
            />
          </div>
        </el-form-item>

        <!-- 图片上传 -->
        <el-form-item label="图片">
          <div class="custom-upload">
            <!-- 上传按钮 -->
            <div v-if="!formData.imageUrl" class="upload-area" @click="triggerImageInput">
              <el-icon><Plus /></el-icon>
              <span>点击上传图片</span>
            </div>

            <!-- 图片预览 -->
            <div v-else class="image-preview">
              <el-image
                  style="width: 275px; height: 113px"
                  :src="formData.imageUrl"
                  fit="cover"
              />
              <div class="file-actions">
                <!-- 删除图标 -->
                <el-icon class="delete-icon" @click.stop="handleDeleteImage">
                  <Delete />
                </el-icon>
              </div>
            </div>

            <!-- 图片选择输入框 -->
            <input
                ref="imageInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleImageChange"
            />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Delete, Plus } from '@element-plus/icons-vue';
import axios from 'axios';

// 从 localStorage 中获取令牌
const getToken = () => {
  return localStorage.getItem('token'); // 假设令牌存储在 localStorage 中
};

// 公示信息列表数据
const publicInfoList = ref([]);

// 编辑抽屉相关状态
const drawerVisible = ref(false);
const formData = ref({
  id: '',
  type: 0,
  fileUrl: '',
  imageUrl: '', // 图片 URL
});

// 文件输入框引用
const fileInput = ref(null);
const imageInput = ref(null);

// 格式化日期
const formatDate = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 获取类型文本
const getTypeText = (type) => {
  const typeTexts = ['年会报告', '审计报告', '慈善年鉴', '专享报告'];
  return typeTexts[type - 1] || '';
};

// 查看 PDF 文件（直接下载）
const handleViewPdf = (fileUrl) => {
  const link = document.createElement('a');
  link.href = fileUrl;
  link.download = fileUrl.split('/').pop(); // 使用文件 URL 的最后一部分作为文件名
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
};

// 编辑公示信息
const handleEdit = (row) => {
  formData.value = { ...row }; // 绑定当前行的数据
  drawerVisible.value = true; // 打开编辑抽屉
};

// 提交表单（更新）
const handleSubmit = async () => {
  try {
    const token = getToken();
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    // 提交更新请求
    const response = await axios.put(
        `/admin/public-info/update/${formData.value.id}`, // 传递 id
        {
          type: formData.value.type, // 传递 type
          fileUrl: formData.value.fileUrl, // 传递新的文件 URL
          imageUrl: formData.value.imageUrl, // 传递新的图片 URL
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
    );

    if (response.data.code === 200) {
      ElMessage.success('更新成功');
      fetchPublicInfoList(); // 刷新列表
      drawerVisible.value = false; // 关闭抽屉
    } else {
      ElMessage.error('更新失败');
    }
  } catch (error) {
    ElMessage.error('操作失败: ' + error.message);
  }
};

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value.click();
};

// 触发图片选择
const triggerImageInput = () => {
  imageInput.value.click();
};

// 处理文件选择
const handleFileChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 校验文件类型
  if (file.type !== 'application/pdf') {
    ElMessage.error('只能上传 PDF 文件！');
    return;
  }

  // 上传文件到服务器
  try {
    const uploadFormData = new FormData();
    uploadFormData.append('file', file);

    const response = await axios.post('/files/upload', uploadFormData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    if (response.data.code === 200) {
      formData.value.fileUrl = response.data.data; // 绑定服务器返回的文件 URL
      ElMessage.success('文件上传成功');
    } else {
      ElMessage.error('文件上传失败: ' + response.data.msg);
    }
  } catch (error) {
    ElMessage.error('文件上传失败: ' + error.message);
  }
};

// 处理图片选择
const handleImageChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 校验文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件！');
    return;
  }

  // 上传图片到服务器
  try {
    const uploadFormData = new FormData();
    uploadFormData.append('file', file);

    const response = await axios.post('/files/upload', uploadFormData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });

    if (response.data.code === 200) {
      formData.value.imageUrl = response.data.data; // 绑定服务器返回的图片 URL
      ElMessage.success('图片上传成功');
    } else {
      ElMessage.error('图片上传失败: ' + response.data.msg);
    }
  } catch (error) {
    ElMessage.error('图片上传失败: ' + error.message);
  }
};

// 删除文件
const handleDeleteFile = () => {
  formData.value.fileUrl = '';
  fileInput.value.value = ''; // 清空文件输入框
};

// 删除图片
const handleDeleteImage = () => {
  formData.value.imageUrl = '';
  imageInput.value.value = ''; // 清空图片输入框
};

// 获取公示信息列表
const fetchPublicInfoList = async () => {
  try {
    const token = getToken();
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    const response = await axios.get('/admin/public-info/list', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    if (response.data.code === 200) {
      publicInfoList.value = response.data.data;
    } else {
      console.error('获取公示信息列表失败:', response.data.msg);
    }
  } catch (error) {
    console.error('获取公示信息列表失败:', error);
  }
};

// 组件挂载时获取数据
onMounted(() => {
  fetchPublicInfoList();
});
</script>

<style scoped>
.public-info-container {
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 20px;
}

.custom-upload {
  display: inline-block;
}

.upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 150px;
  height: 150px;
  border: 1px dashed var(--el-border-color);
  border-radius: 4px;
  cursor: pointer;
  background-color: var(--el-bg-color-page);
  transition: border-color 0.3s;
}

.upload-area:hover {
  border-color: var(--el-color-primary);
}

.upload-area .el-icon {
  font-size: 24px;
  color: var(--el-color-primary);
}

.upload-area span {
  margin-top: 8px;
  font-size: 14px;
  color: var(--el-text-color-regular);
}

.file-preview {
  position: relative;
  width: 100%;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.file-actions {
  margin-left: 100px;
  display: flex;
  gap: 8px;
}

.delete-icon {
  font-size: 18px;
  color: var(--el-color-danger);
  cursor: pointer;
}
</style>
