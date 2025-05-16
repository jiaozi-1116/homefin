<template>
  <div class="admin-container">
    <el-card>
      <h3>购入申请审批</h3>
      <el-table :data="requests">
        <el-table-column prop="ItemName" label="商品名称" />
        <el-table-column prop="Description" label="商品描述" />
        <el-table-column prop="applicant" label="申请人" />
        <el-table-column prop="Amount" label="金额" width="120" />
        <el-table-column prop="Status" label="状态" width="120">
          <template #default="{row}">
            <el-tag :type="statusTagType(row.Status)">
              {{ row.Status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{row}">
            <el-button-group v-if="row.Status === 'Pending'">
              <el-button type="success" @click="approve(row.RequestID)">
                批准
              </el-button>
              <el-button type="danger" @click="reject(row.RequestID)">
                拒绝
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const requests = ref([])

const userInfo = JSON.parse(localStorage.getItem('userInfo'))

const familyId = userInfo.id

// 获取待审批申请
const fetchRequests = async () => {
  const { data } = await axios.get(`http://localhost:8081/api/purchase/pending/${familyId}`)
  requests.value = data
}

// 审批操作（同意）
const approve = async (requestId) => {
  await axios.put(`http://localhost:8081/api/purchase/approve/${requestId}`)
  fetchRequests()
}
//拒绝
const reject = async (requestId) => {
  await axios.put(`http://localhost:8081/api/purchase/reject/${requestId}`)
  fetchRequests()
}

onMounted(fetchRequests)

const statusTagType = (status) => {
  return {
    Pending: 'info',
    Approved: 'success',
    Rejected: 'danger',
    Completed: ''
  }[status]
}
</script>