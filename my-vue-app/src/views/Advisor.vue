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