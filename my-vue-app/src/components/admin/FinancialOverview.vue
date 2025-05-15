<template>
  <div class="financial-overview">
    <el-card class="summary-card">
      <div class="summary-container">
        <div class="summary-item">
          <h3>本月总收入</h3>
          <p class="income">¥ {{ incomeTotal.toLocaleString() }}</p>
        </div>
        <div class="summary-item">
          <h3>本月总支出</h3>
          <p class="expense">¥ {{ expenseTotal.toLocaleString() }}</p>
        </div>
        <div class="summary-item">
          <h3>当前结余</h3>
          <p :class="balanceClass">¥ {{ (incomeTotal - expenseTotal).toLocaleString() }}</p>
        </div>
      </div>
    </el-card>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="12">
        <el-card>
          <h3>收入来源分布</h3>
          <div ref="incomeChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <h3>支出类别分布</h3>
          <div ref="expenseChartRef" style="height: 400px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 新增预算查看部分 -->
    <el-card class="budget-card">
      <h2>家庭预算概览</h2>
      <el-table :data="budgets" style="width: 100%">
        <el-table-column prop="amount" label="预算金额" width="150">
          <template #default="scope">
            {{ formatCurrency(scope.row.amount) }}
          </template>
        </el-table-column>

        <el-table-column prop="startDate" label="开始日期" width="200">
          <template #default="scope">
            {{ formatDate(scope.row.startDate) }}
          </template>
        </el-table-column>

        <el-table-column prop="endDate" label="结束日期" width="200">
          <template #default="scope">
            {{ formatDate(scope.row.endDate) }}
          </template>
        </el-table-column>

        <el-table-column prop="category" label="预算类别" width="150">
          <template #default="scope">
            {{ scope.row.category }}
          </template>
        </el-table-column>

        <el-table-column label="预算进度" width="300">
          <template #default="scope">
            <el-progress 
              :text-inside="true" 
              :stroke-width="24"
              :percentage="calcPercentage(scope.row._categorySpent, scope.row.amount)"
              :status="getProgressStatus(scope.row)"
              :color="customColor"
            >
              <span class="progress-text">
                {{ formatCurrency(scope.row._categorySpent) }} / {{ formatCurrency(scope.row.amount) }}
              </span>
            </el-progress>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick, computed } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'

const userInfo = JSON.parse(localStorage.getItem('userInfo'))
const familyId = ref(userInfo.id)

// const props = defineProps({
//   familyId: {
//     type: Number,
//     required: true
//   }
// })

// 图表实例
let incomeChart = null
let expenseChart = null
const incomeChartRef = ref(null)
const expenseChartRef = ref(null)

// 数据状态
const incomeTotal = ref(0)
const expenseTotal = ref(0)
const budgets = ref([])

// 初始化图表
const initChart = () => {
  incomeChart = echarts.init(incomeChartRef.value)
  expenseChart = echarts.init(expenseChartRef.value)
  
  const commonOptions = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    }
  }

  incomeChart.setOption({
    ...commonOptions,
    series: [{
      type: 'pie',
      data: []
    }]
  })

  expenseChart.setOption({
    ...commonOptions,
    series: [{
      type: 'pie',
      data: []
    }]
  })
}

// 获取财务数据
const fetchFinancialData = async () => {
  try {
    // 获取收入数据
    const incomeRes = await axios.get(`http://localhost:8081/api/income/family/${props.familyId}`)
    incomeTotal.value = incomeRes.data.reduce((sum, item) => sum + item.amount, 0)

    // 获取支出数据
    const expenseRes = await axios.get(`http://localhost:8081/api/expense/family/${props.familyId}`)
    expenseTotal.value = expenseRes.data.reduce((sum, item) => sum + item.amount, 0)

    // 更新图表
    updateCharts(incomeRes.data, expenseRes.data)
    
    // 获取预算数据
    await fetchBudgets()
  } catch (error) {
    console.error('获取财务数据失败:', error)
  }
}

// 获取预算数据
const fetchBudgets = async () => {
  try {
    const response = await axios.get(`http://localhost:8081/api/budgets/family/${props.familyId}`)
    budgets.value = response.data
    
    // 获取每个预算类别的总花费
    for (const budget of budgets.value) {
      const startDate = new Date(budget.startDate)
      const endDate = new Date(budget.endDate)
      const summaryResponse = await axios.get(`http://localhost:8081/api/expense/category-summary/${props.familyId}`, {
        params: {
          startDate: startDate.toISOString().split('T')[0],
          endDate: endDate.toISOString().split('T')[0]
        }
      })
      budget._categorySpent = summaryResponse.data[budget.category] || 0
    }
  } catch (error) {
    console.error("获取预算数据失败:", error)
  }
}

// 更新图表数据
const updateCharts = (incomeData, expenseData) => {
  // 处理收入数据
  const incomeSourceMap = new Map()
  incomeData.forEach(item => {
    const total = incomeSourceMap.get(item.source) || 0
    incomeSourceMap.set(item.source, total + item.amount)
  })

  incomeChart.setOption({
    series: [{
      data: Array.from(incomeSourceMap).map(([name, value]) => ({ name, value }))
    }]
  })

  // 处理支出数据
  const expenseCategoryMap = new Map()
  expenseData.forEach(item => {
    const total = expenseCategoryMap.get(item.category) || 0
    expenseCategoryMap.set(item.category, total + item.amount)
  })

  expenseChart.setOption({
    series: [{
      data: Array.from(expenseCategoryMap).map(([name, value]) => ({ name, value }))
    }]
  })
}

// 计算百分比
const calcPercentage = (spent, amount) => {
  if (!amount || amount <= 0) return 0
  const percentage = (spent / amount) * 100
  return Math.min(percentage, 100).toFixed(1) // 最大显示100%
}

// 获取进度状态
const getProgressStatus = (row) => {
  if (row._categorySpent > row.amount) return 'exception'
  if (row._categorySpent / row.amount >= 0.8) return 'warning'
  return ''
}

const customColor = '#409eff' // 自定义正常状态颜色

// 格式化金额
const formatCurrency = (value) => {
  return '¥' + (value ? Number(value).toFixed(2) : '0.00')
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  const year = date.getFullYear()
  const month = (date.getMonth() + 1).toString().padStart(2, '0')
  const day = date.getDate().toString().padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 计算结余样式
const balanceClass = computed(() => {
  const balance = incomeTotal.value - expenseTotal.value
  return balance >= 0 ? 'positive' : 'negative'
})

// 监听familyId变化
// watch(() => props.familyId, (newVal) => {
//   if (newVal) {
//     fetchFinancialData()
//   }
// })

// 初始化
onMounted(() => {
  nextTick(() => {
    initChart()
  })
})
</script>

<style scoped>
.summary-container {
  display: flex;
  justify-content: space-around;
  padding: 20px 0;
}

.summary-item {
  text-align: center;
}

.summary-item h3 {
  margin-bottom: 10px;
  color: #666;
}

.summary-item p {
  font-size: 24px;
  font-weight: bold;
}

.income {
  color: #67C23A;
}

.expense {
  color: #F56C6C;
}

.positive {
  color: #67C23A;
}

.negative {
  color: #F56C6C;
}

.chart-row {
  margin-top: 20px;
}

.budget-card {
  margin-top: 20px;
}

.progress-text {
  font-size: 12px;
  color: #fff;
}
</style>