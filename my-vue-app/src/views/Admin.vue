<template>
    <div>
      <!-- 页面头部，显示管理员信息 -->
      <el-page-header 
        @back="goBack" 
        content="户主页面"
        class="page-header"
        >
        <template #title>
          户主: {{ adminName }}
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
        <el-menu-item :index="'/admin/manage-members'">家庭成员信息</el-menu-item>
        <el-menu-item :index="'/admin/manage-budget'">家庭预算</el-menu-item>
        <el-menu-item :index="'/admin/detail'">家庭明细</el-menu-item>
        <el-menu-item :index="'/admin/admincalendar'">日历</el-menu-item>
        <el-menu-item :index="'/admin/family-member'">家人收支</el-menu-item>
         <el-menu-item :index="'/admin/financial-overview'">本月财务情况</el-menu-item>
         <el-menu-item :index="'/admin/financial-analysis'">财务分析</el-menu-item>
        <el-menu-item :index="'/admin/announcements'">家庭财务公告和通知</el-menu-item>
        <el-menu-item :index="'/admin/financial-suggestions'">家庭成员的财务建议</el-menu-item>
        <el-menu-item :index="'/admin/publish-materials'">发布财务知识和宣传知识</el-menu-item>
        <el-menu-item :index="'/admin/purchase'">购入申请</el-menu-item>
        <el-menu-item :index="'/admin/personal-information'">个人信息</el-menu-item>
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
  /* 页面整体背景 */
  .el-container {
    background: #f5f7fa;
    min-height: 100vh;
  }
  
  /* 头部样式 */
  .page-header {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 60px;
    z-index: 1000;
    background: linear-gradient(135deg, #4a6fa5 0%, #355081 100%);
    box-shadow: 0 2px 15px rgba(58, 89, 136, 0.15);
    display: flex;
    align-items: center;
    padding: 0 30px;
  }
  
  .page-header :deep(.el-page-header__title),
  .page-header :deep(.el-page-header__content) {
    color: #fff !important;
    font-size: 18px;
    font-weight: 500;
  }
  
  /* 菜单样式 */
  .el-menu--horizontal {
    position: fixed;
    width: 100%;
    margin-top: 60px;
    background: linear-gradient(135deg, #f8f9fc 0%, #e9eff5 100%);
    border-bottom: 1px solid #e4e7ed;
    padding: 0 20px;
  }
  
  .el-menu--horizontal > .el-menu-item {
    height: 60px;
    line-height: 60px;
    margin: 0 15px;
    color: #4a5568;
    font-weight: 500;
    transition: all 0.3s ease;
  }
  
  .el-menu--horizontal > .el-menu-item:hover {
    background-color: rgba(74, 111, 165, 0.1);
    color: #355081;
  }
  
  .el-menu--horizontal > .el-menu-item.is-active {
    color: #355081;
    border-bottom: 3px solid #4a6fa5;
  }
  
  /* 退出按钮样式 */
  .page-header .el-button--danger {
    background: #ff6b6b;
    border-color: #ff6b6b;
    border-radius: 20px;
    padding: 8px 20px;
    transition: all 0.3s ease;
  }
  
  .page-header .el-button--danger:hover {
    background: #ff5252;
    border-color: #ff5252;
    transform: translateY(-1px);
    box-shadow: 0 3px 12px rgba(255, 82, 82, 0.3);
  }
  
  /* 内容区域 */
  .el-main {
    padding: 150px 30px 30px;
    max-width: 1400px;
    margin: 0 auto;
  }
  </style>
  