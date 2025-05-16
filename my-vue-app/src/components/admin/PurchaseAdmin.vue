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
            <el-button-group v-if="row.Status === 'Approved'">
                <el-button type="primary" @click="showDetail(row.RequestID)" style="width: 120px;">
                详情
                </el-button>
              </el-button-group>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="申请详情" width="50%">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="商品名称">{{ currentDetail.itemName }}</el-descriptions-item>
        <el-descriptions-item label="申请金额">¥{{ currentDetail.amount?.toFixed(2) }}</el-descriptions-item>
        <!-- <el-descriptions-item label="申请人">{{ currentDetail.applicant }}</el-descriptions-item> -->
        <el-descriptions-item label="申请时间">{{ formatTime(currentDetail.applyTime) }}</el-descriptions-item>
        <el-descriptions-item label="商品描述">{{ currentDetail.description }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="statusTagType(currentDetail.status)">
            {{ currentDetail.status }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item v-if="currentDetail.Status === 'Approved'" label="批准时间">
          {{ formatTime(currentDetail.ApproveTime) }}
        </el-descriptions-item>
        <el-descriptions-item label="凭证图片" v-if="images.length > 0">
          <el-image 
            v-for="(img, index) in images" 
            :key="index"
            :src="'http://localhost:8081' + img.ImagePath" 
            :preview-src-list="images.map(i => 'http://localhost:8081' + i.ImagePath)"
            fit="cover"
            style="width: 100px; height: 100px; margin-right: 10px;"
          />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const requests = ref([])
const detailVisible = ref(false)
const currentDetail = ref({})
const images = ref([])

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

// 显示详情
const showDetail = async (requestId) => {
  try {
    // 获取申请详情
    const { data: detailData } = await axios.get(`http://localhost:8081/api/purchase/detail/${requestId}`)
    currentDetail.value = detailData
    
    // 获取关联图片
    const { data: imagesData } = await axios.get(`http://localhost:8081/api/purchase/images/${requestId}`)
    images.value = imagesData
    
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

const formatTime = (time) => {
  if (!time) return ''
  try {
    return new Date(time).toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false
    })
  } catch {
    return time
  }
}
</script>