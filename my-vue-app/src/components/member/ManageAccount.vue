<template>
  <div class="account-container">
    <!-- 页面标题 -->
    <h1 class="page-title">
      <i class="el-icon-user"></i> 账户信息管理
    </h1>

    <!-- 账户信息卡片 -->
    <el-card shadow="hover" class="account-card">
      <div class="card-header">
        <h2><i class="el-icon-setting"></i> 个人信息设置</h2>
      </div>

      <el-form 
        :model="accountForm" 
        @submit.prevent="saveAccount"
        label-position="top"
        class="account-form"
      >
        <div class="form-grid">
          <!-- 基本信息 -->
          <div class="form-section">
            <h3 class="section-title">
              <i class="el-icon-user-solid"></i> 基本信息
            </h3>
            <el-form-item label="用户名" required>
              <el-input 
                v-model="accountForm.username" 
                placeholder="请输入用户名"
                clearable
              >
                <template #prefix>
                  <i class="el-icon-user"></i>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="全名">
              <el-input 
                v-model="accountForm.fullName" 
                placeholder="请输入您的全名"
                clearable
              >
                <template #prefix>
                  <i class="el-icon-edit"></i>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <!-- 安全信息 -->
          <div class="form-section">
            <h3 class="section-title">
              <i class="el-icon-lock"></i> 安全信息
            </h3>
            <el-form-item label="密码">
              <el-input 
                v-model="accountForm.password" 
                type="password"
                show-password
                placeholder="输入新密码以修改"
                clearable
              >
                <template #prefix>
                  <i class="el-icon-key"></i>
                </template>
              </el-input>
              <div class="form-hint">留空则不修改密码</div>
            </el-form-item>
          </div>

          <!-- 联系方式 -->
          <div class="form-section">
            <h3 class="section-title">
              <i class="el-icon-message"></i> 联系方式
            </h3>
            <el-form-item label="邮箱" required>
              <el-input 
                v-model="accountForm.email" 
                type="email"
                placeholder="请输入有效的邮箱地址"
                clearable
              >
                <template #prefix>
                  <i class="el-icon-message"></i>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item label="电话号码">
              <el-input 
                v-model="accountForm.phoneNumber" 
                type="tel"
                placeholder="请输入电话号码"
                clearable
              >
                <template #prefix>
                  <i class="el-icon-phone"></i>
                </template>
              </el-input>
            </el-form-item>
          </div>
        </div>

        <div class="form-actions">
          <el-button 
            type="primary" 
            native-type="submit"
            :loading="saving"
            class="save-btn"
          >
            <i class="el-icon-check"></i> 保存更改
          </el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const userInfo = JSON.parse(localStorage.getItem('userInfo'))
const memberId = userInfo.id

const accountForm = ref({
  username: '',
  password: '',
  fullName: '',
  email: '',
  phoneNumber: ''
})

const saving = ref(false)

// 获取用户信息并填充到表单中
const fetchUserInfo = async () => {
  try {
    const response = await axios.get(`http://localhost:8081/api/users/${memberId}`)
    // 不显示密码
    accountForm.value = {
      ...response.data,
      password: ''
    }
  } catch (error) {
    console.error("Error fetching user info:", error)
    ElMessage.error('获取用户信息失败')
  }
}

// 保存账户信息
const saveAccount = async () => {
  saving.value = true
  
  try {
    // 如果密码为空，则不更新密码
    const dataToUpdate = {
      ...accountForm.value
    }
    
    if (!dataToUpdate.password) {
      delete dataToUpdate.password
    }

    await axios.put(`http://localhost:8081/api/users/${memberId}`, dataToUpdate)
    ElMessage.success('账户信息更新成功')
    
    // 重新获取用户信息以确认更新
    await fetchUserInfo()
  } catch (error) {
    console.error("Error saving account:", error)
    ElMessage.error('更新账户信息失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.account-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  text-align: center;
  color: #409EFF;
  margin-bottom: 30px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-title i {
  margin-right: 10px;
  font-size: 28px;
}

.account-card {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.account-card:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
}

.card-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.card-header h2 {
  color: #303133;
  font-size: 18px;
  display: flex;
  align-items: center;
  margin: 0;
}

.card-header i {
  margin-right: 10px;
  color: #409EFF;
}

.account-form {
  padding: 0 10px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.form-section {
  margin-bottom: 20px;
}

.section-title {
  color: #606266;
  font-size: 16px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px dashed #ebeef5;
  display: flex;
  align-items: center;
}

.section-title i {
  margin-right: 8px;
  color: #67C23A;
}

.form-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.form-actions {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

.save-btn {
  width: 200px;
  padding: 12px;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  
  .save-btn {
    width: 100%;
  }
}
</style>