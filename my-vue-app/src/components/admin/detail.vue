<template>
    <div class="filter-container">
        <!-- 第一行：时间范围选择 -->
        <div class="time-range-row">
            <span class="label">时间范围</span>
            <el-select v-model="selectedTimeRange" placeholder="选择时间范围" @change="handleRangeChange" style="width: 1000px">
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
            />
        </div>

        <!-- 第二行：统计信息 -->
        <div class="stats-row">
            <div class="stat-card income">
                <div class="stat-title">收入</div>
                <div class="stat-value">{{ totalIncome }}</div>
            </div>
            <div class="stat-card expense">
                <div class="stat-title">支出</div>
                <div class="stat-value">{{ totalExpense }}</div>
            </div>
            <div class="stat-card balance">
                <div class="stat-title">结余</div>
                <div class="stat-value">{{ balance }}</div>
            </div>
        </div>

        <!-- 第三行：其他筛选条件 -->
        <div class="filter-row">
            <el-row :gutter="20">
                <el-col :span="6">
                    <span class="label">家庭成员</span>
                    <el-select v-model="selectedMember" placeholder="选择成员" @change="filterRecords">
                        <el-option label="全部" value="all"></el-option>
                        <el-option
                            v-for="member in members"
                            :key="member.id"
                            :label="member.name"
                            :value="member.id"
                        ></el-option>
                    </el-select>
                </el-col>
                <el-col :span="6">
                    <span class="label">收支类别</span>
                    <el-select v-model="selectedCategory" placeholder="选择收支类别" @change="filterRecords">
                        <el-option label="全部" value="all"></el-option>
                        <el-option
                            v-for="category in categories"
                            :key="category"
                            :label="category"
                            :value="category"
                        ></el-option>
                    </el-select>
                </el-col>
                <el-col :span="6">
                    <span class="label">收入/支出</span>
                    <el-select v-model="selectedIncomeExpense" placeholder="选择收入/支出" @change="filterRecords">
                        <el-option label="全部" value="all"></el-option>
                        <el-option label="收入" value="income"></el-option>
                        <el-option label="支出" value="expense"></el-option>
                    </el-select>
                </el-col>
                <el-col :span="6">
    <div class="button-group">
        <el-button @click="filterRecords" type="primary" class="filter-btn">筛选</el-button>
        <el-button @click="resetFilters" type="primary" class="filter-btn">重置</el-button>
    </div>
</el-col>
            </el-row>
            <el-row :gutter="20" class="advanced-filters">
                <el-col :span="6">
                    <span class="label">最小金额</span>
                    <el-input v-model="minAmount" placeholder="输入最小金额" @keyup.enter="filterRecords"></el-input>
                </el-col>
                <el-col :span="6">
                    <span class="label">最大金额</span>
                    <el-input v-model="maxAmount" placeholder="输入最大金额" @keyup.enter="filterRecords"></el-input>
                </el-col>
                <el-col :span="12">
                    <span class="label">备注内容</span>
                    <el-input v-model="remark" placeholder="输入备注内容" @keyup.enter="filterRecords"></el-input>
                </el-col>
            </el-row>
        </div>

        <!-- 收支明细列表 -->
        <div v-for="group in groupedRecords" :key="group.date" class="detail-group">
            <div class="detail-header">
                <span>{{ formatFullDate({ date: group.date }) }}</span>
                <span>收入: {{ group.income || 0 }}</span>
                <span>支出: {{ group.expense || 0 }}</span>
            </div>
            
            <el-table :data="group.records" stripe class="detail-table">
                <el-table-column prop="memberName" label="成员" width="120"></el-table-column>
                <el-table-column prop="time" label="收支时间" :formatter="formatDate" width="150"></el-table-column>
                <el-table-column prop="amount" label="金额" width="120">
                    <template #default="scope">
                        <span :class="{'income-text': scope.row.incomeId, 'expense-text': scope.row.expenseId}">
                            {{ scope.row.amount }}
                        </span>
                    </template>
                </el-table-column>
                <el-table-column prop="category" label="收支类别" width="150"></el-table-column>
                <el-table-column prop="remark" label="备注"></el-table-column>
                <el-table-column label="操作" width="120">
                    <template #default="scope">
                        <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import axios from 'axios';
import { ElMessage } from 'element-plus';

const userInfo = JSON.parse(localStorage.getItem('userInfo'));
const adminId = ref(userInfo.id);

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

const categories = ref(['餐饮', 'Gift', '购物', '娱乐', '住房', '医疗', '教育', '其他']);

// 筛选条件
const selectedTimeRange = ref('week');
const selectedDate = ref(new Date());
const selectedMember = ref('all');
const selectedCategory = ref('all');
const selectedIncomeExpense = ref('all');
const minAmount = ref('');
const maxAmount = ref('');
const remark = ref('');

// 收支记录
const records = ref([]);
const filteredRecords = ref([]);
const groupedRecords = ref([]);

// 计算属性
const datePickerType = computed(() => {
    return selectedTimeRange.value === 'year' ? 'year' : selectedTimeRange.value === 'month' ? 'month' : 'week';
});

const dateFormat = computed(() => {
    if (selectedTimeRange.value === 'week') {
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

// 统计信息
const totalIncome = computed(() => {
    return groupedRecords.value.reduce((sum, group) => sum + (group.income || 0), 0);
});

const totalExpense = computed(() => {
    return groupedRecords.value.reduce((sum, group) => sum + (group.expense || 0), 0);
});

const balance = computed(() => {
    return totalIncome.value - totalExpense.value;
});

// 方法
const handleRangeChange = () => {
    selectedDate.value = new Date(); // 重置为当前时间
    filterRecords();
};

const resetFilters = () => {
    selectedMember.value = 'all';
    selectedCategory.value = 'all';
    selectedIncomeExpense.value = 'all';
    minAmount.value = '';
    maxAmount.value = '';
    remark.value = '';
    filterRecords();
};

const getDateRange = () => {
    const date = selectedDate.value;
    switch (selectedTimeRange.value) {
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

    if (selectedCategory.value !== 'all') {
        filtered = filtered.filter(record => record.category === selectedCategory.value);
    }

    if (selectedIncomeExpense.value !== 'all') {
        if (selectedIncomeExpense.value === 'income') {
            filtered = filtered.filter(record => record.incomeId);
        } else if (selectedIncomeExpense.value === 'expense') {
            filtered = filtered.filter(record => record.expenseId);
        }
    }

    if (minAmount.value) {
        filtered = filtered.filter(record => record.amount >= parseFloat(minAmount.value));
    }

    if (maxAmount.value) {
        filtered = filtered.filter(record => record.amount <= parseFloat(maxAmount.value));
    }

    if (remark.value) {
        filtered = filtered.filter(record => record.remark && record.remark.includes(remark.value));
    }

    filteredRecords.value = filtered;

    // 按日期分组
    const grouped = {};
    filtered.forEach(record => {
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

    groupedRecords.value = Object.values(grouped).sort((a, b) => new Date(b.date) - new Date(a.date));
};

const handleEdit = (record) => {
    // 这里添加编辑逻辑
    console.log('编辑记录:', record);
    ElMessage.info(`编辑记录: ${record.id}`);
};

// 格式化日期函数
const formatDate = (row, column, cellValue) => {
    const date = new Date(cellValue);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// 格式化日期显示为几月几日星期几
const formatFullDate = (row, column, cellValue) => {
    const date = new Date(row.date || cellValue);
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const weekday = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'][date.getDay()];
    return `${month}月${day}日 ${weekday}`;
};

onMounted(async () => {
    await getMember();
    await getRecords();
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

.stats-row {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;
}

.stat-card {
    flex: 1;
    padding: 15px;
    border-radius: 8px;
    text-align: center;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-title {
    font-size: 20px;
    color: #666;
    margin-bottom: 5px;
}

.stat-value {
    font-size: 30px;
    font-weight: bold;
}

.income {
    background-color: #f0f9eb;
    border: 1px solid #e1f3d8;
}

.expense {
    background-color: #fef0f0;
    border: 1px solid #fde2e2;
}

.balance {
    background-color: #f4f4f5;
    border: 1px solid #e9e9eb;
}

.filter-row {
    margin-bottom: 20px;
}

.advanced-filters {
    margin-top: 15px;
}

.detail-group {
    margin-top: 20px;
    border: 1px solid #ebeef5;
    border-radius: 4px;
    overflow: hidden;
}

.detail-header {
    padding: 12px 16px;
    background-color: #f5f7fa;
    border-bottom: 1px solid #ebeef5;
    display: flex;
    justify-content: space-between;
    font-weight: bold;
}

.detail-table {
    margin-top: 0;
    border-top: none;
}

.income-text {
    color: #67c23a;
    font-weight: bold;
}

.expense-text {
    color: #f56c6c;
    font-weight: bold;
}

.el-select, .el-date-picker, .el-input {
    width: 100%;
}

.el-row {
    margin-bottom: 10px;
}

.el-col {
    display: flex;
    flex-direction: column;
}

.button-group {
    display: flex;
     margin-top: 28px; 
    gap: 10px;
}

.filter-btn {
    flex: 1;
}
</style>