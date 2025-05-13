<template>
  <div>
    <!-- 记录支出 -->
    <section id="record-expense">
      <el-card>
        <h2>记录支出</h2>
        <el-form :model="expenseForm" @submit.prevent="saveExpense">
          <el-form-item label="支出金额">
            <el-input v-model="expenseForm.amount" type="number" placeholder="请输入支出金额"></el-input>
          </el-form-item>
          <el-form-item label="支出类别">
            <el-select v-model="expenseForm.category" placeholder="请选择支出类别">
              <el-option
                v-for="(option, index) in expenseCategories"
                :key="index"
                :label="option"
                :value="option"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="支出日期">
            <el-date-picker v-model="expenseForm.date" type="date" placeholder="选择日期"></el-date-picker>
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="expenseForm.description" placeholder="请输入描述"></el-input>
          </el-form-item>
          <el-button type="primary" native-type="submit">保存支出</el-button>
        </el-form>
      </el-card>
    </section>

    <!-- 在记录支出卡片下方添加搜索卡片 -->
    <section id="search-expense" style="margin-top: 20px;">
      <el-card>
        <h3>搜索支出</h3>
        <el-form :model="searchForm">
          <el-form-item label="支出类别">
            <el-select v-model="searchForm.category" placeholder="选择类别">
              <el-option
                v-for="(option, index) in expenseCategories"
                :key="index"
                :label="option"
                :value="option"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="日期">
            <el-date-picker 
              v-model="searchForm.date" 
              type="date" 
              placeholder="选择日期"
              value-format="YYYY-MM-DD"
            ></el-date-picker>
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="searchForm.description" placeholder="输入描述"></el-input>
          </el-form-item>
        </el-form>
      </el-card>
    </section>

    <!-- 显示支出列表 -->
    <section id="expense-list">
      <el-table :data="filteredExpenses" style="width: 100%">
        <el-table-column label="支出金额" prop="amount"></el-table-column>
        <el-table-column label="支出类别" prop="category"></el-table-column>
        <el-table-column label="日期" prop="date" :formatter="formatDate"></el-table-column>
        <el-table-column label="描述" prop="description"></el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="danger" @click="deleteExpense(row.expenseId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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
import { nextTick } from 'vue';
import { computed } from 'vue';

const userInfo = JSON.parse(localStorage.getItem('userInfo'))

const memberId = userInfo.id
// 表单数据
const expenseForm = ref({
  userId: memberId,
  amount: '',
  category: '',
  date: '',
  description: ''
});

const expenseCategories = ref([
  '餐饮',
  '购物',
  '日用',
  '交通',
  '蔬菜',
  '饮品',
  '水果',
  '零食',
  '服饰',
  '外卖',
  '买菜',
  '运动',
  '娱乐',
  '花费',
  '房租',
  '房贷',
  '社交',
  '礼物',
  '旅行',
  '烟酒',
  '汽车',
  '水电费',
  '办公',
  '礼金',
  '转账',
  '麻将',
  '其他',
]);

// 支出数据
const expenses = ref([]);

// 获取支出数据
const fetchExpenses = async () => {
  try {
    const response = await axios.get(`http://localhost:8081/api/expense/${memberId}`);
    expenses.value = response.data;
  } catch (error) {
    console.error('获取支出数据失败', error);
  }
};

// 保存支出数据
const saveExpense = async () => {
  try {
    await axios.post('http://localhost:8081/api/expense', expenseForm.value);
    fetchExpenses(); // 更新支出列表
    expenseForm.value = {
      userId: memberId,
      amount: '',
      category: '',
      date: '',
      description: ''
    }; // 清空表单
  } catch (error) {
    console.error('保存支出失败', error);
  }
};

// 删除支出记录
const deleteExpense = async (expenseId) => {
  try {
    await axios.delete(`http://localhost:8081/api/expense/${expenseId}`);
    fetchExpenses(); // 更新支出列表
  } catch (error) {
    console.error('删除支出失败', error);
  }
};

// 页面加载时获取支出数据
onMounted(() => {
  fetchExpenses();
});

// 添加搜索表单数据
const searchForm = ref({
  category: '',
  date: '',
  description: ''
});

// 计算属性过滤支出列表
const filteredExpenses = computed(() => {
  return expenses.value.filter(expense => {
    const categoryMatch = expense.category.toLowerCase().includes(searchForm.value.category.toLowerCase());
    const dateMatch = searchForm.value.date ? expense.date === searchForm.value.date : true;
    const descMatch = expense.description.toLowerCase().includes(searchForm.value.description.toLowerCase());
    return categoryMatch && dateMatch && descMatch;
  });
});

const formatDate = (row, column, cellValue) => { 
  const date = new Date(cellValue); 
  const year = date.getFullYear(); 
  const month = (date.getMonth() + 1).toString().padStart(2, '0'); 
  const day = date.getDate().toString().padStart(2, '0'); 
  return `${year}-${month}-${day}`; 
}
</script>

<style scoped>
/* 整体容器样式 */
#record-expense, #search-expense, #expense-list {
  padding: 20px;
}

/* 卡片样式 */
.el-card {
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 标题样式 */
h2, h3 {
  color: #333;
  margin-bottom: 20px;
}

/* 表单样式 */
.el-form-item {
  margin-bottom: 15px;
}

/* 按钮样式 */
.el-button {
  border-radius: 4px;
}

.el-button.primary {
  background-color: #409eff;
  color: white;
}

.el-button.danger {
  background-color: #f56c6c;
  color: white;
}

/* 表格样式 */
.el-table {
  margin-top: 20px;
}

.el-table th {
  background-color: #f2f6fc;
  color: #606266;
}
</style>