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
            <el-input v-model="expenseForm.category" placeholder="请输入支出类别"></el-input>
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
        <el-input v-model="searchForm.category" placeholder="输入类别"></el-input>
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
        <el-table-column label="日期" prop="date"></el-table-column>
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
</script>