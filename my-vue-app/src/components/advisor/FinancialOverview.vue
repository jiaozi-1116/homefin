  <template>
    <section id="view-financial-overview">
      <el-card>
        <h2>{{ advisorName }} - 家庭财务概览（家庭 ID: {{ familyId }}）</h2>
  
        <!-- 按日期汇总 -->
        <el-table :data="financialData" style="width: 100%" border v-loading="loading">
          <el-table-column prop="date" label="日期" width="180" sortable></el-table-column>
          <el-table-column label="收入（元）" width="180">
            <template #default="{ row }">
              <span style="color: #67C23A">{{ row.totalIncome.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="支出（元）" width="180">
            <template #default="{ row }">
              <span style="color: #F56C6C">{{ row.totalExpense.toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="净收入（元）">
            <template #default="{ row }">
              <span :style="{ color: row.netIncome >= 0 ? '#67C23A' : '#F56C6C' }">
                {{ row.netIncome.toFixed(2) }}
              </span>
            </template>
          </el-table-column>
        </el-table>
  
        <!-- 分类统计 -->
        <el-divider></el-divider>
        <h3>分类统计</h3>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-card shadow="hover">
              <h4>收入来源</h4>
              <el-table :data="incomeByCategory" style="width: 100%">
                <el-table-column prop="source" label="来源"></el-table-column>
                <el-table-column prop="amount" label="金额" align="right">
                  <template #default="{ row }">
                    {{ row.amount.toFixed(2) }}
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="hover">
              <h4>支出分类</h4>
              <el-table :data="expenseByCategory" style="width: 100%">
                <el-table-column prop="category" label="类别"></el-table-column>
                <el-table-column prop="amount" label="金额" align="right">
                  <template #default="{ row }">
                    {{ row.amount.toFixed(2) }}
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-col>
        </el-row>
      </el-card>
    </section>
  </template>
  
  <script setup>
  import { ref, onMounted, watch } from 'vue';
  import axios from 'axios';
  import { ElMessage } from 'element-plus';
  
  const props = defineProps({
    advisorName: {
      type: String,
      required: true
    },
    familyId: {
      type: Number,
      required: true
    }
  });

  console.log('家庭 ID:', props.familyId); // 调试日志

  
  // 财务数据状态
  const financialData = ref([]);
  const incomeByCategory = ref([]);
  const expenseByCategory = ref([]);
  const loading = ref(true);
  
// 获取家庭成员的用户ID列表
const getFamilyUserIds = async () => {
  try {
    const res = await axios.get(`http://localhost:8081/api/family/${props.familyId}/members`);
    console.log('家庭成员：',res)
    // 确保返回的数据是数组
    if (Array.isArray(res.data)) {
      return res.data.map(member => member.id); // 提取每个成员的 id
    } else {
      ElMessage.error('返回数据格式不正确');
      return [];
    }
  } catch (error) {
    ElMessage.error('获取家庭成员失败: ' + error.message);
    return [];
  }
};
  
  // 合并财务数据
  const mergeFinancialData = async (userIds) => {
    try {
      // 获取所有用户的收入数据
      const incomePromises = userIds.map(userId => 
        axios.get(`http://localhost:8081/api/income/allIncome/${userId}`).catch(() => ({ data: [] }))
      );
      const incomeResponses = await Promise.all(incomePromises);
      const allIncomes = incomeResponses.flatMap(res => res.data);
      console.log('合并的收入数据：',allIncomes)
  
      // 获取所有用户的支出数据
      const expensePromises = userIds.map(userId => 
        axios.get(`http://localhost:8081/api/expense/allExpense/${userId}`).catch(() => ({ data: [] }))
      );
      const expenseResponses = await Promise.all(expensePromises);
      const allExpenses = expenseResponses.flatMap(res => res.data);
      console.log('合并的支出数据：',allExpenses)
  
      // 按日期汇总
      const dailySummary = {};
      allIncomes.forEach(income => {
        const date = income.date;
        dailySummary[date] = dailySummary[date] || { totalIncome: 0, totalExpense: 0 };
        dailySummary[date].totalIncome += parseFloat(income.amount);
      });
      allExpenses.forEach(expense => {
        const date = expense.date;
        dailySummary[date] = dailySummary[date] || { totalIncome: 0, totalExpense: 0 };
        dailySummary[date].totalExpense += parseFloat(expense.amount);
      });
  
      // 格式化为数组
      financialData.value = Object.entries(dailySummary)
        .map(([date, values]) => ({
          date,
          totalIncome: values.totalIncome,
          totalExpense: values.totalExpense,
          netIncome: values.totalIncome - values.totalExpense
        }))
        .sort((a, b) => new Date(b.date) - new Date(a.date));

        console.log('概览：',financialData.value)
  
      // 分类统计收入
      const incomeCategories = allIncomes.reduce((acc, income) => {
        acc[income.source] = (acc[income.source] || 0) + parseFloat(income.amount);
        return acc;
      }, {});
      incomeByCategory.value = Object.entries(incomeCategories)
        .map(([source, amount]) => ({ source, amount }));
  
      // 分类统计支出
      const expenseCategories = allExpenses.reduce((acc, expense) => {
        acc[expense.category] = (acc[expense.category] || 0) + parseFloat(expense.amount);
        return acc;
      }, {});
      expenseByCategory.value = Object.entries(expenseCategories)
        .map(([category, amount]) => ({ category, amount }));
  
    } catch (error) {
      ElMessage.error('数据处理失败');
    } finally {
      loading.value = false;
    }
  };
  

  // 初始化加载数据
  onMounted(async () => {
    const userIds = await getFamilyUserIds();
    if (userIds.length > 0) {
      console.log('userIds:',userIds)
      await mergeFinancialData(userIds);
    } else {
      loading.value = false;
    }
  });


  
// 新增数据加载方法
const loadData = async () => {
  loading.value = true;
  try {
    const userIds = await getFamilyUserIds();
    if (userIds.length > 0) {
      await mergeFinancialData(userIds);
    }
  } catch (error) {
    ElMessage.error('数据加载失败: ' + error.message);
  } finally {
    loading.value = false;
  }
};

// 监听familyId变化
watch(() => props.familyId, (newVal) => {
  if (newVal) {
    console.log('familyId变化:', newVal);
    loadData();
  }
});

// 初始化加载数据
onMounted(async () => {
  await loadData();
});
  </script>
  
  <style scoped>
  .el-card {
    margin: 20px;
  }
  h2 {
    margin-bottom: 20px;
    color: #303133;
  }
  h3 {
    margin: 20px 0;
    color: #606266;
  }
  </style>