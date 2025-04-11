<template>
    <div class="personal-details">
      <el-card class="profile-card">
        <template #header>
          <div class="card-header">
            <span>个人信息管理</span>
          </div>
        </template>
  
        <el-form 
          :model="userForm" 
          label-width="120px" 
          :rules="rules"
          ref="formRef"
          v-loading="loading"
        >
          <el-form-item label="用户名" prop="username">
            <el-input v-model="userForm.username" disabled></el-input>
          </el-form-item>
  
          <el-form-item label="姓名" prop="fullName">
            <el-input v-model="userForm.fullName"></el-input>
          </el-form-item>
  
          <el-form-item label="电子邮箱" prop="email">
            <el-input v-model="userForm.email" type="email"></el-input>
          </el-form-item>
  
          <el-form-item label="联系电话" prop="phone_number">
            <el-input v-model="userForm.phoneNumber"></el-input>
          </el-form-item>
  
          <el-form-item label="注册日期">
            <el-input :value="formatDate(userForm.dateRegistered)" disabled></el-input>
          </el-form-item>
  
          <el-form-item>
            <el-button type="primary" @click="submitForm">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  import { ElMessage } from 'element-plus'

  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  
  const userForm = ref({
    username: '',
    fullName: '',
    email: '',
    phoneNumber: '',
    dateRegistered: ''
  })
  
  const loading = ref(true)
  const formRef = ref()
  
  const rules = {
    full_name: [
      { required: true, message: '请输入姓名', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '请输入邮箱地址', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱格式', trigger: ['blur', 'change'] }
    ],
    phone_number: [
      { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号码', trigger: 'blur' }
    ]
  }
  
  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const response = await axios.get(`http://localhost:8081/api/users/${userInfo.id}`)
      userForm.value = response.data
    } catch (error) {
      ElMessage.error('获取用户信息失败')
    } finally {
      loading.value = false
    }
  }
  
  // 提交修改
  const submitForm = async () => {
    try {
      await formRef.value.validate()
      await axios.put(`http://localhost:8081/api/users/${userForm.value.userId}`, userForm.value)
      ElMessage.success('修改成功')
      await fetchUserInfo()
    } catch (error) {
      if (!error.response) {
        ElMessage.error('表单验证失败')
      } else {
        ElMessage.error('修改失败')
      }
    }
  }
  
  // 日期格式化
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit'
    })
  }
  
  onMounted(() => {
    fetchUserInfo()
  })
  </script>
  
  <style scoped>
  .personal-details {
    max-width: 800px;
    margin: 20px auto;
    padding: 20px;
  }
  
  .profile-card {
    margin-top: 20px;
  }
  
  .card-header {
    font-size: 18px;
    font-weight: bold;
  }
  
  .el-form {
    margin-top: 20px;
  }
  </style>