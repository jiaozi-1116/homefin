<template>
    <section id="manage-clients">
      <el-card>
        <h2>管理客户档案和信息</h2>
  
        <!-- 当前选择的客户 -->
        <el-card shadow="hover" style="margin-bottom: 20px;">
          <h3>当前客户</h3>
          <el-button type="primary" @click="showEditDialog">编辑信息</el-button>
          <el-descriptions v-if="currentClient" border>
            <el-descriptions-item label="姓名">{{ currentClient.fullName }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ currentClient.email }}</el-descriptions-item>
            <el-descriptions-item label="电话">{{ currentClient.phoneNumber }}</el-descriptions-item>
            <el-descriptions-item label="注册日期">{{ currentClient.dateRegistered }}</el-descriptions-item>
          </el-descriptions>
          <el-empty v-else description="未选择客户" />
        </el-card>
  
        <!-- 其他客户 -->
        <el-card shadow="hover">
          <h3>其他客户</h3>
          <el-table :data="otherClients" style="width: 100%" border>
            <el-table-column prop="fullName" label="姓名" width="180"></el-table-column>
            <el-table-column prop="email" label="邮箱"></el-table-column>
            <el-table-column prop="phoneNumber" label="电话"></el-table-column>
            <el-table-column prop="dateRegistered" label="注册日期"></el-table-column>
          </el-table>
        </el-card>
      </el-card>

      <el-dialog v-model="editDialogVisible" title="修改客户信息" width="30%">
            <el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="80px">
                <el-form-item label="姓名" prop="fullName">
                    <el-input v-model="editForm.fullName" />
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="editForm.email" />
                </el-form-item>
                <el-form-item label="电话" prop="phoneNumber">
                    <el-input v-model="editForm.phoneNumber" />
                </el-form-item>
            </el-form>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="editDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="submitEditForm" :loading="isSubmitting">
                        确认修改
                    </el-button>
                </span>
            </template>
        </el-dialog>


    </section>
  </template>
  
  <script setup>
  import { ref, onMounted, watch } from 'vue';
  import axios from 'axios';
  import { ElMessage } from 'element-plus';
  // 在原有导入基础上添加 ElLoading 和 ElNotification
import { ElLoading, ElNotification } from 'element-plus';
  
  const props = defineProps({
    advisorName: {
      type: String,
      required: true
    },
    familyId: {
      type: Number,
      required: true
    }
  });
  
  // 当前客户信息
  const currentClient = ref(null);
  
  // 其他客户列表
  const otherClients = ref([]);
  
  // 加载当前客户信息
  const loadCurrentClient = async () => {
    try {
      // 获取当前家庭的 adminId
      const familyResponse = await axios.get(`http://localhost:8081/api/family/find/${props.familyId}`);
      console.log('当前家庭：',familyResponse.data)
      const adminId = familyResponse.data.adminID;
      console.log('adminId:',adminId)
  
      // 获取当前客户信息
      const userResponse = await axios.get(`http://localhost:8081/api/users/${adminId}`);
      currentClient.value = userResponse.data;
      console.log('当前客户信息：',currentClient.value)
    } catch (error) {
      ElMessage.error('加载当前客户信息失败: ' + error.message);
    }
  };
  
  // 加载其他客户信息
  const loadOtherClients = async () => {
    try {
      // 获取所有 role 为 Admin 的用户
      const response = await axios.get('http://localhost:8081/api/users');
      const allAdmins = response.data.filter(user => user.role === 'Admin');
      console.log('所有 role 为 Admin 的用户:',allAdmins)
  
      // 排除当前客户
      if (currentClient.value) {
        otherClients.value = allAdmins.filter(admin => admin.userId !== currentClient.value.userId);
      } else {
        otherClients.value = allAdmins;
      }
    } catch (error) {
      ElMessage.error('加载其他客户信息失败: ' + error.message);
    }
  };
  
  // 监听 familyId 变化
  watch(() => props.familyId, async () => {
    await loadCurrentClient();
    await loadOtherClients();
  });
  
  // 初始化加载数据
  onMounted(async () => {
    await loadCurrentClient();
    await loadOtherClients();
  });



const editDialogVisible = ref(false);
const editForm = ref({
    fullName: '',
    email: '',
    phoneNumber: ''
});
const isSubmitting = ref(false);
const editFormRef = ref(null);

// 添加表单验证规则
const formRules = {
    fullName: [
        { required: true, message: '请输入姓名', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
    ],
    phoneNumber: [
        { required: true, message: '请输入联系电话', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ]
};

// 添加方法
const showEditDialog = () => {
    if (!currentClient.value) {
        ElMessage.warning('请先选择客户');
        return;
    }
    editForm.value = { ...currentClient.value };
    editDialogVisible.value = true;
};

const submitEditForm = async () => {
    try {
        await editFormRef.value.validate();
        isSubmitting.value = true;
        
        const loadingInstance = ElLoading.service({
            lock: true,
            text: '正在提交修改...'
        });

        await axios.put(`http://localhost:8081/api/users/${currentClient.value.userId}`, editForm.value);
        
        ElNotification.success({
            title: '修改成功',
            message: '客户信息已更新'
        });

        // 刷新数据
        await loadCurrentClient();
        await loadOtherClients();
        editDialogVisible.value = false;
    } catch (error) {
        if (error.response) {
            ElMessage.error(`修改失败: ${error.response.data.message}`);
        } else if (!error.toString().includes('validate')) {
            ElMessage.error('网络请求异常');
        }
    } finally {
        isSubmitting.value = false;
        ElLoading.service().close();
    }
};
  </script>
  
  <style scoped>
  .el-card {
    margin: 20px;
  }
  h2 {
    margin-bottom: 20px;
    color: #303133;
  }
  h3 {
    margin: 20px 0;
    color: #606266;
  }
  </style>