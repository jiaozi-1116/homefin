<template>
  <div class="financial-analysis">
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

    <el-card class="suggestion-section">
      <div class="suggestion-header">
        <h2>财务建议</h2>
        <el-button type="primary" @click="showSuggestionDialog = true">提交新建议</el-button>
      </div>
      
      <el-table :data="suggestions" style="width: 100%">
        <el-table-column prop="content" label="建议内容" width="400"></el-table-column>
        <el-table-column prop="dateSubmitted" label="提交日期"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 提交建议对话框 -->
    <el-dialog v-model="showSuggestionDialog" title="提交新建议">
      <el-form :model="newSuggestion">
        <el-form-item label="建议内容">
          <el-input
            v-model="newSuggestion.content"
            type="textarea"
            :rows="4"
            placeholder="请输入您的财务建议"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSuggestionDialog = false">取消</el-button>
        <el-button type="primary" @click="submitSuggestion">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'
import { computed } from 'vue'
import { ElMessage } from 'element-plus'


const userInfo = JSON.parse(localStorage.getItem('userInfo'))


const props = defineProps({
  familyId: {
    type: Number,
    required: true
  }
})

// 图表实例
let incomeChart = null
let expenseChart = null
const incomeChartRef = ref(null)
const expenseChartRef = ref(null)

// 数据状态
const incomeTotal = ref(0)
const expenseTotal = ref(0)
const suggestions = ref([])
const showSuggestionDialog = ref(false)
const newSuggestion = ref({
  content: '',
  familyId: props.familyId,
  userId: userInfo.id, // 需要从登录信息获取实际用户ID
  status:'Pending'
})

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
    console.log('收入数据:',incomeTotal.value)

    
    // 获取支出数据
    const expenseRes = await axios.get(`http://localhost:8081/api/expense/family/${props.familyId}`)
    expenseTotal.value = expenseRes.data.reduce((sum, item) => sum + item.amount, 0)

    // 更新图表
    updateCharts(incomeRes.data, expenseRes.data)
  } catch (error) {
    console.error('获取财务数据失败:', error)
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

// 获取建议列表
const fetchSuggestions = async () => {
  try {
    const res = await axios.get(`http://localhost:8081/api/suggestion/family/${props.familyId}`)
    suggestions.value = res.data.map(item => ({
      ...item,
      dateSubmitted: new Date(item.date_submitted).toLocaleDateString()
    }))
  } catch (error) {
    console.error('获取建议失败:', error)
  }
}

// 提交建议
const submitSuggestion = async () => {
  try {
    await axios.post('http://localhost:8081/api/suggestion/member', newSuggestion.value)
    await fetchSuggestions()
    showSuggestionDialog.value = false
    newSuggestion.value.content = ''
    ElMessage.success('建议提交成功')
  } catch (error) {
    ElMessage.error('提交建议失败')
  }
}

// 状态标签样式
const statusTagType = (status) => {
  return {
    'Pending': 'info',
    'Reviewed': 'warning',
    'Implemented': 'success'
  }[status]
}

// 计算结余样式
const balanceClass = computed(() => {
  const balance = incomeTotal.value - expenseTotal.value
  return balance >= 0 ? 'positive' : 'negative'
})

// 监听familyId变化
watch(() => props.familyId, (newVal) => {
  if (newVal) {
    fetchFinancialData()
    fetchSuggestions()
  }
})

// 初始化
onMounted(() => {
  nextTick(() => {
    initChart()
    if (props.familyId) {
      fetchFinancialData()
      fetchSuggestions()
    }
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

.suggestion-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-row {
  margin-top: 20px;
}
</style>