<template>
    <div class="calendar-container">
      <div class="view-switch">
        <el-radio-group v-model="viewType">
          <el-radio-button label="month">月视图</el-radio-button>
          <el-radio-button label="year">年视图</el-radio-button>
        </el-radio-group>
      </div>
  
      <!-- 月视图 -->
      <el-calendar v-if="viewType === 'month'" v-model="currentDate">
        <template #date-cell="{ data }">
          <div 
            class="cell-content" 
            @click="handleDateClick(data.day)"
            >
            <div class="date">{{ data.day.split('-').slice(2).join('-') }}</div>
            <div class="amounts">
              <div class="expense">-{{ getAmount(data.day, 'expense') }}</div>
              <div class="income">+{{ getAmount(data.day, 'income') }}</div>
            </div>
          </div>
        </template>
      </el-calendar>
  
      <!-- 年视图 -->
      <!-- <div v-else class="year-view">
        <div v-for="month in 12" :key="month" class="month-card">
          <el-calendar 
            :value="new Date(currentDate.getFullYear(), month-1)"
            :range="getMonthRange(month)"
          >
            <template #header>
              <span>{{ month }}月</span>
            </template>
            <template #date-cell="{ data }">
              <div class="cell-content" @click="handleMonthClick(month)">
                <div v-if="isCurrentMonth(data.day, month)" class="month-amount">
                  <div>-{{ getMonthAmount(month, 'expense') }}</div>
                  <div>+{{ getMonthAmount(month, 'income') }}</div>
                </div>
              </div>
            </template>
          </el-calendar>
        </div>
      </div> -->
      <div v-else class="year-view">
    <div
      class="month-card"
      v-for="month in 12"
      :key="month"
      @click="handleMonthClick(month)"
      :class="{ 'selected': selectedMonth === month }"
    >
      <div class="month-title">{{ month }} 月</div>
      <div class="month-amount">
        <div class="expense">-{{ getMonthAmount(month, 'expense') }}</div>
        <div class="income">+{{ getMonthAmount(month, 'income') }}</div>
      </div>
    </div>
  </div>
  
      <!-- 收支记录 -->
      <div class="records">
        <h3>{{ selectedDate }} 的收支记录</h3>
        <el-table :data="currentRecords" style="width: 100%">
          <el-table-column prop="date" label="日期" width="120"/>
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{row}">
              <el-tag :type="row.type === '支出' ? 'danger' : 'success'">
                {{ row.type }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="amount" label="金额" width="120"/>
          <el-table-column prop="category" label="分类"/>
          <el-table-column prop="description" label="描述"/>
        </el-table>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, computed, onMounted  } from 'vue'
  import { ElMessage } from 'element-plus'
    import axios from 'axios'
  
  const viewType = ref('month')
  const currentDate = ref(new Date())
  const selectedDate = ref('')
  const financialData = reactive({
    daily: {},   // {日期: {expense, income}}
    monthly: {}  // {月份: {expense, income}}
  })
  const records = ref([]) // 原始记录数据

  const userInfo = JSON.parse(localStorage.getItem('userInfo'))

const memberId = userInfo.id
  
  // 当前显示的记录
  const currentRecords = computed(() => {
    return records.value.filter(item => item.date.startsWith(selectedDate.value))
  })
  
  // 获取每日金额
  const getAmount = (date, type) => {
    return financialData.daily[date]?.[type] || 0
  }
  
  // 获取每月金额
  const getMonthAmount = (month, type) => {
  const key = `${currentDate.value.getFullYear()}-${String(month).padStart(2, '0')}`
  return financialData.monthly?.[key]?.[type] || 0
}

  
  // 处理日期点击
  const handleDateClick = (date) => {
    selectedDate.value = date
    loadRecords(date)
  }
  
  const selectedMonth = ref(null);
  
  // 处理月份点击
  const handleMonthClick = (month) => {
    
    const year = currentDate.value.getFullYear()
    selectedDate.value = `${year}-${month.toString().padStart(2, '0')}`
    loadRecords(`${year}-${month.toString().padStart(2, '0')}`)
    selectedMonth.value = month;
  }
  
  // 加载财务数据
  const loadFinancialData = async () => {
    try {
        console.log('成功加载财务数据:', memberId,currentDate.value.getFullYear());
      const { data } = await axios.get('http://localhost:8081/api/calendar/personal-financial-data', {
        params: {
            userId:memberId,
          year: currentDate.value.getFullYear()
        }
      })
      console.log('成功加载财务数据:', data);
      Object.assign(financialData, data)
    } catch (error) {
  console.error('加载失败:', error);
  if (error.response) {
    console.error('响应错误:', error.response.status, error.response.data);
  } else if (error.request) {
    console.error('请求没有响应:', error.request);
  } else {
    console.error('设置错误:', error.message);
  }
  ElMessage.error('数据加载失败');
}
  }
  
  // 加载详细记录
  const loadRecords = async (date) => {
    try {
      const { data } = await axios.get('http://localhost:8081/api/calendar/personal-records', {
        params: {
            userId:memberId,
          date
        }
      })
      records.value = data
    } catch (error) {
      ElMessage.error('记录加载失败')
    }
  }
  
  // 辅助方法：获取月份范围
  const getMonthRange = (month) => {
    const year = currentDate.value.getFullYear()
    const start = new Date(year, month-1, 1)
    const end = new Date(year, month, 0)
    return [start, end]
  }
  
  // 辅助方法：判断是否当前月份
  const isCurrentMonth = (dateStr, month) => {
    const date = new Date(dateStr)
    return date.getMonth() + 1 === month
  }
  
  onMounted(() => {
    loadFinancialData()
    loadRecords(new Date().toISOString().split('T')[0])
  })
  </script>
  
  <style scoped>
  .calendar-container {
    padding: 20px;
  }
  
  .view-switch {
    margin-bottom: 20px;
  }
  
  .year-view {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }
  
  .month-card {
    border: 1px solid #ebeef5;
    border-radius: 4px;
    padding: 10px;
   
  }
  
  .cell-content {
    height: 100%;
    padding: 5px;
    cursor: pointer;
  }
  
  .amounts {
    font-size: 12px;
    line-height: 1.5;
  }
  
  .expense {
    color: #f56c6c;
  }
  
  .income {
    color: #67c23a;
  }
  
  .month-amount {
    font-size: 12px;
    text-align: center;
  }
  
  .records {
    margin-top: 30px;
  }

  .month-card.selected {
  background-color: #e0f7fa; /* 选中时的背景颜色 */
  border-color: #00acc1; /* 选中时的边框颜色 */
}
  </style>