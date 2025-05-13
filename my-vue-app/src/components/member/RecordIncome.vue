<template>
  <div>
    <!-- 记录收入 -->
    <section id="record-income">
      <el-card>
        <h2>记录收入</h2>
        <el-form :model="incomeForm" @submit.prevent="saveIncome">
          <el-form-item label="收入金额">
            <el-input v-model="incomeForm.amount" type="number" placeholder="请输入收入金额"></el-input>
          </el-form-item>
          <el-form-item label="收入来源">
            <el-select v-model="incomeForm.source" placeholder="请选择收入来源">
              <el-option
                v-for="(option, index) in incomeSources"
                :key="index"
                :label="option"
                :value="option"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="收入日期">
            <el-date-picker v-model="incomeForm.date" type="date" placeholder="选择日期"></el-date-picker>
          </el-form-item>
          <el-form-item label="描述">
            <el-input v-model="incomeForm.description" placeholder="请输入描述"></el-input>
          </el-form-item>
          <el-button type="primary" native-type="submit">保存收入</el-button>
        </el-form>
      </el-card>
    </section>

    <!-- 在记录收入卡片下方添加搜索卡片 -->
    <section id="search-income" style="margin-top: 20px;">
      <el-card>
        <h3>搜索收入</h3>
        <el-form :model="searchForm">
          <el-form-item label="收入来源">
            <el-select v-model="searchForm.source" placeholder="选择来源">
              <el-option
                v-for="(option, index) in incomeSources"
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

    <!-- 显示收入列表 -->
    <section id="income-list">
      <el-table :data="filteredIncomes" style="width: 100%">
        <el-table-column label="收入金额" prop="amount"></el-table-column>
        <el-table-column label="收入来源" prop="source"></el-table-column>
        <el-table-column label="日期" prop="date" :formatter="formatDate"></el-table-column>
        <el-table-column label="描述" prop="description"></el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="danger" @click="deleteIncome(row.incomeId)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { useRoute } from 'vue-router';
import axios from 'axios';
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { ElMessageBox } from 'element-plus';
import * as echarts from 'echarts';
import { nextTick } from 'vue';
import { computed } from 'vue';

//   const props = defineProps({
//   memberId: {
//     type: Number,
//     required: true
//   }
// })
const incomeSources = ref([
  '工资',
  '兼职',
  '生活费',
  '红包',
  '年终奖',
  '奖学金',
  '礼金',
  '分红',
  '理财',
  '退款',
  '借入',
  '卖闲置',
  '还款',
  '奖金',
  '其他',
]);
const userInfo = JSON.parse(localStorage.getItem('userInfo'));

const memberId = userInfo.id;

const incomeForm = ref({
  userId: memberId,
  amount: '',
  source: '',
  date: '',
  description: '',
});

const incomes = ref([]);

// 获取收入数据
const fetchIncomes = async () => {
  try {
    const response = await axios.get(`http://localhost:8081/api/income/${memberId}`);
    incomes.value = response.data;
  } catch (error) {
    console.error('获取收入数据失败', error);
  }
};

// 保存收入数据
const saveIncome = async () => {
  try {
    await axios.post('http://localhost:8081/api/income', incomeForm.value);
    console.log('添加的收入：', incomeForm.value);
    fetchIncomes(); // 更新收入列表
    incomeForm.value = {
      userId: memberId,
      amount: '',
      source: '',
      date: '',
      description: '',
    }; // 清空表单
  } catch (error) {
    console.error('保存收入失败', error);
  }
};

// 删除收入记录
const deleteIncome = async (incomeId) => {
  try {
    await axios.delete(`http://localhost:8081/api/income/${incomeId}`);
    fetchIncomes(); // 更新收入列表
  } catch (error) {
    console.error('删除收入失败', error);
  }
};

// 页面加载时获取收入数据
onMounted(() => {
  fetchIncomes();
});

// 添加搜索表单数据
const searchForm = ref({
  source: '',
  date: '',
  description: '',
});

// 计算属性过滤收入列表
const filteredIncomes = computed(() => {
  return incomes.value.filter((income) => {
    const sourceMatch = income.source.toLowerCase().includes(searchForm.value.source.toLowerCase());
    const dateMatch = searchForm.value.date ? income.date === searchForm.value.date : true;
    const descMatch = income.description.toLowerCase().includes(searchForm.value.description.toLowerCase());
    return sourceMatch && dateMatch && descMatch;
  });
});

const formatDate = (row, column, cellValue) => {
  const date = new Date(cellValue);
  const year = date.getFullYear();
  const month = (date.getMonth() + 1).toString().padStart(2, '0');
  const day = date.getDate().toString().padStart(2, '0');
  return `${year}-${month}-${day}`;
};
</script>

<style scoped>
/* 设置页面整体的字体和背景颜色 */
body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f4f4f9;
}

/* 设置卡片的样式 */
.el-card {
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

/* 设置表单标题的样式 */
h2,
h3 {
  color: #333;
  margin-bottom: 20px;
}

/* 设置表单元素的样式 */
.el-form-item {
  margin-bottom: 15px;
}

/* 设置输入框和选择框的样式 */
.el-input,
.el-select {
  width: 100%;
}

/* 设置按钮的样式 */
.el-button {
  border-radius: 4px;
}

/* 设置表格的样式 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
}

/* 设置表格表头的样式 */
.el-table__header th {
  background-color: #e9e9f3;
  color: #555;
}

/* 设置表格行的悬停样式 */
.el-table__row:hover {
  background-color: #f0f0f7;
}
</style>