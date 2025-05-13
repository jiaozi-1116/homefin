<template>
  <div class="admin-calendar-page">
    <div class="page-header">
      <h1>家庭财务日历管理</h1>
      <div class="member-selector">
        <el-select
          v-model="selectedMember"
          placeholder="选择家庭成员"
          value-key="id" 
        >
        
        
          <el-option
            v-for="member in familyMembers"
            :key="member.id"
            :label="`${member.name} (${member.relationship})`"
            :value="member"
          />
        </el-select>
      </div>
    </div>

    <!-- 当选择成员后显示成员日历 -->
    <div v-if="selectedMember" class="calendar-content">
      <div class="calendar-header">
        <el-button-group>
          <el-button @click="prevYear"><i class="el-icon-arrow-left"></i></el-button>
          <el-button type="primary" disabled>{{ currentYear }}年</el-button>
          <el-button @click="nextYear"><i class="el-icon-arrow-right"></i></el-button>
        </el-button-group>
        
        <div class="view-switch">
          <el-radio-group v-model="viewType">
            <el-radio-button label="month">月视图</el-radio-button>
            <el-radio-button label="year">年视图</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 月视图 -->
      <el-calendar v-if="viewType === 'month'" v-model="currentDate">
        <template #date-cell="{ data }">
          <div 
            class="cell-content" 
            @click="handleDateClick(data.day)"
            :class="{ 'has-data': hasDailyData(data.day) }"
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
      <div v-else class="year-view">
        <div
          class="month-card"
          v-for="month in 12"
          :key="month"
          @click="handleMonthClick(month)"
          :class="{ 'selected': selectedMonth === month, 'has-data': hasMonthlyData(month) }"
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
        <h3>{{ selectedDateTitle }} 的收支记录</h3>
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

    <!-- 未选择成员时的提示 -->
    <div v-else class="empty-state">
      <el-empty description="请选择一个家庭成员查看其财务日历" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 获取管理员信息
const userInfo = JSON.parse(localStorage.getItem('userInfo'))
const adminId = userInfo.id

// 状态管理
const familyMembers = ref([]) // 家庭成员列表
const selectedMember = ref(null) // 当前选择的家庭成员
const viewType = ref('month') // 视图类型：month/year
const currentDate = ref(new Date()) // 当前查看的日期
const selectedMonth = ref(null) // 年视图中选中的月份
const selectedDate = ref('') // 选中的日期
const financialData = reactive({
  daily: {},   // {日期: {expense, income}}
  monthly: {}  // {月份: {expense, income}}
})
const records = ref([]) // 原始记录数据

// 计算属性
const currentYear = computed(() => currentDate.value.getFullYear())

// 当前显示的记录
const currentRecords = computed(() => {
  if (viewType.value === 'month') {
    return records.value.filter(item => item.date.startsWith(selectedDate.value))
  } else {
    // 年视图显示整月记录
    return records.value.filter(item => 
      item.date.startsWith(`${currentYear.value}-${String(selectedMonth.value).padStart(2, '0')}`)
    )
  }
})

// 选中日期的标题
const selectedDateTitle = computed(() => {
  if (viewType.value === 'month') {
    return selectedDate.value
  } else {
    return `${currentYear.value}年${selectedMonth.value}月`
  }
})

// 加载家庭成员
const loadFamilyMembers = async () => {
  try {
    const { data } = await axios.get(`http://localhost:8081/api/family/${adminId}/members`)
    familyMembers.value = data
    console.log('家庭成员加载成功:', data)
    
    // 如果有家庭成员，默认选择第一个
    if (data.length > 0) {
      selectedMember.value = { ...data[0] } // 创建对象副本
    }
  } catch (error) {
    console.error('加载家庭成员失败:', error)
    ElMessage.error('获取家庭成员信息失败')
  }
}



// 获取每日金额
const getAmount = (date, type) => {
  return financialData.daily[date]?.[type] || 0
}

// 获取每月金额
const getMonthAmount = (month, type) => {
  const key = `${currentYear.value}-${String(month).padStart(2, '0')}`
  return financialData.monthly?.[key]?.[type] || 0
}

// 检查某天是否有数据
const hasDailyData = (date) => {
  return financialData.daily[date] && (financialData.daily[date].expense > 0 || financialData.daily[date].income > 0)
}

// 检查某月是否有数据
const hasMonthlyData = (month) => {
  const key = `${currentYear.value}-${String(month).padStart(2, '0')}`
  return financialData.monthly?.[key] && (financialData.monthly[key].expense > 0 || financialData.monthly[key].income > 0)
}

// 处理日期点击
const handleDateClick = (date) => {
  selectedDate.value = date
  selectedMonth.value = null // 清除月份选择
  loadRecords(date)
}

// 处理月份点击
const handleMonthClick = (month) => {
  selectedMonth.value = month
  selectedDate.value = '' // 清除日期选择
  loadRecords(`${currentYear.value}-${String(month).padStart(2, '0')}`)
}

// 加载财务数据
const loadFinancialData = async () => {
  if (!selectedMember.value) return
  
  try {
    console.log('加载财务数据:', selectedMember.value.id, currentYear.value)
    const { data } = await axios.get('http://localhost:8081/api/calendar/personal-financial-data', {
      params: {
        userId: selectedMember.value.id,
        year: currentYear.value
      }
    })
    console.log('财务数据加载成功:', data)
    Object.assign(financialData, data)
  } catch (error) {
    console.error('加载失败:', error)
    ElMessage.error('数据加载失败')
  }
}

// 加载详细记录
const loadRecords = async (date = '') => {
  if (!selectedMember.value) return
  
  try {
    console.log('加载记录:', selectedMember.value.id, date)
    const { data } = await axios.get('http://localhost:8081/api/calendar/personal-records', {
      params: {
        userId: selectedMember.value.id,
        date: date || (viewType.value === 'month' 
          ? new Date().toISOString().split('T')[0] 
          : `${currentYear.value}-${String(new Date().getMonth() + 1).padStart(2, '0')}`)
      }
    })
    records.value = data
  } catch (error) {
    console.error('加载记录失败:', error)
    ElMessage.error('记录加载失败')
  }
}

// 年份切换
const prevYear = () => {
  currentDate.value = new Date(currentYear.value - 1, currentDate.value.getMonth(), 1)
  loadFinancialData()
}

const nextYear = () => {
  currentDate.value = new Date(currentYear.value + 1, currentDate.value.getMonth(), 1)
  loadFinancialData()
}

onMounted(() => {
  loadFamilyMembers()
})

// 监听视图类型变化
watch(viewType, () => {
  if (viewType.value === 'month') {
    selectedMonth.value = null
    selectedDate.value = new Date().toISOString().split('T')[0]
    loadRecords(selectedDate.value)
  } else {
    selectedDate.value = ''
    selectedMonth.value = new Date().getMonth() + 1
    loadRecords(`${currentYear.value}-${String(selectedMonth.value).padStart(2, '0')}`)
  }
})

// 监听当前年份变化
watch(currentYear, () => {
  loadFinancialData()
  if (viewType.value === 'month') {
    loadRecords(selectedDate.value || new Date().toISOString().split('T')[0])
  } else {
    loadRecords(`${currentYear.value}-${String(selectedMonth.value).padStart(2, '0')}`)
  }
})

// 监听成员变化
watch(selectedMember, (newMember) => {
  if (newMember) {
    console.log('成员变更:', newMember)
    loadFinancialData()
    loadRecords()
  }
})
</script>

<style scoped>
.admin-calendar-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.member-selector {
  width: 300px;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.month-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}

.month-card.selected {
  background-color: #e0f7fa;
  border-color: #00acc1;
}

.month-card.has-data {
  border-color: #b3e5fc;
}

.cell-content {
  height: 100%;
  padding: 8px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.cell-content:hover {
  background-color: #f5f7fa;
}

.cell-content.has-data {
  background-color: #e8f5e9;
}

.cell-content.has-data:hover {
  background-color: #dcedc8;
}

.amounts {
  font-size: 12px;
  line-height: 1.5;
  margin-top: 5px;
}

.expense {
  color: #f56c6c;
}

.income {
  color: #67c23a;
}

.month-amount {
  font-size: 14px;
  text-align: center;
  margin-top: 10px;
}

.records {
  margin-top: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.empty-state {
  padding: 60px 20px;
  text-align: center;
}
</style>