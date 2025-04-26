<template>
  <div class="register-container">
    <div class="register-form-wrapper">
      <h1 class="register-title">用户注册</h1>
      <el-form :model="registerForm" @submit.prevent="handleRegister" class="register-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="全名" prop="fullName">
          <el-input v-model="registerForm.fullName" placeholder="请输入全名"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="电话号码" prop="phoneNumber">
          <el-input v-model="registerForm.phoneNumber" placeholder="请输入电话号码"></el-input>
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="registerForm.role" placeholder="请选择角色">
            <el-option label="Admin" value="Admin"></el-option>
            <el-option label="Member" value="Member"></el-option>
            <el-option label="Advisor" value="Advisor"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" class="register-button">注册</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const registerForm = ref({
  username: '',
  password: '',
  fullName: '',
  email: '',
  phoneNumber: '',
  role: '' // 添加角色字段
})

const handleRegister = async () => {
  console.log('Register Data:', registerForm.value);
  if (registerForm.value.username && registerForm.value.password && registerForm.value.role) {
    try {
      // 假设API请求地址为 '/api/register'
      const response = await axios.post('http://localhost:8081/api/register', {
        username: registerForm.value.username,
        password: registerForm.value.password,
        fullName: registerForm.value.fullName,
        email: registerForm.value.email,
        phoneNumber: registerForm.value.phoneNumber,
        role: registerForm.value.role // 添加角色字段
      })

      if (response.data.success) {
        ElMessage.success('注册成功')
        // 在这里处理注册成功后的逻辑，例如跳转页面
      } else {
        ElMessage.error(response.data.message)
      }
    } catch (error) {
      ElMessage.error('注册失败，请稍后再试')
    }
  } else {
    ElMessage.error('请填写完整信息')
  }
}

console.log('Register component loaded');
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f4f4f9;
}

.register-form-wrapper {
  background-color: #ffffff;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  width: 400px;
}

.register-title {
  text-align: center;
  color: #333;
  margin-bottom: 30px;
  font-size: 24px;
}

.register-form .el-form-item {
  margin-bottom: 20px;
}

.register-form .el-input,
.register-form .el-select {
  border-radius: 4px;
}

.register-button {
  width: 100%;
  padding: 12px;
  font-size: 16px;
  border-radius: 4px;
}
</style>