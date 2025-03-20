<template>
  <div class="carousel-container">
    <!-- 轮播图列表模块 -->
    <div class="carousel-list">
      <!-- 操作栏 -->
      <div class="action-bar">
        <!-- 添加按钮 -->
        <el-button type="primary" @click="handleAdd">添加</el-button>
        <!-- 一键删除按钮 -->
        <el-button
            type="danger"
            :disabled="selectedCarousels.length === 0"
            @click="handleBatchDelete"
        >
          一键删除
        </el-button>
      </div>

      <!-- 轮播图表格 -->
      <el-table
          :data="carouselList"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
          :header-cell-style="{ background: 'var(--el-bg-color-page)', color: 'var(--el-text-color-primary)' }"
      >
        <!-- 多选列 -->
        <el-table-column type="selection" width="55" />

        <!-- 排序列 -->
        <el-table-column prop="sort" label="排序号" width="100" />

        <!-- 轮播图列 -->
        <el-table-column label="轮播图" width="450">
          <template #default="{ row }">
            <el-image
                style="width: 400px; height: 150px; border-radius: 4px"
                :src="row.imageUrl"
                fit="cover"
            />
          </template>
        </el-table-column>

        <!-- 绑定项目名称列 -->
        <el-table-column prop="projectName" label="绑定项目名称" />

        <!-- 创建时间列 -->
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>

        <!-- 更新时间列 -->
        <el-table-column prop="updatedAt" label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.updatedAt) }}
          </template>
        </el-table-column>

        <!-- 操作列（固定在右侧） -->
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="default" @click="handleEdit(row)">更新</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑/添加抽屉 -->
    <el-drawer
        v-model="drawerVisible"
        :title="isEdit ? '编辑轮播图' : '添加轮播图'"
        direction="rtl"
        size="40%"
    >
      <el-form :model="formData" label-width="120px">
        <el-form-item label="排序号">
          <el-input v-model="formData.sort" placeholder="请输入排序号" />
        </el-form-item>
        <el-form-item label="轮播图">
          <!-- 自定义图片上传组件 -->
          <div class="custom-upload">
            <!-- 上传按钮 -->
            <div v-if="!formData.imageUrl" class="upload-area" @click="triggerFileInput">
              <el-icon><Plus /></el-icon>
              <span>点击上传图片</span>
            </div>

            <!-- 图片预览 -->
            <div v-else class="image-preview">
              <img :src="formData.imageUrl" alt="Preview Image" @click="triggerFileInput" />
              <div class="image-actions">
                <!-- 预览图标 -->
                <el-icon class="preview-icon" @click.stop="handlePreview">
                  <ZoomIn />
                </el-icon>
                <!-- 删除图标 -->
                <el-icon class="delete-icon" @click.stop="handleDeleteImage">
                  <Delete />
                </el-icon>
              </div>
            </div>

            <!-- 文件选择输入框 -->
            <input
                ref="fileInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handleFileChange"
            />
          </div>
        </el-form-item>
        <el-form-item label="绑定项目">
          <el-select
              v-model="formData.projectId"
              placeholder="请选择绑定项目"
              clearable
          >
            <el-option
                v-for="project in projectList"
                :key="project.id"
                :label="project.title"
                :value="project.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存' : '添加' }}</el-button>
      </template>
    </el-drawer>

    <!-- 图片预览对话框 -->
    <el-dialog v-model="previewVisible" title="图片预览" width="50%">
      <img :src="previewImageUrl" alt="Preview Image" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Delete, Plus, ZoomIn } from '@element-plus/icons-vue';
import axios from 'axios';

// 从 localStorage 中获取令牌
const getToken = () => {
  return localStorage.getItem('token'); // 假设令牌存储在 localStorage 中
};

// 轮播图列表数据
const carouselList = ref([]);

// 项目列表数据
const projectList = ref([]);

// 选中的轮播图
const selectedCarousels = ref([]);

// 编辑/添加抽屉相关状态
const drawerVisible = ref(false);
const isEdit = ref(false); // 是否为编辑模式
const formData = ref({
  id: '',
  sort: 0, // 排序号
  imageUrl: '', // 轮播图 URL（与后端一致）
  projectId: '', // 绑定的项目ID（与后端一致）
});

// 图片预览相关状态
const previewImageUrl = ref('');
const previewVisible = ref(false);

// 文件输入框引用
const fileInput = ref(null);

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

// 获取轮播图列表
const fetchCarouselList = async () => {
  try {
    const token = getToken();
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    const response = await axios.get('/admin/carousel/list', {
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
      },
    });
    if (response.data.code === 200) {
      carouselList.value = response.data.data;
    } else {
      console.error('获取轮播图列表失败:', response.data.msg);
    }
  } catch (error) {
    console.error('获取轮播图列表失败:', error);
  }
};

// 获取项目列表
const fetchProjectList = async () => {
  try {
    const token = getToken();
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    const response = await axios.get('/admin/projects', {
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
      },
    });
    if (response.data.code === 200) {
      // 提取项目的 title 和 id
      projectList.value = response.data.data.map((project) => ({
        id: project.id,
        title: project.title,
      }));
    } else {
      console.error('获取项目列表失败:', response.data.msg);
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
  }
};

// 处理选中轮播图
const handleSelectionChange = (selection) => {
  selectedCarousels.value = selection;
};

// 添加轮播图
const handleAdd = () => {
  isEdit.value = false;
  formData.value = {
    id: '',
    sort: 0,
    imageUrl: '',
    projectId: '',
  };
  drawerVisible.value = true;
};

// 编辑轮播图
const handleEdit = (row) => {
  isEdit.value = true;
  formData.value = { ...row };
  drawerVisible.value = true;
};

// 批量删除轮播图
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除选中的轮播图吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    const token = getToken();
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    const selectedIds = selectedCarousels.value.map((item) => item.id);
    const response = await axios.post(
        '/admin/carousel/batch-delete',
        { ids: selectedIds },
        {
          headers: {
            Authorization: `Bearer ${token}`, // 添加令牌到请求头
          },
        }
    );
    if (response.data.code === 200) {
      ElMessage.success('删除成功');
      await fetchCarouselList(); // 刷新列表
    } else {
      ElMessage.error('删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败: ' + error.message);
    }
  }
};

// 提交表单（添加或编辑）
const handleSubmit = async () => {
  try {
    console.log("Form Data:", formData.value); // 打印 formData
    const token = getToken();
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    if (isEdit.value) {
      // 编辑逻辑
      const response = await axios.put(
          `/admin/carousel/update/${formData.value.id}`,
          formData.value,
          {
            headers: {
              Authorization: `Bearer ${token}`, // 添加令牌到请求头
            },
          }
      );
      if (response.data.code === 200) {
        ElMessage.success('编辑成功');
        await fetchCarouselList(); // 刷新列表
      }
    } else {
      // 添加逻辑
      const response = await axios.post(
          '/admin/carousel/add',
          formData.value,
          {
            headers: {
              Authorization: `Bearer ${token}`, // 添加令牌到请求头
            },
          }
      );
      if (response.data.code === 200) {
        ElMessage.success('添加成功');
        await fetchCarouselList(); // 刷新列表
      }
    }
    drawerVisible.value = false; // 关闭抽屉
  } catch (error) {
    ElMessage.error('操作失败: ' + error.message);
  }
};

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value.click();
};

// 处理文件选择
const handleFileChange = async (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 校验文件类型和大小
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件！');
    return;
  }
  if (file.size > 2 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 2MB！');
    return;
  }

  // 上传图片到服务器
  try {
    const uploadFormData = new FormData(); // 重命名为 uploadFormData
    uploadFormData.append('file', file);

    const response = await axios.post('/files/upload', uploadFormData, {
      headers: {
        'Content-Type': 'multipart/form-data', // 确保正确设置 Content-Type
      },
    });

    console.log("Upload Response:", response.data); // 打印上传响应

    if (response.data.code === 200) {
      // 绑定服务器返回的图片 URL
      formData.value.imageUrl = response.data.data; // 使用外部的 formData
      console.log("Image URL:", formData.value.imageUrl); // 打印图片 URL
      ElMessage.success('图片上传成功');
    } else {
      ElMessage.error('图片上传失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('图片上传失败:', error);
    ElMessage.error('图片上传失败: ' + error.message);
  }
};

// 图片预览
const handlePreview = () => {
  previewImageUrl.value = formData.value.imageUrl;
  previewVisible.value = true;
};

// 删除图片
const handleDeleteImage = () => {
  formData.value.imageUrl = '';
  fileInput.value.value = ''; // 清空文件输入框
};

// 组件挂载时获取数据
onMounted(() => {
  fetchCarouselList();
  fetchProjectList(); // 获取项目列表
});
</script>

<style scoped>
.carousel-container {
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 20px;
}

.action-bar {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
}

.el-table {
  width: 100%;
  overflow-x: auto;
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

.image-preview {
  display: flex;
  position: relative;
  width: 400px;
  height: 150px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  display: flex;
  gap: 8px;
}

.preview-icon,
.delete-icon {
  font-size: 18px;
  color: white;
  cursor: pointer;
  background-color: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  padding: 4px;
  transition: background-color 0.3s;
}

.preview-icon:hover,
.delete-icon:hover {
  background-color: rgba(0, 0, 0, 0.7);
}
</style>
