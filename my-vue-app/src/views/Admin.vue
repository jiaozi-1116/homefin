<template>
    <div>
      <!-- 页面头部，显示管理员信息 -->
      <el-page-header 
        @back="goBack" 
        content="家庭管理员页面"
        class="page-header"
        >
        <template #title>
          管理员: {{ adminName }}
        </template>

        <template #extra>
          <el-button type="danger" @click="handleLogout">退出登录</el-button>
        </template>
        
      </el-page-header>
      
      <!-- 横向菜单 -->
      <el-menu 
      :default-active="$route.path" 
      mode="horizontal" 
      router
      >
        <el-menu-item :index="'/admin/manage-members'">管理家庭成员信息</el-menu-item>
        <el-menu-item :index="'/admin/manage-budget'">设置和管理家庭预算</el-menu-item>
        <el-menu-item :index="'/admin/financial-report'">查看和分析家庭财务报告</el-menu-item>
        <el-menu-item :index="'/admin/announcements'">发布家庭财务公告和通知</el-menu-item>
        <el-menu-item :index="'/admin/financial-suggestions'">处理家庭成员的财务建议</el-menu-item>
      </el-menu>
  
      <el-container>
        <el-main>
          <router-view :adminId = "adminId"></router-view>
        </el-main>
      </el-container>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useRoute } from 'vue-router'
  import axios from 'axios'
  import { onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus';
  import { ElMessageBox } from 'element-plus'
  import * as echarts from 'echarts';
  import Chart from 'chart.js/auto';
  import { nextTick } from 'vue';

  //------------------------------------------验证登录----------------------------------------------

  const router = useRouter()
  const route = useRoute()
// 使用 ref 来存储 adminId 和 adminName
const adminName = ref('')
const adminId = ref('')

// 检查是否有登录信息
const userInfo = JSON.parse(localStorage.getItem('userInfo'))

onMounted(() => {
  if (userInfo) {
    // 如果有用户信息，恢复用户状态
    console.log('已登录用户:', userInfo)

    adminId.value = userInfo.id
    adminName.value = userInfo.name
  } else {
    // 如果没有用户信息，跳转到登录页面
    router.push({ name: 'Login' })
  }
console.log('Admin ID:', adminId.value)
console.log('Admin Name:', adminName.value)

})

// 如果没有用户信息，你可以在这里使用 route.params 获取 adminId 和 adminName
// if (!userInfo) {
//   adminId.value = route.params.adminId
//   adminName.value = route.params.adminName
// }


  const users = ref([]);  // 存储所有用户

 let familyID = ref(null);

    const handleMenuSelect = (index) => {
    activeMenu.value = index
  }
  const goBack = () => {
    router.back()
    // 返回上一页逻辑
  }


  const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 清除登录状态（根据实际实现调整）
    localStorage.removeItem('token')
    // 跳转到登录页
    router.push('/login')
  }).catch(() => {})
}

  </script>
  

 


  <style scoped>
  /* 固定页面头部到顶部，并使其宽度占满整个浏览器宽度 */
.page-header {
  position: fixed;   /* 固定定位 */
  top: 0;             /* 靠近顶部 */
  left: 0;            /* 靠近左侧 */
  width: 100%;        /* 宽度等于浏览器的宽度 */
  height: 5%;
  z-index: 1000;      /* 确保在其他元素上面 */
  background-color: #fff; /* 可选，确保头部背景为白色 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 可选，添加阴影效果 */
}


.el-menu--horizontal {
  --el-menu-horizontal-height: 70px;
  margin-top: 70px;
}





  </style>
  