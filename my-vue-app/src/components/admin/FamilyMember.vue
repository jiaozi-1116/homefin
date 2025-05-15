<template>
  <div class="member-statistics">
    <!-- 筛选条件行 -->
    <div class="filter-row">
      <el-row :gutter="20">
        <!-- 家庭成员选择 -->
        <el-col :span="6">
          <span class="label">家庭成员</span>
          <el-select 
            v-model="selectedMember" 
            placeholder="选择成员" 
            @change="fetchChartData"
          >
            <el-option label="全部" value="all"></el-option>
            <el-option
              v-for="member in familyMembers"
              :key="member.id"
              :label="member.name"
              :value="member.id"
            />
          </el-select>
        </el-col>

        <!-- 时间范围选择 -->
        <el-col :span="6">
          <span class="label">时间范围</span>
          <el-select 
            v-model="selectedTimeRange" 
            placeholder="选择时间范围" 
            @change="handleTimeRangeChange"
          >
            <el-option label="天" value="day"></el-option>
            <el-option label="周" value="week"></el-option>
            <el-option label="月" value="month"></el-option>
            <el-option label="年" value="year"></el-option>
          </el-select>
        </el-col>

        <!-- 日期选择 -->
        <el-col :span="6">
          <span class="label">选择日期</span>
          <el-date-picker
            v-model="selectedDate"
            :type="datePickerType"
            :format="dateFormat"
            placeholder="选择日期"
            @change="fetchChartData"
          />
        </el-col>
      </el-row>
    </div>

    <!-- 统计图表 -->
    <div id="member-stat-chart" style="width: 100%; height: 500px; margin-top: 20px;"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';
import { ElMessage } from 'element-plus';

  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  const familyId = ref(userInfo.id)

// 家庭成员数据
const familyMembers = ref([]);
// 筛选状态
const selectedMember = ref('all');
const selectedTimeRange = ref('month');
const selectedDate = ref(new Date());
// 日期选择器配置
const datePickerType = ref('month');
const dateFormat = ref('yyyy-MM');

// 图表实例
let chartInstance = null;

// 获取家庭成员数据
const getFamilyMembers = async () => {
  try {
    const res = await axios.get(`http://localhost:8081/api/family/${familyId.value}/members`);
    familyMembers.value = res.data;
  } catch (error) {
    ElMessage.error('获取家庭成员失败');
  }
};

// 处理时间范围变化
const handleTimeRangeChange = (value) => {
  switch (value) {
    case 'day':
      datePickerType.value = 'date';
      dateFormat.value = 'yyyy-MM-dd';
      break;
    case 'week':
      datePickerType.value = 'week';
      dateFormat.value = 'yyyy 第 WW 周';
      break;
    case 'month':
      datePickerType.value = 'month';
      dateFormat.value = 'yyyy-MM';
      break;
    case 'year':
      datePickerType.value = 'year';
      dateFormat.value = 'yyyy';
      break;
  }
  // 重置为当前日期
  selectedDate.value = new Date();
  fetchChartData();
};

// 获取日期范围（用于接口参数）
const getDateRange = () => {
  const date = selectedDate.value;
  switch (selectedTimeRange.value) {
    case 'day':
      return {
        start: new Date(date).toISOString().split('T')[0],
        end: new Date(date).toISOString().split('T')[0]
      };
    case 'week':
      const weekStart = new Date(date);
      weekStart.setDate(date.getDate() - date.getDay() + 1);
      return {
        start: weekStart.toISOString().split('T')[0],
        end: new Date(weekStart.setDate(weekStart.getDate() + 6)).toISOString().split('T')[0]
      };
    case 'month':
      return {
        start: new Date(date.getFullYear(), date.getMonth(), 1).toISOString().split('T')[0],
        end: new Date(date.getFullYear(), date.getMonth() + 1, 0).toISOString().split('T')[0]
      };
    case 'year':
      return {
        start: new Date(date.getFullYear(), 0, 1).toISOString().split('T')[0],
        end: new Date(date.getFullYear(), 11, 31).toISOString().split('T')[0]
      };
  }
};

// 获取统计数据并更新图表
const fetchChartData = async () => {
  try {
    const { start, end } = getDateRange();
    const params = {
      memberId: selectedMember.value,
      startTime: start,
      endTime: end,
      timeType: selectedTimeRange.value
    };

    const res = await axios.get('http://localhost:8081/api/statistics/member', { params });
    
    // 初始化或更新图表
    if (!chartInstance) {
      chartInstance = echarts.init(document.getElementById('member-stat-chart'));
    }

    const option = {
      title: {
        text: '家庭成员收支统计',
        left: 'center'
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        }
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        data: res.data.categories,
        axisLabel: {
          rotate: 45,
          interval: 0
        }
      },
      yAxis: {
        type: 'value',
        name: '金额（元）',
        min: 0
      },
      series: [
        {
          name: '收入',
          type: 'bar',
          data: res.data.incomeData,
          itemStyle: { color: '#67c23a' }
        },
        {
          name: '支出',
          type: 'bar',
          data: res.data.expenseData,
          itemStyle: { color: '#f56c6c' }
        }
      ]
    };

    chartInstance.setOption(option);
  } catch (error) {
    ElMessage.error('获取统计数据失败');
  }
};

// 初始化
onMounted(async () => {
  await getFamilyMembers();
  fetchChartData();
  
  // 监听窗口大小变化
  window.addEventListener('resize', () => {
    chartInstance?.resize();
  });
});

// 组件卸载时销毁图表
onMounted(() => {
  if (chartInstance) {
    chartInstance.dispose();
    chartInstance = null;
  }
});
</script>

<style scoped>
.filter-row {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.label {
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.el-select, .el-date-picker {
  width: 100%;
}
</style>