<template>
  <div class="trend-analysis">
    <el-card>
      <h2>家庭财务趋势分析</h2>
      
      <!-- 时间范围选择 -->
      <div class="time-range-selector">
        <el-date-picker
          v-model="year"
          type="year"
          placeholder="选择年份"
          format="YYYY"
          value-format="YYYY"
          @change="fetchTrendData"
        />
        <el-button type="primary" @click="fetchTrendData">查询</el-button>
      </div>

      <!-- 收入支出对比折线图 -->
      <el-card class="chart-card">
        <h3>收入/支出趋势对比</h3>
        <div ref="trendChartRef" style="height: 450px;"></div>
      </el-card>

      <!-- 同比增长率卡片 -->
      <el-row :gutter="20" class="growth-rate-row">
        <el-col :span="12">
          <el-card>
            <h3>收入同比增长率</h3>
            <div class="growth-rate-content">
              <span :class="incomeGrowthRate >= 0 ? 'positive' : 'negative'">
                {{ incomeGrowthRate.toFixed(2) }}%
              </span>
              <el-icon :color="incomeGrowthRate >= 0 ? '#67C23A' : '#F56C6C'" :size="24">
                <ArrowUp v-if="incomeGrowthRate >= 0" />
                <ArrowDown v-else />
              </el-icon>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <h3>支出同比增长率</h3>
            <div class="growth-rate-content">
              <span :class="expenseGrowthRate >= 0 ? 'positive' : 'negative'">
                {{ expenseGrowthRate.toFixed(2) }}%
              </span>
              <el-icon :color="expenseGrowthRate >= 0 ? '#67C23A' : '#F56C6C'" :size="24">
                <ArrowUp v-if="expenseGrowthRate >= 0" />
                <ArrowDown v-else />
              </el-icon>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 季节性波动分析 -->
      <el-card class="seasonal-card">
        <h3>季节性波动分析</h3>
        <div ref="seasonalChartRef" style="height: 400px;"></div>
        <div class="seasonal-analysis">
          <p v-if="peakMonths.length > 0">
            <strong>消费高峰月：</strong>{{ peakMonths.join('、') }}月
            <el-tag type="danger" size="small">峰值</el-tag>
          </p>
          <p v-if="lowMonths.length > 0">
            <strong>消费低谷月：</strong>{{ lowMonths.join('、') }}月
            <el-tag type="success" size="small">谷值</el-tag>
          </p>
          <p v-if="seasonalComment">
            <strong>季节性特征：</strong>{{ seasonalComment }}
          </p>
        </div>
      </el-card>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'
import { ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  familyId: {
    type: Number,
    required: true
  }
})

// 图表实例
let trendChart = null
let seasonalChart = null
const trendChartRef = ref(null)
const seasonalChartRef = ref(null)

// 数据状态
const year = ref(new Date().getFullYear().toString())
const currentYearData = ref([])
const lastYearData = ref([])
const incomeGrowthRate = ref(0)
const expenseGrowthRate = ref(0)
const peakMonths = ref([])
const lowMonths = ref([])
const seasonalComment = ref('')

// 初始化图表
const initCharts = () => {
  trendChart = echarts.init(trendChartRef.value)
  seasonalChart = echarts.init(seasonalChartRef.value)
  
  // 设置默认图表选项
  trendChart.setOption(getTrendChartOption([], []))
  seasonalChart.setOption(getSeasonalChartOption([]))
}

// 获取趋势数据
const fetchTrendData = async () => {
  try {
    // 获取当前年数据
    const currentRes = await axios.get(`http://localhost:8081/api/financial/trend`, {
      params: {
        familyId: props.familyId,
        year: year.value
      }
    })
    currentYearData.value = currentRes.data
    
    // 获取去年数据
    const lastYear = parseInt(year.value) - 1
    const lastRes = await axios.get(`http://localhost:8081/api/financial/trend`, {
      params: {
        familyId: props.familyId,
        year: lastYear
      }
    })
    lastYearData.value = lastRes.data
    
    // 计算同比增长率
    calculateGrowthRate()
    
    // 分析季节性波动
    analyzeSeasonal()
    
    // 更新图表
    updateCharts()
  } catch (error) {
    console.error('获取趋势数据失败:', error)
    ElMessage.error('获取趋势数据失败')
  }
}

// 计算同比增长率
const calculateGrowthRate = () => {
  const currentYearTotalIncome = currentYearData.value.reduce((sum, item) => sum + item.income, 0)
  const lastYearTotalIncome = lastYearData.value.reduce((sum, item) => sum + item.income, 0)
  const currentYearTotalExpense = currentYearData.value.reduce((sum, item) => sum + item.expense, 0)
  const lastYearTotalExpense = lastYearData.value.reduce((sum, item) => sum + item.expense, 0)
  
  incomeGrowthRate.value = lastYearTotalIncome > 0 
    ? ((currentYearTotalIncome - lastYearTotalIncome) / lastYearTotalIncome) * 100 
    : 0
  
  expenseGrowthRate.value = lastYearTotalExpense > 0 
    ? ((currentYearTotalExpense - lastYearTotalExpense) / lastYearTotalExpense) * 100 
    : 0
}

// 分析季节性波动
const analyzeSeasonal = () => {
  if (currentYearData.value.length === 0) return
  
  // 计算月平均支出
  const monthlyExpenses = currentYearData.value.map(item => item.expense)
  const avgExpense = monthlyExpenses.reduce((sum, val) => sum + val, 0) / monthlyExpenses.length
  
  // 找出高于平均20%的月份(高峰月)
  peakMonths.value = currentYearData.value
    .map((item, index) => item.expense > avgExpense * 1.2 ? index + 1 : null)
    .filter(month => month !== null)
  
  // 找出低于平均20%的月份(低谷月)
  lowMonths.value = currentYearData.value
    .map((item, index) => item.expense < avgExpense * 0.8 ? index + 1 : null)
    .filter(month => month !== null)
  
  // 生成季节性特征描述
  generateSeasonalComment()
}

// 生成季节性特征描述
const generateSeasonalComment = () => {
  if (peakMonths.value.length === 0 && lowMonths.value.length === 0) {
    seasonalComment.value = '支出分布较为均匀，无明显季节性波动特征'
    return
  }
  
  let comment = ''
  
  if (peakMonths.value.includes(1) || peakMonths.value.includes(2)) {
    comment += '春节前后消费明显增加；'
  }
  
  if (peakMonths.value.includes(6) || peakMonths.value.includes(7)) {
    comment += '年中购物季消费较高；'
  }
  
  if (peakMonths.value.includes(11) || peakMonths.value.includes(12)) {
    comment += '年底消费显著提升；'
  }
  
  if (lowMonths.value.includes(3) || lowMonths.value.includes(4)) {
    comment += '春季消费相对较低；'
  }
  
  if (lowMonths.value.includes(8) || lowMonths.value.includes(9)) {
    comment += '夏末秋初消费较为平稳；'
  }
  
  seasonalComment.value = comment || '支出呈现一定的季节性波动特征'
}

// 更新图表
const updateCharts = () => {
  trendChart.setOption(getTrendChartOption(currentYearData.value, lastYearData.value))
  seasonalChart.setOption(getSeasonalChartOption(currentYearData.value))
}

// 获取趋势图表配置
const getTrendChartOption = (currentData, lastData) => {
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  
  return {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        let result = params[0].axisValue + '<br/>'
        params.forEach(param => {
          result += `${param.seriesName}: ¥${param.value.toLocaleString()}<br/>`
        })
        return result
      }
    },
    legend: {
      data: ['本年收入', '本年支出', '上年收入', '上年支出']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: months
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '本年收入',
        type: 'line',
        data: currentData.map(item => item.income),
        lineStyle: {
          color: '#67C23A'
        },
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '本年支出',
        type: 'line',
        data: currentData.map(item => item.expense),
        lineStyle: {
          color: '#F56C6C'
        },
        itemStyle: {
          color: '#F56C6C'
        }
      },
      {
        name: '上年收入',
        type: 'line',
        data: lastData.map(item => item.income),
        lineStyle: {
          color: '#67C23A',
          type: 'dashed'
        },
        itemStyle: {
          color: '#67C23A'
        }
      },
      {
        name: '上年支出',
        type: 'line',
        data: lastData.map(item => item.expense),
        lineStyle: {
          color: '#F56C6C',
          type: 'dashed'
        },
        itemStyle: {
          color: '#F56C6C'
        }
      }
    ]
  }
}

// 获取季节性图表配置
const getSeasonalChartOption = (data) => {
  const months = ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
  
  // 计算月平均支出
  const avgExpense = data.length > 0 
    ? data.reduce((sum, item) => sum + item.expense, 0) / data.length 
    : 0
  
  return {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const value = params[0].value
        const diff = value - avgExpense
        const diffPercent = (diff / avgExpense * 100).toFixed(1)
        return `${params[0].axisValue}<br/>支出: ¥${value.toLocaleString()}<br/>` +
               `与平均值差异: ${diff >= 0 ? '+' : ''}${diffPercent}%`
      }
    },
    xAxis: {
      type: 'category',
      data: months
    },
    yAxis: {
      type: 'value',
      axisLabel: {
        formatter: '¥{value}'
      }
    },
    series: [
      {
        name: '月支出',
        type: 'bar',
        data: data.map(item => item.expense),
        itemStyle: {
          color: function(params) {
            // 高于平均20%显示红色，低于平均20%显示绿色，其他显示蓝色
            const value = params.data
            if (value > avgExpense * 1.2) {
              return '#F56C6C'
            } else if (value < avgExpense * 0.8) {
              return '#67C23A'
            } else {
              return '#409EFF'
            }
          }
        },
        markLine: {
          silent: true,
          data: [
            {
              type: 'average',
              name: '平均值',
              lineStyle: {
                color: '#E6A23C'
              },
              label: {
                position: 'end',
                formatter: '平均值: {c}'
              }
            }
          ]
        }
      }
    ]
  }
}

// 监听familyId变化
watch(() => props.familyId, (newVal) => {
  if (newVal) {
    fetchTrendData()
  }
})

// 初始化
onMounted(() => {
  nextTick(() => {
    initCharts()
    if (props.familyId) {
      fetchTrendData()
    }
  })
})
</script>

<style scoped>
.trend-analysis {
  padding: 20px;
}

.time-range-selector {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.chart-card {
  margin-bottom: 20px;
}

.growth-rate-row {
  margin-bottom: 20px;
}

.growth-rate-content {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  padding: 20px 0;
}

.growth-rate-content span {
  margin-right: 10px;
}

.positive {
  color: #67C23A;
}

.negative {
  color: #F56C6C;
}

.seasonal-card {
  margin-top: 20px;
}

.seasonal-analysis {
  margin-top: 20px;
  padding: 0 20px;
}

.seasonal-analysis p {
  margin: 10px 0;
  line-height: 1.6;
}
</style>