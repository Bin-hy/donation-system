<template>
  <div class="project-container">
    <!-- 项目列表模块 -->
    <div class="project-list">
      <!-- 查询表单 -->
      <div class="query-form-container">
        <el-form :inline="true" :model="queryForm" class="query-form">
          <el-row :gutter="20">
            <!-- 左侧表单内容 -->
            <el-col :span="18">
              <el-row :gutter="10">
                <!-- 项目名称 -->
                <el-col :span="8">
                  <el-form-item label="项目名称">
                    <el-input
                        v-model="queryForm.title"
                        placeholder="请输入项目名称"
                        clearable
                    />
                  </el-form-item>
                </el-col>

                <!-- 项目编号 -->
                <el-col :span="16">
                  <el-form-item label="项目编号">
                    <el-input
                        v-model="queryForm.recordNumber"
                        placeholder="请输入项目编号"
                        clearable
                    />
                  </el-form-item>
                </el-col>

                <!-- 项目分类 -->
                <el-col :span="8">
                  <el-form-item label="项目分类">
                    <el-select
                        v-model="queryForm.category"
                        placeholder="请选择项目分类"
                        clearable
                        style="width: 150px;"
                    >
                      <el-option label="扶贫济困" :value="0" />
                      <el-option label="健康医疗" :value="1" />
                      <el-option label="助老扶幼" :value="2" />
                      <el-option label="文化教育" :value="3" />
                      <el-option label="社会服务" :value="4" />
                      <el-option label="科技环保" :value="5" />
                      <el-option label="灾难救助" :value="6" />
                      <el-option label="专项基金" :value="7" />
                    </el-select>
                  </el-form-item>
                </el-col>

                <!-- 时间范围 -->
                <el-col :span="8">
                  <el-form-item label="时间范围">
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
              <el-form-item style="margin-top: 45px;">
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
            :disabled="selectedProjects.length === 0"
            @click="handleBatchDelete"
        >
          一键删除
        </el-button>
      </div>

      <!-- 项目表格 -->
      <el-table
          :data="paginatedProjects"
          style="width: 100%"
          border
          @selection-change="handleSelectionChange"
          :header-cell-style="{ background: 'var(--el-bg-color-page)', color: 'var(--el-text-color-primary)' }"
      >
        <!-- 多选列 -->
        <el-table-column type="selection" width="55" />

        <!-- 项目名称列（固定在左侧） -->
        <el-table-column prop="title" label="项目名称" fixed="left" width="200" />

        <!-- 封面图片列 -->
        <el-table-column label="封面" width="250">
          <template #default="{ row }">
            <el-image
                style="width: 200px; height: 120px; border-radius: 4px"
                :src="row.image"
                fit="cover"
            />
          </template>
        </el-table-column>

        <!-- 项目分类列 -->
        <el-table-column label="项目分类" width="120">
          <template #default="{ row }">
            <span>{{ getCategoryLabel(row.category) }}</span>
          </template>
        </el-table-column>

        <!-- 是否推荐列 -->
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
    projectList.filter((project) => project.isRecommended === 1).length > 8
  "
                @change="handleRecommendChange(row)"
            />
          </template>
        </el-table-column>

        <!-- 机构信息列 -->
        <el-table-column prop="orgInfo.initiator" label="发起机构" width="180" />
        <el-table-column prop="orgInfo.receiver" label="善款接收机构" width="180" />
        <el-table-column prop="orgInfo.executor" label="执行机构" width="180" />

        <!-- 其他列 -->
        <el-table-column prop="recordNumber" label="备案编号" width="150" />
        <el-table-column prop="targetAmount" label="目标金额" width="120" />
        <el-table-column prop="raisedAmount" label="已筹金额" width="120" />
        <el-table-column prop="donationCount" label="爱心次数" width="100" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />

        <!-- projectInfo 相关列 -->
        <el-table-column prop="projectInfo.purpose" label="项目目的" width="200" />
        <el-table-column prop="projectInfo.cost" label="项目成本" width="120" />
        <el-table-column prop="projectInfo.surplusPlan" label="剩余财产处理" width="150" />
        <el-table-column prop="projectInfo.invoiceMethod" label="发票开具方式" width="150" />
        <el-table-column prop="projectInfo.contact" label="联系方式" width="120" />
        <el-table-column prop="projectInfo.fundUsage" label="资金用途" width="150" />
        <el-table-column prop="projectInfo.beneficiaries" label="受益对象" width="150" />
        <el-table-column prop="projectInfo.leader" label="负责人" width="120" />

        <!-- 操作列（固定在右侧） -->
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="default" @click="handleView(row)">查看</el-button>
            <el-button type="warning" size="default" @click="handleEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页组件 -->
      <div class="pagination">
        <el-pagination
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="projectList.length"
            :page-size="pageSize"
            :current-page="currentPage"
            :page-sizes="[5, 10, 20, 50]"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 查看弹窗 -->
    <el-dialog
        v-if="currentProject"
        v-model="viewDialogVisible"
        title="项目详情"
        width="50%"
        center
    >
      <div>
        <!-- 上半部分：左右布局 -->
        <el-row :gutter="20">
          <!-- 左边：图片 -->
          <el-col :span="12">
            <el-image
                style="width: 100%; border-radius: 4px"
                :src="currentProject.image"
                fit="cover"
            />
          </el-col>
          <!-- 右边：项目基本信息 -->
          <el-col :span="12">
            <div class="project-info">
              <div class="info-item">
                <span class="label">项目名称：</span>
                <span class="value">{{ currentProject.title }}</span>
              </div>
              <div class="info-item">
                <span class="label">备案编号：</span>
                <span class="value">{{ currentProject.recordNumber }}</span>
              </div>
              <div class="info-item">
                <span class="label">目标金额：</span>
                <span class="value">￥{{ currentProject.targetAmount }} 元</span>
              </div>
              <div class="info-item">
                <span class="label">已筹金额：</span>
                <span class="value">￥{{ currentProject.raisedAmount }} 元</span>
              </div>
              <div class="info-item">
                <span class="label">爱心次数：</span>
                <span class="value">{{ currentProject.donationCount }} 次</span>
              </div>
              <div class="info-item">
                <span class="label">起止日期：</span>
                <span class="value">{{ currentProject.startDate }} 至 {{ currentProject.endDate }}</span>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 项目详情 -->
        <div class="info-section">
          <h2>项目详情</h2>
          <p style="font-family: 仿宋, serif;">{{ currentProject.details }}</p>
        </div>

        <!-- 慈善机构信息 -->
        <div class="info-section" v-if="currentProject.orgInfo">
          <h2>慈善机构信息</h2>
          <ul class="info-list">
            <li><strong>发起机构：</strong>{{ currentProject.orgInfo.initiator }}</li>
            <li><strong>善款接收机构：</strong>{{ currentProject.orgInfo.receiver }}</li>
            <li><strong>执行机构：</strong>{{ currentProject.orgInfo.executor }}</li>
          </ul>
        </div>

        <!-- 项目信息 -->
        <div class="info-section" v-if="currentProject.projectInfo">
          <h2>项目信息</h2>
          <ul class="info-list">
            <li><strong>募捐目的：</strong>{{ currentProject.projectInfo.purpose }}</li>
            <li><strong>募捐成本：</strong>{{ currentProject.projectInfo.cost }}</li>
            <li><strong>剩余财产处理方案：</strong>{{ currentProject.projectInfo.surplusPlan }}</li>
            <li><strong>发票开具方式：</strong>{{ currentProject.projectInfo.invoiceMethod }}</li>
            <li><strong>联系方式：</strong>{{ currentProject.projectInfo.contact }}</li>
            <li><strong>募得款物用途：</strong>{{ currentProject.projectInfo.fundUsage }}</li>
            <li><strong>受益人：</strong>{{ currentProject.projectInfo.beneficiaries }}</li>
            <li><strong>活动负责人：</strong>{{ currentProject.projectInfo.leader }}</li>
          </ul>
        </div>
      </div>
    </el-dialog>

    <!-- 编辑/添加抽屉 -->
    <el-drawer
        v-model="drawerVisible"
        :title="isEdit ? '编辑项目' : '添加项目'"
        direction="rtl"
        size="40%"
    >
      <!-- 表单 -->
      <el-form
          :model="formData"
          :rules="formRules"
          label-width="120px"
          ref="formRef"
      >
        <!-- 项目名称 -->
        <el-form-item label="项目名称" prop="title">
          <el-input v-model="formData.title" placeholder="请输入项目名称" />
        </el-form-item>

        <!-- 项目分类 -->
        <el-form-item label="项目分类" prop="category">
          <el-select
              v-model="formData.category"
              placeholder="请选择分类"
          >
            <el-option label="扶贫济困" :value="0" />
            <el-option label="健康医疗" :value="1" />
            <el-option label="助老扶幼" :value="2" />
            <el-option label="文化教育" :value="3" />
            <el-option label="社会服务" :value="4" />
            <el-option label="科技环保" :value="5" />
            <el-option label="灾难救助" :value="6" />
            <el-option label="专项基金" :value="7" />
          </el-select>
        </el-form-item>

        <!-- 封面图片 -->
        <el-form-item label="封面图片" prop="image">
          <div class="image-upload">
            <el-image
                v-if="formData.image"
                style="width: 200px; height: 120px; border-radius: 4px; cursor: pointer"
                :src="formData.image"
                fit="cover"
                @click="triggerFileInput"
            />
            <div
                v-else
                class="image-placeholder"
                @click="triggerFileInput"
            >
              <span>点击上传封面</span>
            </div>
            <input
                type="file"
                ref="fileInput"
                style="display: none"
                @change="handleFileChange"
                accept="image/*"
            />
          </div>
        </el-form-item>

        <!-- 其他表单项 -->
        <el-form-item label="项目编号" prop="recordNumber">
          <el-input v-model="formData.recordNumber" placeholder="请输入项目编号" />
        </el-form-item>
        <el-form-item label="目标金额" prop="targetAmount">
          <el-input v-model="formData.targetAmount" placeholder="请输入目标金额" />
        </el-form-item>
        <el-form-item label="已筹金额" prop="raisedAmount">
          <el-input v-model="formData.raisedAmount" placeholder="请输入已筹金额" />
        </el-form-item>
        <el-form-item label="爱心次数" prop="donationCount">
          <el-input v-model="formData.donationCount" placeholder="请输入爱心次数" />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker
              v-model="formData.startDate"
              type="date"
              placeholder="选择开始日期"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker
              v-model="formData.endDate"
              type="date"
              placeholder="选择结束日期"
              value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="发起机构" prop="orgInfo.initiator">
          <el-input v-model="formData.orgInfo.initiator" placeholder="请输入发起机构" />
        </el-form-item>
        <el-form-item label="善款接收机构" prop="orgInfo.receiver">
          <el-input v-model="formData.orgInfo.receiver" placeholder="请输入善款接收机构" />
        </el-form-item>
        <el-form-item label="执行机构" prop="orgInfo.executor">
          <el-input v-model="formData.orgInfo.executor" placeholder="请输入执行机构" />
        </el-form-item>
        <el-form-item label="项目目的" prop="projectInfo.purpose">
          <el-input v-model="formData.projectInfo.purpose" placeholder="请输入项目目的" />
        </el-form-item>
        <el-form-item label="项目成本" prop="projectInfo.cost">
          <el-input v-model="formData.projectInfo.cost" placeholder="请输入项目成本" />
        </el-form-item>
        <el-form-item label="剩余财产处理" prop="projectInfo.surplusPlan">
          <el-input v-model="formData.projectInfo.surplusPlan" placeholder="请输入剩余财产处理方式" />
        </el-form-item>
        <el-form-item label="发票开具方式" prop="projectInfo.invoiceMethod">
          <el-input v-model="formData.projectInfo.invoiceMethod" placeholder="请输入发票开具方式" />
        </el-form-item>
        <el-form-item label="联系方式" prop="projectInfo.contact">
          <el-input v-model="formData.projectInfo.contact" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="资金用途" prop="projectInfo.fundUsage">
          <el-input v-model="formData.projectInfo.fundUsage" placeholder="请输入资金用途" />
        </el-form-item>
        <el-form-item label="受益对象" prop="projectInfo.beneficiaries">
          <el-input v-model="formData.projectInfo.beneficiaries" placeholder="请输入受益对象" />
        </el-form-item>
        <el-form-item label="负责人" prop="projectInfo.leader">
          <el-input v-model="formData.projectInfo.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="项目详情" prop="details">
          <el-input
              v-model="formData.details"
              type="textarea"
              :rows="4"
              placeholder="请输入项目详情"
          />
        </el-form-item>
      </el-form>

      <!-- 抽屉底部按钮 -->
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

// 项目列表数据
const projectList = ref([]);

// 查询表单数据
const queryForm = ref({
  title: '', // 项目名称
  recordNumber: '', // 项目编号
  category: null, // 项目分类
  dateRange: [], // 时间范围
});

// 分页相关状态
const currentPage = ref(1); // 当前页码
const pageSize = ref(5); // 每页显示条数

// 选中的项目
const selectedProjects = ref([]);

// 查看弹窗相关状态
const viewDialogVisible = ref(false);
const currentProject = ref(null);

// 编辑/添加抽屉相关状态
const drawerVisible = ref(false);
const isEdit = ref(false); // 是否为编辑模式
const formData = ref({
  title: '',
  recordNumber: '',
  targetAmount: '',
  raisedAmount: '',
  donationCount: '',
  startDate: '',
  endDate: '',
  category: null,
  isRecommended: false, // 是否推荐
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
  details: '',
  image: '',
});



// 分页后的项目列表
const paginatedProjects = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return projectList.value.slice(start, end);
});

// 获取分类名称
const getCategoryLabel = (category) => {
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
  return categoryMap[category] || '未知分类';
};

// 获取项目数据
const fetchProjects = async () => {
  const token = localStorage.getItem('token');
  if (token) {
    try {
      const params = {
        title: queryForm.value.title,
        recordNumber: queryForm.value.recordNumber,
        category: queryForm.value.category, // 增加分类参数
        startDate: queryForm.value.dateRange?.[0],
        endDate: queryForm.value.dateRange?.[1],
      };

      const response = await axios.get('/admin/projects', {
        params,
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.data.code === 200) {
        // 处理时间戳
        projectList.value = response.data.data.map(project => ({
          ...project,
          startDate: new Date(project.startDate).toISOString().split('T')[0], // 转换为 YYYY-MM-DD
          endDate: new Date(project.endDate).toISOString().split('T')[0], // 转换为 YYYY-MM-DD
        }));
      } else {
        console.error('获取项目列表失败:', response.data.msg);
        ElMessage.error('获取项目列表失败');
      }
    } catch (error) {
      console.error('获取项目列表失败:', error);
      ElMessage.error('获取项目列表失败：' + error.message);
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

    // 检查当前推荐的项目数量
    const recommendedCount = projectList.value.filter(
        (project) => project.isRecommended === 1
    ).length;

    // 如果当前项目已经是推荐状态，且推荐数量已经达到八个，阻止操作
    if (row.isRecommended === 1 && recommendedCount > 8) {
      ElMessage.warning('推荐数量已达到上限（8个），无法继续推荐');
      row.isRecommended = 0; // 恢复为未推荐状态
      return;
    }

    // 调用接口更新推荐状态
    const response = await axios.put(
        `/admin/projects/${row.id}/recommend`,
        null, // 不需要请求体
        {
          params: {
            isRecommended: row.isRecommended, // 传递推荐状态
          },
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

// 表单验证规则
// eslint-disable-next-line no-unused-vars
const formRules = ref({
  title: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }],
  category: [{ required: true, message: '项目分类不能为空', trigger: 'change' }],
  recordNumber: [{ required: true, message: '项目编号不能为空', trigger: 'blur' }],
  targetAmount: [{ required: true, message: '目标金额不能为空', trigger: 'blur' }],
  raisedAmount: [{ required: true, message: '已筹金额不能为空', trigger: 'blur' }],
  donationCount: [{ required: true, message: '爱心次数不能为空', trigger: 'blur' }],
  startDate: [{ required: true, message: '开始日期不能为空', trigger: 'change' }],
  endDate: [{ required: true, message: '结束日期不能为空', trigger: 'change' }],
  'orgInfo.initiator': [{ required: true, message: '发起机构不能为空', trigger: 'blur' }],
  'orgInfo.receiver': [{ required: true, message: '善款接收机构不能为空', trigger: 'blur' }],
  'orgInfo.executor': [{ required: true, message: '执行机构不能为空', trigger: 'blur' }],
  'projectInfo.purpose': [{ required: true, message: '项目目的不能为空', trigger: 'blur' }],
  'projectInfo.cost': [{ required: true, message: '项目成本不能为空', trigger: 'blur' }],
  'projectInfo.surplusPlan': [{ required: true, message: '剩余财产处理不能为空', trigger: 'blur' }],
  'projectInfo.invoiceMethod': [{ required: true, message: '发票开具方式不能为空', trigger: 'blur' }],
  'projectInfo.contact': [{ required: true, message: '联系方式不能为空', trigger: 'blur' }],
  'projectInfo.fundUsage': [{ required: true, message: '资金用途不能为空', trigger: 'blur' }],
  'projectInfo.beneficiaries': [{ required: true, message: '受益对象不能为空', trigger: 'blur' }],
  'projectInfo.leader': [{ required: true, message: '负责人不能为空', trigger: 'blur' }],
  details: [{ required: true, message: '项目详情不能为空', trigger: 'blur' }],
  image: [{ required: true, message: '封面图片不能为空', trigger: 'change' }],
});

// 查看项目详情
const handleView = (project) => {
  currentProject.value = project;
  viewDialogVisible.value = true;
};

// 编辑项目
const handleEdit = (project) => {
  isEdit.value = true;
  formData.value = { ...project };
  drawerVisible.value = true;
};

// 添加项目
const handleAdd = () => {
  isEdit.value = false;
  formData.value = {
    title: '',
    recordNumber: '',
    targetAmount: '',
    raisedAmount: '',
    donationCount: '',
    startDate: '',
    endDate: '',
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
    details: '',
    image: '', // 封面图片
  };
  drawerVisible.value = true;
};

const formRef = ref(null); // 表单引用
// 提交表单（添加或编辑）
const handleSubmit = async () => {
  try {
    // 验证表单
    await formRef.value.validate();

    // 如果验证通过，继续提交逻辑
    const payload = {
      ...formData.value,
      startDate: new Date(formData.value.startDate).getTime(),
      endDate: new Date(formData.value.endDate).getTime(),
    };

    if (isEdit.value) {
      // 编辑逻辑
      const response = await axios.put(`/admin/projects/${formData.value.id}`, payload, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.data.code === 200) {
        await fetchProjects(); // 重新获取数据
        ElMessage.success('编辑成功');
      }
    } else {
      // 添加逻辑
      const response = await axios.post('/admin/projects', payload, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.data.code === 200) {
        await fetchProjects(); // 重新获取数据
        ElMessage.success('添加成功');
      }
    }
    drawerVisible.value = false;
  } catch (error) {
    // 捕获验证失败的错误
    if (error instanceof Error) {
      // 如果是表单验证失败，弹出全局提示
      ElMessage.error('表单验证失败，请检查填写内容');
    } else if (error !== 'cancel') {
      // 其他错误（如网络请求失败）
      ElMessage.error('必填项未填，请检查填写内容' );
    }
  }
};


// 处理选中项目
const handleSelectionChange = (selection) => {
  selectedProjects.value = selection;
};

// 批量删除项目
const handleBatchDelete = async () => {
  try {
    // 确认删除操作
    await ElMessageBox.confirm('确定要删除选中的项目吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 获取选中的项目 ID 列表
    const selectedIds = selectedProjects.value.map(project => project.id);

    // 从 localStorage 中获取令牌
    const token = localStorage.getItem('token');
    if (!token) {
      ElMessage.error('用户未登录，请先登录');
      return;
    }

    // 发送批量删除请求，直接传递数组
    await axios.post('/admin/projects/batch-delete', selectedIds, {
      headers: {
        Authorization: `Bearer ${token}`, // 添加令牌到请求头
        'Content-Type': 'application/json',
      },
    });

    // 重新获取项目列表
    await fetchProjects();
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
  fetchProjects(); // 根据查询条件获取数据
};

// 处理重置
const handleReset = () => {
  queryForm.value = {
    title: '',
    recordNumber: '',
    dateRange: [],
  };
  currentPage.value = 1;
  fetchProjects(); // 重置后获取全部数据
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
      formData.value.image = response.data.data; // 更新图片 URL
      ElMessage.success('上传成功');
    } else {
      ElMessage.error(response.data.message || '上传失败');
    }
  } catch (error) {
    ElMessage.error('上传失败：' + error.message);
  }
};

// 组件挂载时初始化数据
onMounted(() => {
  fetchProjects(); // 页面加载时获取全部数据
});
</script>

<style scoped>
/* 外层容器样式 */
.project-container {
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 20px;
}

/* 项目列表模块样式 */
.project-list {
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
  width: 200px;
  height: 120px;
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

/* 项目信息样式 */
.project-info {
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

.info-section h2 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  color: var(--el-text-color-primary);
}

.info-list {
  list-style: none;
  padding-left: 20px;
  margin: 0;
}

.info-list li {
  margin-bottom: 8px;
  color: var(--el-text-color-regular);
}

.info-list strong {
  font-weight: bold;
  margin-right: 8px;
}
</style>
