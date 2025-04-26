<template>
  <div>
    <el-page-header @back="goBack" content="财务顾问页面">
      <template #title>
        财务顾问: {{ advisorName }}
      </template>
      <template #extra>
        <el-select
          v-model="selectedFamily"
          placeholder="请选择家庭"
          style="width: 200px; margin-right: 20px;"
          @change="handleFamilyChange"
        >
          <el-option
            v-for="family in families"
            :key="family.familyID"
            :label="family.familyName"
            :value="family.familyID"
          />
        </el-select>
        <el-button type="danger" @click="handleLogout" style="margin-left: 10px;">
          退出登录
        </el-button>
      </template>
    </el-page-header>

    <el-menu 
      :default-active="$route.path" 
      mode="horizontal" 
      router
    >
      <el-menu-item :index="`/advisor/${advisorName}/financial-overview`">查看财务情况</el-menu-item>
      <el-menu-item :index="`/advisor/${advisorName}/financial-analysis`">财务分析和建议</el-menu-item>
      <el-menu-item :index="`/advisor/${advisorName}/generate-reports`">生成财务报告和规划</el-menu-item>
      <!-- <el-menu-item :index="`/advisor/${advisorName}/online-consultation`">在线沟通和咨询</el-menu-item>
      <el-menu-item :index="`/advisor/${advisorName}/recommend-products`">推荐理财产品和方案</el-menu-item> -->
      <el-menu-item :index="`/advisor/${advisorName}/manage-clients`">管理客户档案和信息</el-menu-item>
      <el-menu-item :index="`/advisor/${advisorName}/meetings`">参与家庭财务会议和讨论</el-menu-item>
      <el-menu-item :index="`/advisor/${advisorName}/publish-materials`">发布财务知识和宣传材料</el-menu-item>
      <el-menu-item :index="`/advisor/${advisorName}/personal-details`">个人信息</el-menu-item>
    </el-menu>

    <el-container>
      <el-main>
        <router-view :advisor-name="advisorName" :familyId="selectedFamily"></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { ref , onMounted} from 'vue';
import axios from 'axios';
import { ElMessageBox } from 'element-plus';
const router = useRouter()
const route = useRoute()

const advisorName = route.params.advisorName

const goBack = () => {
  router.back()
}



// 家庭选择框数据
const selectedFamily = ref('');

const families = ref([]);

onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8081/api/family');
    families.value = response.data;
  } catch (error) {
    console.error('获取家庭数据失败:', error);
  }
});

// 处理家庭选择变化
const handleFamilyChange = (value) => {
  console.log('选择的家庭:', value);
  // 这里可以添加逻辑，例如根据选择的家庭加载数据
};

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
  background: #f8fafc;
  min-height: 100vh;
}

/* 头部样式 - 深靛蓝色调 */
.page-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 60px;
  z-index: 1000;
  background: linear-gradient(135deg, #2b6cb0 0%, #2c5282 100%);
  box-shadow: 0 2px 15px rgba(43, 108, 176, 0.15);
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

/* 家庭选择器样式 */
.el-select {
  --el-select-input-focus-border-color: #4c51bf;
}
.el-select:hover :deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px #4c51bf inset !important;
}

/* 菜单样式 - 浅靛蓝渐变 */
.el-menu--horizontal {
  margin-top: 60px;
  background: linear-gradient(135deg, #f8fafc 0%, #ebf4ff 100%);
  border-bottom: 1px solid #c3dafe;
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
  background-color: rgba(76, 81, 191, 0.08);
  color: #2b6cb0;
}

.el-menu--horizontal > .el-menu-item.is-active {
  color: #2c5282;
  border-bottom: 3px solid #4c51bf;
}

/* 退出按钮 */
.el-button--danger {
  background: #fc8181;
  border-color: #fc8181;
  border-radius: 20px;
  padding: 8px 20px;
  transition: all 0.3s ease;
}

.el-button--danger:hover {
  background: #f56565;
  border-color: #f56565;
  transform: translateY(-1px);
  box-shadow: 0 3px 12px rgba(245, 101, 101, 0.2);
}

/* 内容区域 */
.el-main {
  padding: 100px 30px 30px;
  max-width: 1400px;
  margin: 0 auto;
}
</style>