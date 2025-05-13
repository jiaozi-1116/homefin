<template>
    <div>
      <!-- 页面头部，显示家庭成员信息 -->
      <el-page-header @back="goBack" content="家庭成员页面">
        <template #title>
          家庭成员: {{ memberName }}

          
        </template>

        <el-button type="danger" @click="handleLogout" style="margin-left: 10px;">
          退出登录
        </el-button>
      </el-page-header>


  
      <!-- 横向菜单 -->
      <el-menu 
      :default-active="$route.path" 
      mode="horizontal" 
      router
      >
        <el-menu-item :index="`/member/${memberName}/record-income`">记录收入</el-menu-item>
        <el-menu-item :index="`/member/${memberName}/record-expense`">记录支出</el-menu-item>
        <el-menu-item :index="`/member/${memberName}/chart`">统计图表</el-menu-item>
        <el-menu-item :index="`/member/${memberName}/calendar`">日历</el-menu-item>
        <el-menu-item :index="`/member/${memberName}/suggestion`">财务建议和反馈</el-menu-item>
        
        <!-- <el-menu-item :index="`/member/${memberName}/discussion`">家庭财务讨论</el-menu-item> -->
        <el-menu-item :index="`/member/${memberName}/notice`">家庭公告和通知</el-menu-item>
        <!-- <el-menu-item index="8">查看和管理个人理财项目</el-menu-item> -->
        <el-menu-item :index="`/member/${memberName}/budget`">个人预算</el-menu-item>
        <el-menu-item :index="`/member/${memberName}/manage-account`">个人信息和账户</el-menu-item>
        <el-menu-item :index="`/member/${memberName}/financial-knowledge`">财务知识</el-menu-item>
      </el-menu>
  

      <el-container>
        <el-main>
          <router-view :member-name="memberName" :memberId="memberId"></router-view>
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
  import { nextTick } from 'vue';

//------------------------------------------------------验证登录------------------------
  const router = useRouter()
  
  const route = useRoute()
  // 使用 ref 来存储 adminId 和 adminName
const memberName = ref('')
const memberId = ref('')
const familyID = ref(null); 
// 检查是否有登录信息
const userInfo = JSON.parse(localStorage.getItem('userInfo'))
// 表单数据
const incomeForm = ref({
  userId: '',
  amount: '',
  source: '',
  date: '',
  description: ''
});



onMounted(() => {
  if (userInfo) {
    // 如果有用户信息，恢复用户状态
    console.log('已登录用户:', userInfo)

    memberId.value = userInfo.id
    memberName.value = userInfo.name

    // 更新 incomeForm 的 userId
    // incomeForm.value.userId = memberId.value
    // expenseForm.value.userId = memberId.value
  } else {
    // 如果没有用户信息，跳转到登录页面
    router.push({ name: 'Login' })
  }

console.log('member ID:', memberId.value)
console.log('member Name:', memberName.value)

})

// 如果没有用户信息，你可以在这里使用 route.params 获取 adminId 和 adminName
// if (!userInfo) {
//   memberId.value = route.params.memberId
//   memberName.value = route.params.memberName
// }  
//------------------------------------------------------------------------------------------------------------
 
  const goBack = () => {
    // 返回上一页逻辑
    router.back()
  }
  
  // 新增退出逻辑
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
  /* 整体背景 */
  .el-container {
    background: #f7fafc;
    min-height: 100vh;
  }
  
  /* 头部样式 - 绿色调 */
  .page-header {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 60px;
    z-index: 1000;
    background: linear-gradient(135deg, #48bb78 0%, #38a169 100%);
    box-shadow: 0 2px 15px rgba(72, 187, 120, 0.15);
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
  
  /* 菜单样式 - 浅绿渐变 */
  .el-menu--horizontal {
    margin-top: 60px;
    background: linear-gradient(135deg, #f0fff4 0%, #e6fffa 100%);
    border-bottom: 1px solid #c6f6d5;
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
    background-color: rgba(72, 187, 120, 0.08);
    color: #2f855a;
  }
  
  .el-menu--horizontal > .el-menu-item.is-active {
    color: #2f855a;
    border-bottom: 3px solid #48bb78;
  }
  
  /* 退出按钮 */
  .el-button--danger {
    background: #f56565;
    border-color: #f56565;
    border-radius: 20px;
    padding: 8px 20px;
    transition: all 0.3s ease;
  }
  
  .el-button--danger:hover {
    background: #e53e3e;
    border-color: #e53e3e;
    transform: translateY(-1px);
    box-shadow: 0 3px 12px rgba(229, 62, 62, 0.2);
  }
  
  /* 内容区域 */
  .el-main {
    padding: 100px 30px 30px;
    max-width: 1400px;
    margin: 0 auto;
  }
  </style>
  