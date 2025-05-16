<template>
  <div>
    <section class="filter-section">
      <div class="filter-group">
        <el-select v-model="timeRange" placeholder="选择统计范围" @change="handleRangeChange" style="width: 620px;">
          <el-option label="周" value="week" />
          <el-option label="月" value="month" />
          <el-option label="年" value="year" />
        </el-select>
        
        <el-date-picker
          v-model="selectedDate"
          :type="datePickerType"
          :format="dateFormat"
          placeholder="选择日期"
          @change="fetchData"
          style="width: 620px;"
        />
      </div>

      <div class="button-group">
        <el-button type="primary" @click="showIncome = true">查看{{ rangeLabel }}收入</el-button>
        <el-button type="warning" @click="showIncome = false">查看{{ rangeLabel }}支出</el-button>
      </div>
    </section>

    <section class="stats-section">
      <el-card class="stats-section">
        <el-row :gutter="20">
          <el-col :span="6" v-for="(card, index) in statsCards" :key="index">
            <el-card class="stat-card" style="height: 120px;">
              <div class="card-content" style="text-align: center">
                <div class="stat-title">{{ card.title }}</div>
                <div class="stat-value" style="font-size: 24px;">{{ card.value }}</div>
                <div class="stat-detail" v-if="card.detail">{{ card.detail }}</div>
              </div>
            </el-card>
        </el-col>
        </el-row>
        <el-card class="chart-card" style="margin-top: 20px;">
            <div ref="trendChart" style="height: 400px;"></div>
        </el-card>
      </el-card>

      <el-card class="chart-card">
        <div ref="pieChart" style="height: 400px;"></div>
      </el-card>

      <el-card class="ranking-card">
          <h3>{{ rangeLabel }}{{ showIncome ? '收入' : '支出' }}类目排行</h3>
          <div class="ranking-list">
            <div v-for="(item, index) in categoryRanking" :key="index" class="ranking-item">
              <div class="category-info">
                <span class="category-name">{{ item.category }}</span>
                <span class="category-amount">￥{{ item.amount.toFixed(2) }}</span>
              </div>
              <el-progress 
                :percentage="item.percentage" 
                :color="showIncome ? '#67C23A' : '#F56C6C'"
                :show-text="false"
              />
              <div class="category-meta">
                <span>排名: {{ index + 1 }}</span>
                <span>占比: {{ item.percentage }}%</span>
                <span>类目数量: {{ item.count }}</span>
              </div>
            </div>
          </div>
        </el-card>



    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, reactive, watch  } from 'vue'
import { ElSelect, ElOption, ElDatePicker, ElButton } from 'element-plus'
import * as echarts from 'echarts'
import axios from 'axios'
import { use } from 'echarts/core'
import { TooltipComponent, GridComponent } from 'echarts/components'
import { LineChart, PieChart  } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'


use([TooltipComponent, GridComponent, LineChart, PieChart, CanvasRenderer])

const userInfo = JSON.parse(localStorage.getItem('userInfo'))
const memberId = userInfo.id

// 响应式数据
const timeRange = ref('week')
const selectedDate = ref(new Date())
const showIncome = ref(true)
const trendChart = ref(null)
let chartInstance = null

// 计算属性
const datePickerType = computed(() => {
  return timeRange.value === 'year' ? 'year' : timeRange.value === 'month' ? 'month' : 'week'
})

const dateFormat = computed(() => {
  if (timeRange.value === 'week') {
    const { start, end } = getDateRange()
    const startDate = new Date(start)
    const endDate = new Date(end)
    return `${startDate.getMonth() + 1}月${startDate.getDate()}日-${endDate.getMonth() + 1}月${endDate.getDate()}日统计`
  }
  return {
    // week: '第w周 YYYY',
    month: 'YYYY年MM月统计',
    year: 'YYYY年统计'
  }[timeRange.value]
})

const rangeLabel = computed(() => {
  return {
    week: '本周',
    month: '本月',
    year: '本年'
  }[timeRange.value]
})

// 方法
const handleRangeChange = () => {
  selectedDate.value = new Date() // 重置为当前时间
  fetchData()
}

const getDateRange = () => {
  const date = selectedDate.value
  switch(timeRange.value) {
    case 'week':
      const startOfWeek = new Date(date)
      startOfWeek.setDate(date.getDate() - date.getDay() + 1)
      return {
        start: startOfWeek.toISOString().split('T')[0],
        end: new Date(startOfWeek.setDate(startOfWeek.getDate() + 6)).toISOString().split('T')[0]
      }
    case 'month':
      return {
        start: new Date(date.getFullYear(), date.getMonth(), 1).toISOString().split('T')[0],
        end: new Date(date.getFullYear(), date.getMonth() + 1, 0).toISOString().split('T')[0]
      }
    case 'year':
      return {
        start: new Date(date.getFullYear(), 0, 1).toISOString().split('T')[0],
        end: new Date(date.getFullYear(), 11, 31).toISOString().split('T')[0]
      }
  }
}


// 计算统计卡片数据-------------------------------------------------
const statsCards = computed(() => {
  const isIncome = showIncome.value
  const typeLabel = isIncome ? '收入' : '支出'
  const date = new Date(statsData.maxDate)
  
  const mainCard = {
    title: timeRange.value === 'year' 
      ? `本年单月最高${typeLabel}`
      : `${rangeLabel.value}单日最高${typeLabel}`,
    value: `￥${statsData.maxAmount.toFixed(2)}`,
    detail: timeRange.value === 'year'
      ? `在${date.getMonth() + 1}月，你${typeLabel}了￥${statsData.maxAmount.toFixed(2)}`
      : `在${date.toLocaleDateString()}，你${typeLabel}了￥${statsData.maxAmount.toFixed(2)}`
  }

  return [
    mainCard,
    {
      title: `${rangeLabel.value}总${typeLabel}`,
      value: `￥${statsData.total.toFixed(2)}`
    },
    {
      title: `${rangeLabel.value}平均每日${typeLabel}`,
      value: `￥${(statsData.total / (timeRange.value === 'year' ? 12 : statsData.count)).toFixed(2)}`
    },
    {
      title: `${rangeLabel.value}${typeLabel}笔数`,
      value: `${statsData.count}笔`
    }
  ]
})


// 新增响应式数据----------------
const statsData = reactive({
  maxAmount: 0,
  maxDate: '',
  total: 0,
  count: 0
})

const fetchData = async () => {
  try {
    const { start, end } = getDateRange()
    const type = showIncome.value ? 'income' : 'expense'
    console.log('memberId:', memberId); // 添加调试信息
    console.log('start:', start);
    console.log('end:', end);
    console.log('groupBy:', timeRange.value === 'year' ? 'month' : 'day');
    const response = await axios.get(`http://localhost:8081/api/finance/${type}/range`, {
      params: {
        memberId,
        start,
        end,
        groupBy: timeRange.value === 'year' ? 'month' : 'day'
      }
    })

    const processedData = processChartData(response.data); // 保存处理后的数据
    renderChart(processedData); // 传递处理后的数据
    await fetchCategoryData()
    await fetchRankData() // 新增获取排行数据
  } catch (error) {
    console.error('数据获取失败:', error)
  }
}

// 修改后的数据处理
const processChartData = (data) => {
  const dates = []
  const amounts = []
  let max = 0
  let maxDate = ''
  let total = 0

  data.forEach(item => {
    dates.push(item.date)
    amounts.push(item.amount)
    total += item.amount
    if (item.amount > max) {
      max = item.amount
      maxDate = item.date
    }
  })

  statsData.maxAmount = max
  statsData.maxDate = maxDate
  statsData.total = total
  statsData.count = data.length

  return { dates, amounts }
}

const renderChart = (data) => {
  if (!chartInstance) {
    chartInstance = echarts.init(trendChart.value)
  }

  const option = {
    title: {
      text: `${rangeLabel.value}${showIncome.value ? '收入' : '支出'}趋势`
    },
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        const date = params[0].axisValue
        const value = params[0].value
        return timeRange.value === 'year' 
          ? `${date.split('-')[0]}年${date.split('-')[1]}月：￥${value}`
          : `${date}：￥${value}`
      }
    },
    xAxis: {
      type: 'category',
      data: data.dates,
      axisLabel: {
        formatter: (value) => {
          if (timeRange.value === 'year') return `${value.split('-')[1]}月`
          if (timeRange.value === 'month') return `${value.split('-')[2]}日`
          return new Date(value).toLocaleDateString()
        }
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: data.amounts,
      type: 'line',
      smooth: true,
      areaStyle: {},
      itemStyle: {
        color: showIncome.value ? '#67C23A' : '#F56C6C'
      }
    }]
  }

  chartInstance.setOption(option)
}

onMounted(() => {
  fetchData()
})


//--------------------------------------------------------------收入/支出占比------------------------------------
// 新增响应式引用
const pieChart = ref(null)
let pieChartInstance = null

// 新增分类数据响应式对象
const categoryData = reactive({
  categories: [],
  amounts: []
})

// 新增分类数据获取方法
const fetchCategoryData = async () => {
  try {
    const { start, end } = getDateRange()
    const type = showIncome.value ? 'income' : 'expense'
    const response = await axios.get(`http://localhost:8081/api/finance/${type}/categories`, {
      params: {
        memberId,
        start,
        end
      }
    })
    processPieData(response.data)
  } catch (error) {
    console.error('分类数据获取失败:', error)
  }
}

// 新增饼图数据处理
const processPieData = (data) => {
  categoryData.categories = data.map(item => item.category)
  categoryData.amounts = data.map(item => ({
    name: item.category,
    value: item.amount
  }))
  renderPieChart()
}

// 新增饼图渲染方法
const renderPieChart = () => {
  if (!pieChartInstance) {
    pieChartInstance = echarts.init(pieChart.value)
  }

  const option = {
    title: [{
      text: `${rangeLabel.value}${showIncome.value ? '收入' : '支出'}`,
      left: 'center',
      top: 'center'
    }],
    tooltip: {
      trigger: 'item',
      formatter: '{b}: ￥{c} ({d}%)'
    },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '50%'],
      data: categoryData.amounts,
      label: {
        show: true,
        formatter: '{b|{b}}\n{hr|}\n{d}%',
        rich: {
          b: {
            fontSize: 12,
            lineHeight: 20
          },
          hr: {
            borderColor: '#999',
            width: '100%',
            borderWidth: 1,
            height: 0
          }
        }
      },
      itemStyle: {
        color: (params) => {
          const colors = [
            '#5470C6', '#91CC75', '#FAC858', '#EE6666',
            '#73C0DE', '#3BA272', '#FC8452', '#9A60B4'
          ]
          return colors[params.dataIndex % colors.length]
        }
      }
    }]
  }

  pieChartInstance.setOption(option)
}

// 监听收入/支出切换
watch(showIncome, () => {
  fetchData()
})

//--------------------------------------------------------------收入/支出类目排行------------------

// 新增引用

const categoryRanking = ref([])

// 新增获取排行数据方法
const fetchRankData = async () => {
  try {
    const { start, end } = getDateRange()
    const type = showIncome.value ? 'income' : 'expense'
    const response = await axios.get(`http://localhost:8081/api/finance/${type}/ranking`, {
      params: {
        memberId,
        start,
        end
      }
    })
    processRankData(response.data)
  } catch (error) {
    console.error('排行数据获取失败:', error)
  }
}

// 新增处理排行数据方法
const processRankData = (data) => {
   // 计算所有类目的总金额
   const totalAmount = data.reduce((sum, item) => sum + item.amount, 0)
  // 计算每个类目的占比
  const newData = data.map(item => {
    const percentage = totalAmount > 0 ? (item.amount / totalAmount) * 100 : 0
    return {
      ...item,
      percentage: percentage.toFixed(2)
    }
  })
  categoryRanking.value=newData
}


// 监听showIncome变化
watch(showIncome, () => {
  fetchData()
})
</script>

<style scoped>
.filter-section {
  margin: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.filter-group {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.button-group {
  margin-top: 15px;
  text-align: center;
}

.chart-section {
  margin: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}
</style>