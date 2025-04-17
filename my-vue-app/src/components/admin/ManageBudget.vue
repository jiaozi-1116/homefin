<template>
 <div>
  <!----------------------------------------------------设置和管理预算 -------------------------------------------------->
  <section id="manage-budget">
    <el-card>
      <h2>设置和管理家庭预算</h2>

      <!-- 表格显示预算内容 -->
      <el-table :data="budgets" style="width: 100%">
        <el-table-column prop="amount" label="预算金额" width="150">
          <template #default="scope">
            <el-input v-model="scope.row.amount" type="number"></el-input>
          </template>
        </el-table-column>

        <el-table-column prop="startDate" label="开始日期" width="250">
          <template #default="scope">
            <el-date-picker 
              v-model="scope.row.startDate" 
              type="date" 
              placeholder="选择日期" 
              format="YYYY-MM-DD"
            ></el-date-picker>
          </template>
        </el-table-column>

        <el-table-column prop="endDate" label="结束日期" width="250">
          <template #default="scope">
            <el-date-picker 
              v-model="scope.row.endDate" 
              type="date" 
              placeholder="选择日期" 
              format="YYYY-MM-DD"
            ></el-date-picker>
          </template>
        </el-table-column>

        <el-table-column prop="category" label="预算类别" width="200">
          <template #default="scope">
            <el-select v-model="scope.row.category" placeholder="请选择类别">
              <el-option label="Food" value="Food"></el-option>
              <el-option label="Transportation" value="Transportation"></el-option>
              <el-option label="Entertainment" value="Entertainment"></el-option>
              <el-option label="Clothing" value="Clothing"></el-option>
              <el-option label="Shopping" value="Shopping"></el-option>
            </el-select>
          </template>
        </el-table-column>


        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button type="danger" @click="deleteBudget(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 添加新预算 -->
      <el-button type="primary" @click="addBudget" style="margin-top: 15px;">添加预算</el-button>

      <!-- 保存按钮 -->
      <el-button type="success" @click="saveBudgets" style="margin-top: 15px;">保存所有预算</el-button>
    </el-card>
  </section>
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

  const userInfo = JSON.parse(localStorage.getItem('userInfo'))

  const adminName = ref(userInfo.name)
const adminId = ref(userInfo.id)

let familyID = ref(null);

  const budgets = ref([]);

const fetchBudgets = async () => {
  try {
  const response = await axios.get(`http://localhost:8081/api/family/${adminId.value}`);
  familyID.value = response.data;
} catch (error) {
  console.error('获取家庭ID失败', error);
}

try {
  const response = await axios.get(`http://localhost:8081/api/budgets/family/${familyID.value}`);
  budgets.value = response.data;  // 替换预算数据，确保 UI 更新
} catch (error) {
  console.error("Error fetching budgets:", error);
}
};

onMounted(() =>{
fetchBudgets()
})

// 添加预算方法
const addBudget = () => {
const newBudget = {
  userId: null,
  familyId: familyID.value,
  amount: 0,       // 默认金额为 0
  startDate: '',   // 默认开始日期为空
  endDate: '',     // 默认结束日期为空
  category: '',     // 默认类别为空
  isNew: true,  
};
budgets.value.push(newBudget);  // 向预算列表中添加新项
};

// 删除预算方法
const deleteBudget = async (budget) => {
try {
  await axios.delete(`http://localhost:8081/api/budgets/${budget.budgetId}`);
  console.log('预算删除成功');
  await fetchBudgets();  // 删除后重新获取预算列表
} catch (error) {
  console.error('删除预算时发生错误:', error);
}
};

//保存预算
const saveBudgets = async () => {
try {
  for (const budget of budgets.value) {
    if (budget.isNew == true) {
      // 新增预算，发送 POST 请求
      const response = await axios.post('http://localhost:8081/api/budgets', budget);
      if (response.data) {
        ElMessage.success('新预算已保存');
      }
    } else {
      if (!budget.budgetId) {
        console.error('预算ID未定义，无法更新');
        continue;  // 如果没有 budgetId，跳过该预算
      }
      // 修改已有预算，发送 PUT 请求
      const response = await axios.put(`http://localhost:8081/api/budgets/${budget.budgetId}`, budget);
      if (response.data) {
        ElMessage.success('预算已更新');
      }
    }
  }
  await fetchBudgets();  // 获取更新后的预算数据
} catch (error) {
  console.error('保存预算时发生错误:', error);
  ElMessage.error('保存预算失败');
}
};
</script>