<template>
    <div class="filter-container">
        <!-- 第一行：时间范围选择 -->
        <div class="time-range-row">
            <span class="label">时间范围</span>
            <el-select v-model="selectedTimeRange" placeholder="选择时间范围" @change="handleRangeChange" style="width: 200px">
                <el-option label="天" value="day"></el-option>
                <el-option label="周" value="week"></el-option>
                <el-option label="月" value="month"></el-option>
                <el-option label="年" value="year"></el-option>
            </el-select>
            <el-date-picker
                v-model="selectedDate"
                :type="datePickerType"
                :format="dateFormat"
                placeholder="选择日期"
                @change="filterRecords"
                style="width: 300px;"
            />
            <span class="label" style="margin-left: 50px;">家庭成员</span>
            <el-select 
                v-model="selectedMember" 
                placeholder="选择成员" 
                @change="fetchChartData"
                style="width: 200px;"
            >
                <el-option label="全部" value="all"></el-option>
                <el-option
                    v-for="member in members"
                    :key="member.id"
                    :label="member.name"
                    :value="member.id"
                />
            </el-select>
        </div>
        
        <!-- 添加柱状图容器 -->
        <div ref="chartRef" style="width: 100%; height: 400px; margin-top: 20px;"></div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watchEffect } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';
import * as echarts from 'echarts';

const userInfo = JSON.parse(localStorage.getItem('userInfo'));
const adminId = ref(userInfo.id);
const chartRef = ref(null);
let chartInstance = null;

// 定义 filteredRecords
const records = ref([]);
const filteredRecords = ref([]);
const groupedRecords = ref([]);

// 模拟数据
const members = ref([]);

const getMember = async () => {
    try {
        const response = await axios.get(`http://localhost:8081/api/family/${adminId.value}/members`);
        members.value = response.data;
    } catch (error) {
        console.error('获取家庭成员信息失败:', error);
        ElMessage.error('获取家庭成员信息失败');
    }
};

// 筛选条件
const selectedTimeRange = ref('day'); // 默认改为天
const selectedDate = ref(new Date());
const selectedMember = ref('all');

// 计算属性
const datePickerType = computed(() => {
    return {
        day: 'date',
        week: 'week',
        month: 'month',
        year: 'year'
    }[selectedTimeRange.value];
});

const dateFormat = computed(() => {
    if (selectedTimeRange.value === 'day') {
        return 'YYYY年MM月DD日统计';
    } else if (selectedTimeRange.value === 'week') {
        const { start, end } = getDateRange();
        const startDate = new Date(start);
        const endDate = new Date(end);
        return `${startDate.getMonth() + 1}月${startDate.getDate()}日-${endDate.getMonth() + 1}月${endDate.getDate()}日统计`;
    }
    return {
        month: 'YYYY年MM月统计',
        year: 'YYYY年统计'
    }[selectedTimeRange.value];
});

// 获取日期范围（新增天的处理）
const getDateRange = () => {
    const date = new Date(selectedDate.value);
    switch (selectedTimeRange.value) {
        case 'day':
            return {
                start: new Date(date.setHours(0,0,0,0)).toISOString(),
                end: new Date(date.setHours(23,59,59,999)).toISOString()
            };
        case 'week':
            const startOfWeek = new Date(date);
            startOfWeek.setDate(date.getDate() - date.getDay() + 1);
            return {
                start: startOfWeek.toISOString().split('T')[0],
                end: new Date(startOfWeek.setDate(startOfWeek.getDate() + 6)).toISOString().split('T')[0]
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

// 获取收支记录
const getRecords = async () => {
    try {
        const response = await axios.get('http://localhost:8081/api/records');
        const memberIds = members.value.map(member => member.id);
        const filteredResponseData = response.data.filter(item => memberIds.includes(item.userId));
        
        records.value = filteredResponseData.map(item => {
            const member = members.value.find(m => m.id === item.userId);
            const memberName = member ? member.name : '未知成员';
            return {
                memberId: item.userId,
                memberName,
                time: item.date,
                amount: item.amount,
                category: item.category || item.source,
                remark: item.description,
                expenseId: item.expenseId,
                incomeId: item.incomeId
            };
        });
        filterRecords();
    } catch (error) {
        console.error('获取收支记录失败:', error);
        ElMessage.error('获取收支记录失败');
    }
};

// 筛选记录
const filterRecords = () => {
    let filtered = records.value;

    const { start, end } = getDateRange();
    filtered = filtered.filter(record => {
        const recordDate = new Date(record.time);
        return recordDate >= new Date(start) && recordDate <= new Date(end);
    });

    if (selectedMember.value !== 'all') {
        filtered = filtered.filter(record => record.memberId === selectedMember.value);
    }

    filteredRecords.value = filtered;
    groupedRecords.value = groupRecords(filtered);
    renderChart();
};

// 按日期分组
const groupRecords = (records) => {
    const grouped = {};
    records.forEach(record => {
        const date = new Date(record.time).toISOString().split('T')[0];
        if (!grouped[date]) {
            grouped[date] = {
                date: date,
                income: 0,
                expense: 0,
                records: []
            };
        }
        if (record.incomeId) {
            grouped[date].income += record.amount;
        } else if (record.expenseId) {
            grouped[date].expense += record.amount;
        }
        grouped[date].records.push(record);
    });
    return Object.values(grouped).sort((a, b) => new Date(b.date) - new Date(a.date));
};

// 渲染图表
const renderChart = () => {
    if (!chartRef.value) return;
    
    if (!chartInstance) {
        chartInstance = echarts.init(chartRef.value);
    }
    
    const isAll = selectedMember.value === 'all';
    const membersData = isAll ? members.value : members.value.filter(m => m.id === selectedMember.value);
    
    const incomeData = [];
    const expenseData = [];
    
    membersData.forEach(member => {
        const memberRecords = filteredRecords.value.filter(r => r.memberId === member.id);
        incomeData.push(memberRecords.filter(r => r.incomeId).reduce((sum, r) => sum + r.amount, 0));
        expenseData.push(memberRecords.filter(r => r.expenseId).reduce((sum, r) => sum + r.amount, 0));
    });
    
    const option = {
        tooltip: { trigger: 'axis' },
        legend: { data: ['收入', '支出'] },
        xAxis: {
            type: 'category',
            data: membersData.map(m => m.name)
        },
        yAxis: { type: 'value' },
        series: [
            {
                name: '收入',
                type: 'bar',
                data: incomeData,
                itemStyle: { color: '#67C23A' }
            },
            {
                name: '支出',
                type: 'bar',
                data: expenseData,
                itemStyle: { color: '#F56C6C' }
            }
        ]
    };
    
    if (!isAll) option.series.pop(); // 如果选择单个成员只显示一个系列
    chartInstance.setOption(option);
};

const fetchChartData = () => {
    filterRecords();
};

const handleRangeChange = () => {
    selectedDate.value = new Date(); // 重置为当前时间
    filterRecords();
};

onMounted(async () => {
    await getMember();
    await getRecords();
    window.addEventListener('resize', () => {
        if (chartInstance) chartInstance.resize();
    });
});
</script>

<style scoped>
.filter-container {
    padding: 20px;
    background-color: #f9f9f9;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.label {
    display: block;
    margin-bottom: 5px;
    font-weight: bold;
    color: #333;
}

.time-range-row {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 15px;
}

.time-range-row .label {
    margin-bottom: 0;
    min-width: 100px;
}
</style>