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
        <el-menu-item :index="`/member/${memberName}/suggestion`">提交财务建议和反馈</el-menu-item>
        <el-menu-item :index="`/member/${memberName}/discussion`">参与家庭财务讨论</el-menu-item>
        <el-menu-item :index="`/member/${memberName}/notice`">查看家庭公告和通知</el-menu-item>
        <!-- <el-menu-item index="8">查看和管理个人理财项目</el-menu-item> -->
        <el-menu-item :index="`/member/${memberName}/budget`">设置和管理个人预算</el-menu-item>
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
  /* 自定义样式 */
  </style>
  