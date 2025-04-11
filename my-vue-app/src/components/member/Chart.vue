<template>
<div>
            <!-- 查看财务报告和统计图表 -->
            <section>
              <h2>统计图表</h2>
              <!-- <el-card> -->
                <!-- <el-table :data="financialReports" style="width: 100%">
                  <el-table-column prop="date" label="日期" width="180"></el-table-column>
                  <el-table-column prop="income" label="收入" width="180"></el-table-column>
                  <el-table-column prop="expense" label="支出" width="180"></el-table-column>
                </el-table>
                <el-button @click="downloadReport">下载报告</el-button> -->
              <!-- </el-card> -->
            </section>
            <div ref="incomeChart" style="height: 400px; width: 50%;"></div>
            <div ref="expenseChart" style="height: 400px; width: 50%;"></div>
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

const userInfo = JSON.parse(localStorage.getItem('userInfo'))

const memberId = userInfo.id

const incomeChart = ref(null);
const expenseChart = ref(null);

onMounted(async () => {
  try {
    // 获取收入和支出数据
    const incomeResponse = await axios.get(`http://localhost:8081/api/income/all/${memberId}`);
    const expenseResponse = await axios.get(`http://localhost:8081/api/expense/all/${memberId}`);
    
    // 打印收入和支出数据
    console.log("Income Data:", incomeResponse.data);
    console.log("Expense Data:", expenseResponse.data);
    
    // 数据处理
    const incomeData = processIncomeData(incomeResponse.data);
    const expenseData = processExpenseData(expenseResponse.data);

    // 等待 DOM 渲染完成后再初始化 ECharts
    await nextTick(() => {
      // 渲染收入图表
      const incomeChartInstance = echarts.init(incomeChart.value);
      incomeChartInstance.setOption({
        title: {
          text: 'Income Sources'
        },
        tooltip: {},
        xAxis: {
          type: 'category',
          data: incomeData.sources // x轴数据来源
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: incomeData.amounts, // y轴数据金额
            type: 'bar'
          }
        ]
      });

      // 渲染支出图表
      const expenseChartInstance = echarts.init(expenseChart.value);
      expenseChartInstance.setOption({
        title: {
          text: 'Expense Categories'
        },
        tooltip: {},
        xAxis: {
          type: 'category',
          data: expenseData.categories // x轴数据类别
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: expenseData.amounts, // y轴数据金额
            type: 'bar'
          }
        ]
      });
    });
  } catch (error) {
    console.error("Error fetching data:", error);
  }
});
// 处理收入数据
const processIncomeData = (data) => {
  const sources = [];
  const amounts = [];
  
  data.forEach(item => {
    sources.push(item.source);  // 收入来源作为 x 轴数据
    amounts.push(item.amount);  // 收入金额作为 y 轴数据
  });

  console.log("Processed Income Data:", { sources, amounts });
  return { sources, amounts };
};

// 处理支出数据
const processExpenseData = (data) => {
  const categories = [];
  const amounts = [];

  data.forEach(item => {
    categories.push(item.category);  // 支出类别作为 x 轴数据
    amounts.push(item.amount);  // 支出金额作为 y 轴数据
  });

  console.log("Processed Expense Data:", { categories, amounts });
  return { categories, amounts };
};

</script>