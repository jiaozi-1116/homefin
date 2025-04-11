<template>
<div >
    <!-- 记录收入 -->
    <section id="record-income">
      <el-card>
        <h2>记录收入</h2>
        <el-form :model="incomeForm" @submit.prevent="saveIncome">
          <el-form-item label="收入金额">
            <el-input v-model="incomeForm.amount" type="number" placeholder="请输入收入金额"></el-input>
          </el-form-item>
          <el-form-item label="收入来源">
            <el-input v-model="incomeForm.source" placeholder="请输入收入来源"></el-input>
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
        <el-input v-model="searchForm.source" placeholder="输入来源"></el-input>
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
        <el-table-column label="日期" prop="date"></el-table-column>
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

//   const props = defineProps({
//   memberId: {
//     type: Number,
//     required: true
//   }
// })

const userInfo = JSON.parse(localStorage.getItem('userInfo'))

const memberId = userInfo.id


const incomeForm = ref({
  userId: memberId,
  amount: '',
  source: '',
  date: '',
  description: ''
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
    console.log('添加的收入：', incomeForm.value)
     fetchIncomes(); // 更新收入列表
    incomeForm.value = {
      userId: memberId,
      amount: '',
      source: '',
      date: '',
      description: ''
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
  description: ''
});

// 计算属性过滤收入列表
const filteredIncomes = computed(() => {
  return incomes.value.filter(income => {
    const sourceMatch = income.source.toLowerCase().includes(searchForm.value.source.toLowerCase());
    const dateMatch = searchForm.value.date ? income.date === searchForm.value.date : true;
    const descMatch = income.description.toLowerCase().includes(searchForm.value.description.toLowerCase());
    return sourceMatch && dateMatch && descMatch;
  });
});

</script>