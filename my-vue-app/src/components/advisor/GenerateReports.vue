<!-- GenerateReports.vue -->
<template>
  <div class="generate-reports">
    <el-alert
      title="当前选择家庭ID"
      type="info"
      :description="familyId.toString()"
      show-icon
      style="margin-bottom: 20px"
    />

    <el-card class="report-form">
      <template #header>
        <div class="card-header">
          <span>生成新报告</span>
        </div>
      </template>

      <el-form :model="reportForm" label-width="120px">
        <el-form-item label="报告类型">
          <el-select v-model="reportForm.reportType" placeholder="请选择">
            <el-option
              v-for="item in reportTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="年份">
          <el-input-number
            v-model="reportForm.year"
            :min="2020"
            :max="2030"
            controls-position="right"
          />
        </el-form-item>

        <el-form-item label="月份（可选）">
          <el-input-number
            v-model="reportForm.month"
            :min="1"
            :max="12"
            controls-position="right"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="generateReport">立即生成</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="report-list" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>历史报告记录</span>
          <el-button type="text" @click="refreshReports">刷新列表</el-button>
        </div>
      </template>

      <el-table :data="reports" style="width: 100%">
        <el-table-column prop="generatedDate" label="生成时间" width="180">
          <template #default="{row}">
            {{ new Date(row.generatedDate).toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="reportType" label="报告类型" width="120" />
        <el-table-column prop="year" label="年份" width="80" />
        <el-table-column prop="month" label="月份" width="80" />
        <el-table-column prop="financialHealthIndex" label="健康指数" width="120">
          <template #default="{row}">
            <el-tag :type="getHealthTagType(row.financialHealthIndex)">
              {{ row.financialHealthIndex }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{row}">
            <el-button type="text" @click="downloadReport(row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus';

const props = defineProps({
  familyId: {
    type: Number,
    required: true
  }
})

const reportForm = ref({
  reportType: 'monthly',
  year: new Date().getFullYear(),
  month: new Date().getMonth() + 1
})

const reportTypes = [
  { value: 'monthly', label: '月度报告' },
  { value: 'annual', label: '年度报告' },
  { value: 'custom', label: '自定义报告' }
]

const reports = ref([])

// 获取健康指数标签类型
const getHealthTagType = (score) => {
  if (score >= 80) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
}

// 生成报告
const generateReport = async () => {
  try {
    const payload = {
      reportType: reportForm.value.reportType,
      year: reportForm.value.year,
      month: reportForm.value.reportType === 'annual' ? null : reportForm.value.month
    }

    const response = await axios.post(
      `http://localhost:8081/api/reports/generate/${props.familyId}`,
      payload
    )

    ElMessage.success('报告生成成功')
    refreshReports()
  } catch (error) {
    ElMessage.error(error.response?.data?.error || '生成报告失败')
  }
}

// 刷新报告列表
const refreshReports = async () => {
  try {
    const response = await axios.get(
      `http://localhost:8081/api/reports/family-generated/${props.familyId}`
    )
    reports.value = response.data
  } catch (error) {
    ElMessage.error('获取报告列表失败')
  }
}

// 初始化加载报告列表
refreshReports()

// 下载报告
const downloadReport = (report) => {
  // 实现下载逻辑，可能需要后端支持
  console.log('下载报告:', report)
  ElMessage.info('下载功能待实现')
}

// 监听familyId变化
watch(() => props.familyId, () => {
  refreshReports()
})
</script>

<style scoped>
.generate-reports {
  padding: 20px;
}

.report-form {
  max-width: 600px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>