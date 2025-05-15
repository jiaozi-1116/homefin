<template>
    <div class="publish-container">
      <h3>发布财务材料</h3>
      <el-form :model="form" label-width="100px" ref="formRef">
        <!-- 标题 -->
        <el-form-item label="标题" prop="title" :rules="[{ required: true, message: '标题不能为空' }]">
          <el-input v-model="form.title" placeholder="请输入材料标题"></el-input>
        </el-form-item>
  
        <!-- 分类 -->
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" placeholder="请选择分类">
            <el-option
              v-for="item in categories"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
  
        <!-- 内容 -->
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            placeholder="请输入详细内容"
          ></el-input>
        </el-form-item>
  
        <!-- 附件上传 -->
        <el-form-item label="附件">
            <el-upload
                :auto-upload="false"
                :on-change="handleFileChange"
                :limit="1"
                :file-list="fileList"
                >
                <el-button type="primary">点击上传</el-button>
                <template #tip>
                    <div class="el-upload__tip">支持PDF/Word/图片格式，不超过10MB</div>
                </template>
                </el-upload>
        </el-form-item>
  
        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="submitForm">立即发布</el-button>
        </el-form-item>
      </el-form>
      
    </div>

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
              v-for="category in Categories"
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
                  <el-image 
                    :src="getImageUrl(material.attachment)" 
                    :preview-src-list="[getImageUrl(material.attachment)]"
                    style="width: 300px; margin-bottom: 10px;"
                  ></el-image>
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
  import { ref, onMounted } from 'vue';
  import axios from 'axios';
  import { ElMessage } from 'element-plus';

  const userInfo = JSON.parse(localStorage.getItem('userInfo'))
  const familyId = ref(userInfo.id)
  
// 文件列表引用
const fileList = ref([]);

  // const props = defineProps({
  //   familyId: {
  //     type: Number,
  //     required: true
  //   }
  // })

  const form = ref({
    title: '',
    content: '',
    category: '财务知识',
    attachment: ''
  });
  
  const categories = ['财务知识', '宣传材料', '投资指南'];
  const formRef = ref();
  
// 文件选择变化处理
const handleFileChange = (file) => {
  if (beforeUpload(file.raw)) { // 执行原有的校验逻辑
    fileList.value = [file.raw]; // 存储原始文件对象
  }
};

// 调整beforeUpload函数使其返回校验结果
const beforeUpload = (file) => {
  const isAllowedType = ['application/pdf', 'image/jpeg', 'image/png', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type);
  const isLt10M = file.size / 1024 / 1024 < 10;
  
  if (!isAllowedType) {
    ElMessage.error('仅支持PDF/Word/图片格式!');
    return false;
  }
  if (!isLt10M) {
    ElMessage.error('文件大小不能超过10MB!');
    return false;
  }
  return true;
};
  
  // 提交表单
 // 提交表单
const submitForm = async () => {
  try {
    await formRef.value.validate();
    const formData = new FormData();
    formData.append('title', form.value.title);
    formData.append('content', form.value.content);
    formData.append('category', form.value.category);
    formData.append('advisorID', userInfo.id);
    formData.append('familyID', null);

    // 获取上传的文件（假设使用手动上传）
    if (fileList.value.length > 0) {
      formData.append('file', fileList.value[0]);
    }

    await axios.post('http://localhost:8081/api/materials', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });
    ElMessage.success('发布成功！');
    formRef.value.resetFields();
    fileList.value = []; // 清空已选文件
  } catch (error) {
    ElMessage.error('发布失败，请检查数据');
  }
};

//--------------------------------------------------------------------------
const materials = ref([])
  const loading = ref(true)

  const memberId = JSON.parse(localStorage.getItem('userInfo'))?.id
  
  // 分类选项
  const Categories = ref([
    { label: '财务知识', value: '财务知识' },
    { label: '宣传材料', value: '宣传材料' },
    { label: '投资指南', value: '投资指南' }
  ])
  
  // 默认选中的分类
  const selectedCategory = ref('财务知识')

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
    // await getFamilyId()
    fetchMaterials()
  })

  </script>
  
  <style scoped>
  .publish-container {
    max-width: 800px;
    margin: 20px auto;
    padding: 20px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  }
  </style>