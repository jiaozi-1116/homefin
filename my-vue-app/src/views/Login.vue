<template>
  <div class="login-container">
    <el-form :model="loginForm" @submit.prevent="handleLogin" class="login-form">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit">登录</el-button>
      </el-form-item>
      <el-form-item>
        <el-button type="default" @click="navigateToRegister">注册</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

  
  <script setup>
  import { ref } from 'vue'
  import { ElMessage } from 'element-plus'
  import axios from 'axios'
  import { useRouter } from 'vue-router'
  
  const loginForm = ref({
    username: '',
    password: ''
  })

  const router = useRouter()
  
  const handleLogin = async () => {
    console.log('Login Data:', loginForm.value);
    if (loginForm.value.username && loginForm.value.password) {
      try {
        // 假设API请求地址为 '/api/login'
        const response = await axios.post('http://localhost:8081/api/login', {
          username: loginForm.value.username,
          password: loginForm.value.password
        })
        
        if (response.data.success) {
          ElMessage.success('登录成功')

        // 保存用户信息到 localStorage
        const userInfo = {
          // username: loginForm.value.username,
          role: response.data.role,
          id: response.data.id,
          name: response.data.name
        };
        localStorage.setItem('userInfo', JSON.stringify(userInfo));  // 将用户信息存储为字符串

          const userRole = response.data.role // 根据角色信息跳转到相应页面 
          console.log('User Role:', userRole) // 打印角色信息
          if (userRole === 'Admin') { 
            router.push({ name: 'Admin', params: { 
              adminName: response.data.name,
              adminId: response.data.id
             } })
          } else if (userRole === 'Member') { 
              router.push({name: 'Member', params: {
                memberName: response.data.name,
                memberId: response.data.id
              }}) // 家庭成员页面 
          } else if (userRole === 'Advisor') { 
            router.push({ name: 'Advisor', params: { 
              advisorName: response.data.name,
              advisorId:response.data.id
            } }) // 财务顾问页面 
          } else { 
            ElMessage.error('未知角色') 
          }
        } else {
          ElMessage.error('用户名或密码错误')
        }
      } catch (error) {
        ElMessage.error('登录失败，请稍后再试')
      }
    } else {
      ElMessage.error('请填写用户名和密码')
    }
  }

  const navigateToRegister = () => { 
    router.push('/register') // 假设注册页面的路由为 '/register' 
    }



  </script>
  
  <style scoped>
/* 设置全屏居中 */
.login-container {
  display: flex;
  justify-content: center;  /* 水平居中 */
  align-items: center;      /* 垂直居中 */
  height: 100vh;            /* 使用全屏高度 */
  background-color: #f5f5f5; /* 背景颜色，视需要调整 */
}

/* 设置表单的宽度和适应性 */
.login-form {
  width: 100%;
  max-width: 400px;         /* 最大宽度400px */
  padding: 20px;            /* 内边距 */
  background-color: white;  /* 白色背景 */
  border-radius: 8px;       /* 圆角 */
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); /* 阴影效果 */
}

/* 输入框和按钮的自适应调整 */
.el-input, .el-button {
  width: 100%;  /* 输入框和按钮宽度占满容器 */
}

/* 输入框和按钮的样式微调 */
.el-input,
.el-button {
  margin-bottom: 15px; /* 每个控件底部的间距 */
}

.el-button[type="default"] {
  margin-top: 10px;
}
  </style>
  