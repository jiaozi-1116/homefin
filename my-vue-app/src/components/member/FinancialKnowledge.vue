<template>
    <div class="financial-knowledge">
      <el-card>
        <h2>财务知识库</h2>
        <!-- 添加分类选择框 -->
        <div class="filter-container">
          <el-select
            v-model="selectedCategory"
            placeholder="请选择分类"
            @change="fetchMaterials"
          >
            <el-option
              v-for="category in categories"
              :key="category.value"
              :label="category.label"
              :value="category.value"
            />
          </el-select>
        </div>
  
        <div v-loading="loading">
          <el-row :gutter="20">
            <el-col :span="8" v-for="material in materials" :key="material.materialID">
              <el-card class="material-card">
                <template #header>
                  <div class="card-header">
                    <h3>{{ material.title }}</h3>
                    <el-tag type="info">{{ formatDate(material.publishDate) }}</el-tag>
                  </div>
                </template>
                <div class="content-preview">
                  {{ truncateContent(material.content, 100) }}
                </div>
                <div class="category-tag">
                  <el-tag :type="getCategoryType(material.category)">
                    {{ material.category }}
                  </el-tag>
                </div>
                <div v-if="material.attachment" class="attachment">
                  <img 
                      v-lazy="getImageUrl(material.attachment)"
                      :preview-src-list="[getImageUrl(material.attachment)]"
                      style="width: 300px; margin-bottom: 10px;"
                      class="lazy-image"
                    />
                  <el-button 
                    type="primary" 
                    size="small"
                    @click="downloadAttachment(material.attachment)">
                    下载附件
                  </el-button>
                </div>
              </el-card>
            </el-col>
          </el-row>
          <div v-if="materials.length === 0" class="empty-tip">
            <el-empty description="暂无相关材料" />
          </div>
        </div>
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue'
  import axios from 'axios'
  import { ElMessage } from 'element-plus'
  
  const materials = ref([])
  const loading = ref(true)
  const familyId = ref(null)
  const memberId = JSON.parse(localStorage.getItem('userInfo'))?.id
  
  // 分类选项
  const categories = ref([
    { label: '财务知识', value: '财务知识' },
    { label: '宣传材料', value: '宣传材料' },
    { label: '投资指南', value: '投资指南' }
  ])
  
  // 默认选中的分类
  const selectedCategory = ref('财务知识')
  
  // 获取家庭ID
  const getFamilyId = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8081/api/member/findFamilyId/${memberId}`
      )
      familyId.value = response.data
    } catch (error) {
      ElMessage.error('获取家庭信息失败')
      console.error(error)
    }
  }
  
  // 获取财务知识材料
  const fetchMaterials = async () => {
    try {
      const params = {
        familyId: familyId.value,
        category: selectedCategory.value // 使用选中的分类
      }
      const response = await axios.get(
        'http://localhost:8081/api/materials',
        { params }
      )
      materials.value = response.data
    } catch (error) {
      ElMessage.error('获取材料失败')
      console.error(error)
    } finally {
      loading.value = false
    }
  }
  
  // 内容截断
  const truncateContent = (text, length) => {
    if (!text) return ''
    return text.length > length ? text.substring(0, length) + '...' : text
  }
  
  // 格式化日期
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString()
  }
  
  // 分类标签样式
  const getCategoryType = (category) => {
    const types = {
      '财务知识': 'success',
      '宣传材料': 'warning',
      '投资指南': 'danger'
    }
    return types[category] || 'info'
  }
  
  // 获取完整图片URL
  const getImageUrl = (filename) => {
    return `http://localhost:8081/uploads/${filename}`
  }
  
  // 下载附件
  const downloadAttachment = (filename) => {
    window.open(getImageUrl(filename), '_blank')
  }
  
  onMounted(async () => {
    await getFamilyId()
    fetchMaterials()
  })
  </script>
  
  <style scoped>
  .financial-knowledge {
    padding: 20px;
  }
  
  .filter-container {
    margin-bottom: 20px;
  }
  
  .material-card {
    margin-bottom: 20px;
    min-height: 380px;
    display: flex;
    flex-direction: column;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px;
  }
  
  .content-preview {
    flex: 1;
    overflow: hidden;
    margin: 10px 0;
    color: #666;
    line-height: 1.6;
  }
  
  .category-tag {
    margin-top: 10px;
  }
  
  .attachment {
    margin-top: 15px;
    text-align: center;
  }
  
  .empty-tip {
    margin: 50px 0;
  }
  </style>