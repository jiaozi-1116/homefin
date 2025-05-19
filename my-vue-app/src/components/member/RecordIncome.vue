<template>
  <div class="finance-container">
    <!-- 页面标题 -->
    <h1 class="page-title">
      <i class="el-icon-money"></i> 收入记录
    </h1>

    <div class="grid-layout">
      <!-- 记录收入表单 -->
      <el-card shadow="hover" class="form-card">
        <div class="card-header">
          <h2><i class="el-icon-edit-outline"></i> 新增收入</h2>
        </div>
        <el-form 
          :model="incomeForm" 
          @submit.prevent="saveIncome"
          label-position="top"
        >
          <el-form-item label="收入金额" required>
            <el-input-number 
              v-model="incomeForm.amount" 
              :min="0" 
              :precision="2" 
              :controls="false"
              placeholder="0.00"
              class="amount-input"
            >
              <template #prefix>¥</template>
            </el-input-number>
          </el-form-item>

          <el-form-item label="收入来源" required>
            <el-select 
              v-model="incomeForm.source" 
              placeholder="请选择收入来源"
              clearable
              class="source-select"
            >
              <el-option
                v-for="(option, index) in incomeSources"
                :key="index"
                :label="option"
                :value="option"
              >
                <span class="option-icon">
                  <i :class="getSourceIcon(option)"></i>
                </span>
                <span>{{ option }}</span>
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="收入日期" required>
            <el-date-picker 
              v-model="incomeForm.date" 
              type="date" 
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
              class="date-picker"
            />
          </el-form-item>

          <el-form-item label="描述">
            <el-input 
              v-model="incomeForm.description" 
              placeholder="请输入描述（可选）"
              clearable
            />
          </el-form-item>

          <el-button 
            type="primary" 
            native-type="submit"
            :disabled="!incomeForm.amount || !incomeForm.source || !incomeForm.date"
            class="submit-btn"
          >
            <i class="el-icon-upload"></i> 保存记录
          </el-button>
        </el-form>
      </el-card>

      <!-- 搜索收入 -->
      <el-card shadow="hover" class="search-card">
        <div class="card-header">
          <h2><i class="el-icon-search"></i> 收入查询</h2>
        </div>
        <el-form label-position="top">
          <el-form-item label="收入来源">
            <el-select 
              v-model="searchForm.source" 
              placeholder="选择来源"
              clearable
            >
              <el-option
                v-for="(option, index) in incomeSources"
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

    <!-- 收入列表 -->
    <el-card shadow="hover" class="list-card">
      <div class="card-header">
        <h2><i class="el-icon-tickets"></i> 收入记录</h2>
        <div class="total-amount">
          总收入: <span class="amount">¥{{ totalIncome.toFixed(2) }}</span>
        </div>
      </div>

      <el-table 
        :data="filteredIncomes" 
        stripe
        border
        v-loading="loading"
        class="finance-table"
      >
        <el-table-column label="金额" width="120" align="right">
          <template #default="{row}">
            <span class="income-amount">+¥{{ row.amount.toFixed(2) }}</span>
          </template>
        </el-table-column>

        <el-table-column label="来源" width="150">
          <template #default="{row}">
            <div class="source-cell">
              <i :class="getSourceIcon(row.source)"></i>
              <span>{{ row.source }}</span>
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
              @click="deleteIncome(row.incomeId)"
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

let familyID = ref()

// 表单数据
const incomeForm = ref({
  userId: memberId,
  amount: '',
  source: '',
  date: dayjs().format('YYYY-MM-DD'),
  description: '',
  familyId: familyID.value
})

const incomeSources = ref([
  '工资', '兼职', '生活费', '红包', '年终奖', '奖学金', 
  '礼金', '分红', '理财', '退款', '借入', '卖闲置', 
  '还款', '奖金', '其他'
])

// 收入数据
const incomes = ref([])
const loading = ref(false)

// 搜索表单
const searchForm = ref({
  source: '',
  dateRange: '',
  description: ''
})

// 获取来源图标
const getSourceIcon = (source) => {
  const icons = {
    '工资': 'el-icon-wallet',
    '兼职': 'el-icon-time',
    '红包': 'el-icon-present',
    '理财': 'el-icon-data-line',
    '奖金': 'el-icon-medal',
    '其他': 'el-icon-more'
  }
  return icons[source] || 'el-icon-money'
}

// 获取收入数据
const fetchIncomes = async () => {
  loading.value = true
  try {
    const response = await axios.get(`http://localhost:8081/api/income/${memberId}`)
    incomes.value = response.data
  } catch (error) {
    ElMessage.error('获取收入数据失败')
    console.error('获取收入数据失败', error)
  } finally {
    loading.value = false
  }
}

// 保存收入
const saveIncome = async () => {
  try {
    await axios.post('http://localhost:8081/api/income', incomeForm.value)
    ElMessage.success('收入记录已保存')
    fetchIncomes()
    incomeForm.value = {
      userId: memberId,
      amount: '',
      source: '',
      date: dayjs().format('YYYY-MM-DD'),
      description: '',
      familyId: familyID.value
    }
  } catch (error) {
    ElMessage.error('保存收入失败')
    console.error('保存收入失败', error)
  }
}

// 删除收入
const deleteIncome = async (incomeId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条收入记录吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await axios.delete(`http://localhost:8081/api/income/${incomeId}`)
    ElMessage.success('收入记录已删除')
    fetchIncomes()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除收入失败')
      console.error('删除收入失败', error)
    }
  }
}

// 格式化日期
const formatDate = (row, column, cellValue) => {
  return dayjs(cellValue).format('YYYY-MM-DD')
}

// 计算总收入
const totalIncome = computed(() => {
  return filteredIncomes.value.reduce((sum, item) => sum + item.amount, 0)
})

// 过滤收入列表
const filteredIncomes = computed(() => {
  return incomes.value.filter(income => {
    const sourceMatch = searchForm.value.source 
      ? income.source === searchForm.value.source 
      : true
    
    const dateMatch = searchForm.value.dateRange
      ? dayjs(income.date).isBetween(searchForm.value.dateRange[0], searchForm.value.dateRange[1], null, '[]')
      : true
    
    const descMatch = searchForm.value.description
      ? income.description.toLowerCase().includes(searchForm.value.description.toLowerCase())
      : true
    
    return sourceMatch && dateMatch && descMatch
  })
})

const getFamilyId = async () => {
  const response = await axios.get(`http://localhost:8081/api/member/findFamilyId/${memberId}`);
  familyID.value = response.data
  console.log("jiatingID:",familyID.value)
}

onMounted(async () => {
  try {
    await getFamilyId(); // 确保先获取 familyID
    incomeForm.value.familyId = familyID.value; // 更新表单中的 familyId
    await fetchIncomes();
  } catch (error) {
    console.error('获取数据时出错:', error);
  }
});
</script>

<style scoped>
.finance-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  text-align: center;
  color: #67C23A;
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
  color: #67C23A;
}

.total-amount {
  font-size: 16px;
  color: #606266;
}

.total-amount .amount {
  color: #67C23A;
  font-weight: bold;
}

.amount-input {
  width: 100%;
}

.source-select {
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

.income-amount {
  color: #67C23A;
  font-weight: 500;
}

.source-cell {
  display: flex;
  align-items: center;
}

.source-cell i {
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