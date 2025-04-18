<template>
 <div>
            <section id="financial-report">
      <h2>查看和分析家庭财务报告</h2>
      
      <div style="width: 200px;">
        <label for="reportType">选择报告类型:</label>
        <select v-model="reportType" id="reportType">
          <option value="yearly">年度报告</option>
          <option value="monthly">月度报告</option>
        </select>
      </div>
      
      <div v-if="reportType === 'yearly'">
        <label for="year">选择年份:</label>
        <select v-model="selectedYear" id="year">
          <option v-for="year in years" :key="year" :value="year">{{ year }}</option>
        </select>
      </div>
      
      <div v-if="reportType === 'monthly'">
        <label for="year">选择年份:</label>
        <select v-model="selectedYear" id="year">
          <option v-for="year in years" :key="year" :value="year">{{ year }}</option>
        </select>
        
        <label for="month">选择月份:</label>
        <select v-model="selectedMonth" id="month">
          <option v-for="month in months" :key="month" :value="month">{{ month }}</option>
        </select>
      </div>
      
      <div>
        <button @click="confirmSelection">确认选择</button>
      </div>
      
      <div v-show="showCharts">
        <h3>收入图表</h3>
        <div ref="incomeChart" id="incomeChart"></div>
        
        <h3>支出图表</h3>
        <div ref="expenseChart" id="expenseChart"></div>
      </div>
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

  const userInfo = JSON.parse(localStorage.getItem('userInfo'))

  const adminName = ref(userInfo.name)
const adminId = ref(userInfo.id)

let familyID = ref(null);


const reportType = ref('yearly');
  const selectedYear = ref(new Date().getFullYear());
  const selectedMonth = ref(new Date().getMonth() + 1);
  const years = reactive([2023, 2024, 2025]);
  const months = reactive([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]);
  const showCharts = ref(false);
  
  const incomeChart = ref(null);
  const expenseChart = ref(null);
  let incomeChartInstance = null;
  let expenseChartInstance = null;
  
  // 获取收入数据
  const getIncomeData = async (year, month) => {
    const url = month 
      ? `http://localhost:8081/api/reports/incomes?year=${year}&month=${month}` 
      : `http://localhost:8081/api/reports/incomes?year=${year}`;
    const response = await fetch(url);
    const data = await response.json();
    console.log(`Income Data for ${year}-${month || ''}:`, data); // 调试输出
    return data;
  };
  
  // 获取支出数据
  const getExpenseData = async (year, month) => {
    const url = month 
      ? `http://localhost:8081/api/reports/expenses?year=${year}&month=${month}` 
      : `http://localhost:8081/api/reports/expenses?year=${year}`;
    const response = await fetch(url);
    const data = await response.json();
    console.log(`Expense Data for ${year}-${month || ''}:`, data); // 调试输出
    return data;
  };
  
  // 初始化图表
  const initCharts = () => {
    if (!incomeChart.value || !expenseChart.value) return;
  
    if (!incomeChartInstance) {
      incomeChartInstance = echarts.init(incomeChart.value);
      incomeChartInstance.setOption({
        title: {
          text: '收入图表'
        },
        tooltip: {},
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '收入',
          type: 'bar',
          data: [] // 数据将在获取后设置
        }]
      });
    }
  
    if (!expenseChartInstance) {
      expenseChartInstance = echarts.init(expenseChart.value);
      expenseChartInstance.setOption({
        title: {
          text: '支出图表'
        },
        tooltip: {},
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '支出',
          type: 'bar',
          data: [] // 数据将在获取后设置
        }]
      });
    }
  };
  
  const confirmSelection = async () => {
    const year = selectedYear.value;
    const month = reportType.value === 'monthly' ? selectedMonth.value : null;
  
    const incomeData = await getIncomeData(year, month);
    const expenseData = await getExpenseData(year, month);
  
    showCharts.value = true;
  
    // 更新收入图表数据
    const incomeValues = Object.values(incomeData);
    const incomeLabels = Object.keys(incomeData);
    console.log("Setting income chart data:", incomeValues);
  
    if (incomeChartInstance) {
      incomeChartInstance.setOption({
        xAxis: {
          data: incomeLabels
        },
        series: [{
          data: incomeValues // 确保这里的数据格式与API返回的收入数据匹配
        }]
      });
    }
  
    const expenseCategories = Object.keys(expenseData);
    const expenseValues = Object.values(expenseData);
  
    // 更新支出图表数据
    console.log("Setting expense chart data:", expenseCategories, expenseValues);
  
    if (expenseChartInstance) {
      expenseChartInstance.setOption({
        xAxis: {
          data: expenseCategories
        },
        series: [{
          data: expenseValues
        }]
      });
    }
  };
  
  onMounted(() => {
    initCharts();
  
    // 监听窗口调整大小事件并调整图表大小
    window.addEventListener('resize', () => {
      if (incomeChartInstance) incomeChartInstance.resize();
      if (expenseChartInstance) expenseChartInstance.resize();
    });
  });

</script>