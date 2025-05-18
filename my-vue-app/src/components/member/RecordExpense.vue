<template>
  <div class="finance-container">
    <!-- 页面标题 -->
    <h1 class="page-title">
      <i class="el-icon-money"></i> 支出记录
    </h1>

    <div class="grid-layout">
      <!-- 记录支出表单 -->
      <el-card shadow="hover" class="form-card">
        <div class="card-header">
          <h2><i class="el-icon-edit-outline"></i> 新增支出</h2>
        </div>
        <el-form 
          :model="expenseForm" 
          @submit.prevent="saveExpense"
          label-position="top"
        >
          <el-form-item label="支出金额" required>
            <el-input-number 
              v-model="expenseForm.amount" 
              :min="0" 
              :precision="2" 
              :controls="false"
              placeholder="0.00"
              class="amount-input"
            >
              <template #prefix>¥</template>
            </el-input-number>
          </el-form-item>

          <el-form-item label="支出类别" required>
            <el-select 
              v-model="expenseForm.category" 
              placeholder="请选择支出类别"
              clearable
              class="category-select"
            >
              <el-option
                v-for="(option, index) in expenseCategories"
                :key="index"
                :label="option"
                :value="option"
              >
                <span class="option-icon">
                  <i :class="getCategoryIcon(option)"></i>
                </span>
                <span>{{ option }}</span>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="支出日期" required>
            <el-date-picker 
              v-model="expenseForm.date" 
              type="date" 
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              class="date-picker"
            />
          </el-form-item>

          <el-form-item label="描述">
            <el-input 
              v-model="expenseForm.description" 
              placeholder="请输入描述（可选）"
              clearable
            />
          </el-form-item>

          <el-button 
            type="primary" 
            native-type="submit"
            :disabled="!expenseForm.amount || !expenseForm.category || !expenseForm.date"
            class="submit-btn"
          >
            <i class="el-icon-upload"></i> 保存记录
          </el-button>
        </el-form>
      </el-card>

      <!-- 搜索支出 -->
      <el-card shadow="hover" class="search-card">
        <div class="card-header">
          <h2><i class="el-icon-search"></i> 支出查询</h2>
        </div>
        <el-form label-position="top">
          <el-form-item label="支出类别">
            <el-select 
              v-model="searchForm.category" 
              placeholder="选择类别"
              clearable
            >
              <el-option
                v-for="(option, index) in expenseCategories"
                :key="index"
                :label="option"
                :value="option"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="日期范围">
            <el-date-picker
              v-model="searchForm.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>

          <el-form-item label="描述关键词">
            <el-input 
              v-model="searchForm.description" 
              placeholder="输入描述关键词"
              clearable
            />
          </el-form-item>
        </el-form>
      </el-card>
    </div>

    <!-- 支出列表 -->
    <el-card shadow="hover" class="list-card">
      <div class="card-header">
        <h2><i class="el-icon-tickets"></i> 支出记录</h2>
        <div class="total-amount">
          总支出: <span class="amount">¥{{ totalExpense.toFixed(2) }}</span>
        </div>
      </div>

      <el-table 
        :data="filteredExpenses" 
        stripe
        border
        v-loading="loading"
        class="finance-table"
      >
        <el-table-column label="金额" width="120" align="right">
          <template #default="{row}">
            <span class="expense-amount">-¥{{ row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="类别" width="150">
          <template #default="{row}">
            <div class="category-cell">
              <i :class="getCategoryIcon(row.category)"></i>
              <span>{{ row.category }}</span>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="日期" width="150" prop="date" :formatter="formatDate" />

        <el-table-column label="描述" prop="description" show-overflow-tooltip />

        <el-table-column label="操作" width="120" align="center">
          <template #default="{row}">
            <el-button 
              type="danger" 
              size="small" 
              plain
              @click="deleteExpense(row.expenseId)"
              class="delete-btn"
            >
              <i class="el-icon-delete"></i> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'

const userInfo = JSON.parse(localStorage.getItem('userInfo'))
const memberId = userInfo.id

// 表单数据
const expenseForm = ref({
  userId: memberId,
  amount: '',
  category: '',
  date: dayjs().format('YYYY-MM-DD'),
  description: ''
})

const expenseCategories = ref([
  '餐饮', '购物', '日用', '交通', '蔬菜', '饮品', '水果', '零食',
  '服饰', '外卖', '买菜', '运动', '娱乐', '花费', '房租', '房贷',
  '社交', '礼物', '旅行', '烟酒', '汽车', '水电费', '办公', '礼金',
  '转账', '麻将', '其他'
])

// 支出数据
const expenses = ref([])
const loading = ref(false)

// 搜索表单
const searchForm = ref({
  category: '',
  dateRange: '',
  description: ''
})

// 获取类别图标
const getCategoryIcon = (category) => {
  const icons = {
    '餐饮': 'el-icon-food',
    '购物': 'el-icon-shopping-cart-2',
    '交通': 'el-icon-truck',
    '房租': 'el-icon-house',
    '水电费': 'el-icon-lightning',
    '旅行': 'el-icon-location-outline',
    '娱乐': 'el-icon-video-camera',
    '运动': 'el-icon-basketball',
    '礼物': 'el-icon-present',
    '其他': 'el-icon-more'
  }
  return icons[category] || 'el-icon-wallet'
}

// 获取支出数据
const fetchExpenses = async () => {
  loading.value = true
  try {
    const response = await axios.get(`http://localhost:8081/api/expense/${memberId}`)
    expenses.value = response.data
  } catch (error) {
    ElMessage.error('获取支出数据失败')
    console.error('获取支出数据失败', error)
  } finally {
    loading.value = false
  }
}

// 保存支出
const saveExpense = async () => {
  try {
    await axios.post('http://localhost:8081/api/expense', expenseForm.value)
    ElMessage.success('支出记录已保存')
    fetchExpenses()
    expenseForm.value = {
      userId: memberId,
      amount: '',
      category: '',
      date: dayjs().format('YYYY-MM-DD'),
      description: ''
    }
  } catch (error) {
    ElMessage.error('保存支出失败')
    console.error('保存支出失败', error)
  }
}

// 删除支出
const deleteExpense = async (expenseId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条支出记录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await axios.delete(`http://localhost:8081/api/expense/${expenseId}`)
    ElMessage.success('支出记录已删除')
    fetchExpenses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除支出失败')
      console.error('删除支出失败', error)
    }
  }
}

// 格式化日期
const formatDate = (row, column, cellValue) => {
  return dayjs(cellValue).format('YYYY-MM-DD')
}

// 计算总支出
const totalExpense = computed(() => {
  return filteredExpenses.value.reduce((sum, item) => sum + item.amount, 0)
})

// 过滤支出列表
const filteredExpenses = computed(() => {
  return expenses.value.filter(expense => {
    const categoryMatch = searchForm.value.category 
      ? expense.category === searchForm.value.category 
      : true
    
    const dateMatch = searchForm.value.dateRange
      ? dayjs(expense.date).isBetween(searchForm.value.dateRange[0], searchForm.value.dateRange[1], null, '[]')
      : true
    
    const descMatch = searchForm.value.description
      ? expense.description.toLowerCase().includes(searchForm.value.description.toLowerCase())
      : true
    
    return categoryMatch && dateMatch && descMatch
  })
})

onMounted(() => {
  fetchExpenses()
})
</script>

<style scoped>
.finance-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  text-align: center;
  color: #F56C6C;
  margin-bottom: 30px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
}

.page-title i {
  margin-right: 10px;
  font-size: 28px;
}

.grid-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.card-header h2 {
  color: #303133;
  font-size: 18px;
  display: flex;
  align-items: center;
  margin: 0;
}

.card-header i {
  margin-right: 10px;
  color: #F56C6C;
}

.total-amount {
  font-size: 16px;
  color: #606266;
}

.total-amount .amount {
  color: #F56C6C;
  font-weight: bold;
}

.amount-input {
  width: 100%;
}

.category-select {
  width: 100%;
}

.date-picker {
  width: 100%;
}

.submit-btn {
  width: 100%;
  margin-top: 10px;
}

.finance-table {
  margin-top: 10px;
}

.expense-amount {
  color: #F56C6C;
  font-weight: 500;
}

.category-cell {
  display: flex;
  align-items: center;
}

.category-cell i {
  margin-right: 8px;
  color: #606266;
}

.delete-btn {
  padding: 7px 12px;
}

.option-icon {
  margin-right: 8px;
  color: #606266;
}

@media (max-width: 992px) {
  .grid-layout {
    grid-template-columns: 1fr;
  }
}
</style>