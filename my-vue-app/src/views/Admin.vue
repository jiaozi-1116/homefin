<template>
    <div>
      <!-- 页面头部，显示管理员信息 -->
      <el-page-header 
        @back="goBack" 
        content="家庭管理员页面"
        class="page-header"
        >
        <template #title>
          管理员: {{ adminName }}
        </template>
      </el-page-header>
      
      <!-- 横向菜单 -->
      <el-menu 
      :default-active="$route.path" 
      mode="horizontal" 
      router
      >
        <el-menu-item :index="'/admin/manage-members'">管理家庭成员信息</el-menu-item>
        <el-menu-item :index="'/admin/manage-budget'">设置和管理家庭预算</el-menu-item>
        <el-menu-item index="3">查看和分析家庭财务报告</el-menu-item>
        <el-menu-item index="4">发布家庭财务公告和通知</el-menu-item>
        <el-menu-item index="5">处理家庭成员的财务建议</el-menu-item>
      </el-menu>
  
      <el-container>
        <el-main>
          <router-view :adminId = "adminId"></router-view>

 <!-- -----------------------------------------------查看和分析家庭财务报告 ----------------------------------------------->
          <div v-show="activeMenu === '3'">
            <section id="financial-report">
      <h2>查看和分析家庭财务报告</h2>
      
      <div style="width: 200px;">
        <label for="reportType">选择报告类型:</label>
        <select v-model="reportType" id="reportType">
          <option value="yearly">年度报告</option>
          <option value="monthly">月度报告</option>
        </select>
      </div>
      
      <div v-if="reportType === 'yearly'">
        <label for="year">选择年份:</label>
        <select v-model="selectedYear" id="year">
          <option v-for="year in years" :key="year" :value="year">{{ year }}</option>
        </select>
      </div>
      
      <div v-if="reportType === 'monthly'">
        <label for="year">选择年份:</label>
        <select v-model="selectedYear" id="year">
          <option v-for="year in years" :key="year" :value="year">{{ year }}</option>
        </select>
        
        <label for="month">选择月份:</label>
        <select v-model="selectedMonth" id="month">
          <option v-for="month in months" :key="month" :value="month">{{ month }}</option>
        </select>
      </div>
      
      <div>
        <button @click="confirmSelection">确认选择</button>
      </div>
      
      <div v-show="showCharts">
        <h3>收入图表</h3>
        <div ref="incomeChart" id="incomeChart"></div>
        
        <h3>支出图表</h3>
        <div ref="expenseChart" id="expenseChart"></div>
      </div>
    </section>
          </div>
  

<!-- --------------------------------------------------发布家庭财务公告和通知---------------------------------------- -->
<div v-if="activeMenu === '4'">
  <!-- 发布家庭财务公告和通知 -->
  <section id="announcements">
    <el-card>
      <h2>发布家庭财务公告和通知</h2>

      <!-- 显示已发布的公告 -->
      <el-table :data="announcements" style="width: 100%">
  <el-table-column label="标题" prop="title"></el-table-column>
  <el-table-column label="内容" prop="content"></el-table-column>
  <el-table-column label="操作">
    <template #default="scope">
      <el-button type="danger" size="mini" @click="deleteAnnouncement(scope.row.noticeId)">删除</el-button>
    </template>
  </el-table-column>
</el-table>


      <!-- 发布新公告 -->
      <el-form :model="announcementForm" @submit.prevent="publishAnnouncement">
        <el-form-item label="标题">
          <el-input v-model="announcementForm.title"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="announcementForm.content"></el-input>
        </el-form-item>
        <el-button type="primary" native-type="submit">发布</el-button>
      </el-form>
    </el-card>
  </section>
</div>

<!-- -----------------------------------------------处理家庭成员的财务建议------------------------------------ -->
<div v-if="activeMenu === '5'">
  <!-- 处理家庭成员的财务建议 -->
  <section id="financial-suggestions">
    <el-card>
      <h2>处理家庭成员的财务建议</h2>
      
      <!-- 建议列表 -->
      <el-table :data="suggestions" style="width: 100%">
        <el-table-column prop="content" label="建议内容" width="300"></el-table-column>
        <el-table-column label="提交人" width="150">
          <template #default="scope">
            {{ getUserName(scope.row.userId) }}
          </template>
        </el-table-column>
        <el-table-column prop="date_submitted"  width="150" ></el-table-column>
        <el-table-column label="状态" width="150">
          <template #default="scope">
            <el-select 
              v-model="scope.row.status" 
              @change="updateSuggestionStatus(scope.row)"
              placeholder="选择状态"
            >
              <el-option label="待处理" value="Pending"></el-option>
              <el-option label="已审核" value="Reviewed"></el-option>
              <el-option label="已实施" value="Implemented"></el-option>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button @click="showDetail(scope.row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 建议详情对话框 -->
    <el-dialog v-model="detailVisible" title="建议详情">
      <div v-if="selectedSuggestion">
        <p><strong>提交人:</strong> {{ getUserName(selectedSuggestion.userId) }}</p>
        <p><strong>提交时间:</strong> {{ formatDateFull(selectedSuggestion.dateSubmitted) }}</p>
        <p><strong>建议内容:</strong></p>
        <el-input 
          type="textarea" 
          :rows="4" 
          v-model="selectedSuggestion.content" 
          readonly
        ></el-input>
      </div>
    </el-dialog>
  </section>
</div>

        </el-main>
      </el-container>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive } from 'vue'
  import { useRoute } from 'vue-router'
  import axios from 'axios'
  import { onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus';
  import { ElMessageBox } from 'element-plus'
  import * as echarts from 'echarts';
  import Chart from 'chart.js/auto';
  import { nextTick } from 'vue';

  //------------------------------------------验证登录----------------------------------------------

  const router = useRouter()
  const route = useRoute()
// 使用 ref 来存储 adminId 和 adminName
const adminName = ref('')
const adminId = ref('')

// 检查是否有登录信息
const userInfo = JSON.parse(localStorage.getItem('userInfo'))

onMounted(() => {
  if (userInfo) {
    // 如果有用户信息，恢复用户状态
    console.log('已登录用户:', userInfo)

    adminId.value = userInfo.id
    adminName.value = userInfo.name
  } else {
    // 如果没有用户信息，跳转到登录页面
    router.push({ name: 'Login' })
  }
console.log('Admin ID:', adminId.value)
console.log('Admin Name:', adminName.value)

})

// 如果没有用户信息，你可以在这里使用 route.params 获取 adminId 和 adminName
// if (!userInfo) {
//   adminId.value = route.params.adminId
//   adminName.value = route.params.adminName
// }




//--------------------------------------------显示家庭成员----------------------------
  const activeMenu = ref('1') // 默认激活第一个菜单项
  
  const members = ref([])
  //获取家庭成员
  const fetchFamilyMembers = async () => { 
    try { 
      const response = await axios.get(`http://localhost:8081/api/family/${adminId.value}/members`) // 假设家庭管理员ID为1 
        members.value = response.data 
    } catch (error) { 
          console.error("Error fetching family members:", error) 
      } 
  }

    onMounted(() => { 
        fetchFamilyMembers() 
    })

    const formatDate = (row, column, cellValue) => { 
      const date = new Date(cellValue); 
      const year = date.getFullYear(); 
      const month = (date.getMonth() + 1).toString().padStart(2, '0'); 
      const day = date.getDate().toString().padStart(2, '0'); 
      return `${year}-${month}-${day}`; 
    }


  //----------------------------------------------------添加家庭成员---------------------------------------------
  const users = ref([]);  // 存储所有用户


  const dialogVisible = ref(false);
  const form = ref({
  userId: '',
  relationship: ''
});

 let familyID = ref(null);

// 获取所有用户
onMounted(async () => {
  try {
    const response = await axios.get('http://localhost:8081/api/users');
    users.value = response.data;
  } catch (error) {
    ElMessage.error('加载用户数据失败');
  }

   // 获取家庭ID
   try {
    const response = await axios.get(`http://localhost:8081/api/family/${adminId.value}`);
    familyID.value = response.data;
  } catch (error) {
    console.error('获取家庭ID失败', error);
  }
});


  
  const addMember = () => {
    // 添加成员逻辑
    dialogVisible.value = true;
  }


  // 保存新的家庭成员
const saveMember = async () => {
  try {
    const response = await axios.post(`http://localhost:8081/api/family/${familyID.value}/addMember`, {
      userId: form.value.userId,
      relationship: form.value.relationship
    });

    if (response.data === '家庭成员添加成功') {
      ElMessage.success('家庭成员添加成功');
      dialogVisible.value = false;
      // 调用已有的 fetchFamilyMembers 方法来更新家庭成员数据
      await fetchFamilyMembers();  // 获取并更新家庭成员列表
    }
  } catch (error) {
    ElMessage.error('添加成员失败');
  }
};

//-------------------------------------------编辑家庭成员----------------------------------------------------------
  
  // 跳转到编辑页面
  const editMember = (members) => {
    router.push({ name: 'EditMember', params: { id: members.id } });
  };

 //----------------------------------------------删除家庭成员----------------------------------------------------
  
 const deleteMember = async (member) => {
  try {
    // 弹出确认框，询问是否确认删除成员
    const result = await ElMessageBox.confirm(
      '确定要删除此家庭成员吗？', 
      '提示', 
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );

    // 如果用户点击“确定”，继续删除操作
    if (result === 'confirm') {
      // 调用后端 API 删除家庭成员，传递 userId 作为唯一标识符
      const response = await axios.delete(`http://localhost:8081/api/family/deleteMember`, {
        data: { userId: member.id }  // 传递要删除的成员的 userId
      });

      if (response.data === '删除成功') {
        ElMessage.success('家庭成员删除成功');
        
        // 从本地列表中移除该成员
        // members.value = members.value.filter(item => item.userId !== member.userId);
        // 调用已有的 fetchFamilyMembers 方法来更新家庭成员数据
        await fetchFamilyMembers();  // 获取并更新家庭成员列表
      } else {
        ElMessage.error('删除失败');
      }
    }
  } catch (error) {
    // 如果用户点击“取消”或发生其他错误，弹出提示
    if (error === 'cancel') {
      ElMessage.info('删除操作已取消');
    } else {
      console.error('删除家庭成员时发生错误:', error);
      ElMessage.error('删除失败');
    }
  }
};
  
//--------------------------------------------------------设置和管理预算------------------------------------------------
const budgets = ref([]);

  const fetchBudgets = async () => {
    try {
    const response = await axios.get(`http://localhost:8081/api/family/${adminId.value}`);
    familyID.value = response.data;
  } catch (error) {
    console.error('获取家庭ID失败', error);
  }
  
  try {
    const response = await axios.get(`http://localhost:8081/api/budgets/family/${familyID.value}`);
    budgets.value = response.data;  // 替换预算数据，确保 UI 更新
  } catch (error) {
    console.error("Error fetching budgets:", error);
  }
};

 onMounted(() =>{
  fetchBudgets()
 })

// 添加预算方法
const addBudget = () => {
  const newBudget = {
    userId: null,
    familyId: familyID.value,
    amount: 0,       // 默认金额为 0
    startDate: '',   // 默认开始日期为空
    endDate: '',     // 默认结束日期为空
    category: '',     // 默认类别为空
    isNew: true,  
  };
  budgets.value.push(newBudget);  // 向预算列表中添加新项
};

// 删除预算方法
const deleteBudget = async (budget) => {
  try {
    await axios.delete(`http://localhost:8081/api/budgets/${budget.budgetId}`);
    console.log('预算删除成功');
    await fetchBudgets();  // 删除后重新获取预算列表
  } catch (error) {
    console.error('删除预算时发生错误:', error);
  }
};

//保存预算
const saveBudgets = async () => {
  try {
    for (const budget of budgets.value) {
      if (budget.isNew == true) {
        // 新增预算，发送 POST 请求
        const response = await axios.post('http://localhost:8081/api/budgets', budget);
        if (response.data) {
          ElMessage.success('新预算已保存');
        }
      } else {
        if (!budget.budgetId) {
          console.error('预算ID未定义，无法更新');
          continue;  // 如果没有 budgetId，跳过该预算
        }
        // 修改已有预算，发送 PUT 请求
        const response = await axios.put(`http://localhost:8081/api/budgets/${budget.budgetId}`, budget);
        if (response.data) {
          ElMessage.success('预算已更新');
        }
      }
    }
    await fetchBudgets();  // 获取更新后的预算数据
  } catch (error) {
    console.error('保存预算时发生错误:', error);
    ElMessage.error('保存预算失败');
  }
};

//---------------------------------------------家庭财务报告---------------------------------------------------
// 定义响应式数据

const reportType = ref('yearly');
  const selectedYear = ref(new Date().getFullYear());
  const selectedMonth = ref(new Date().getMonth() + 1);
  const years = reactive([2023, 2024, 2025]);
  const months = reactive([1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]);
  const showCharts = ref(false);
  
  const incomeChart = ref(null);
  const expenseChart = ref(null);
  let incomeChartInstance = null;
  let expenseChartInstance = null;
  
  // 获取收入数据
  const getIncomeData = async (year, month) => {
    const url = month 
      ? `http://localhost:8081/api/reports/incomes?year=${year}&month=${month}` 
      : `http://localhost:8081/api/reports/incomes?year=${year}`;
    const response = await fetch(url);
    const data = await response.json();
    console.log(`Income Data for ${year}-${month || ''}:`, data); // 调试输出
    return data;
  };
  
  // 获取支出数据
  const getExpenseData = async (year, month) => {
    const url = month 
      ? `http://localhost:8081/api/reports/expenses?year=${year}&month=${month}` 
      : `http://localhost:8081/api/reports/expenses?year=${year}`;
    const response = await fetch(url);
    const data = await response.json();
    console.log(`Expense Data for ${year}-${month || ''}:`, data); // 调试输出
    return data;
  };
  
  // 初始化图表
  const initCharts = () => {
    if (!incomeChart.value || !expenseChart.value) return;
  
    if (!incomeChartInstance) {
      incomeChartInstance = echarts.init(incomeChart.value);
      incomeChartInstance.setOption({
        title: {
          text: '收入图表'
        },
        tooltip: {},
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '收入',
          type: 'bar',
          data: [] // 数据将在获取后设置
        }]
      });
    }
  
    if (!expenseChartInstance) {
      expenseChartInstance = echarts.init(expenseChart.value);
      expenseChartInstance.setOption({
        title: {
          text: '支出图表'
        },
        tooltip: {},
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [{
          name: '支出',
          type: 'bar',
          data: [] // 数据将在获取后设置
        }]
      });
    }
  };
  
  const confirmSelection = async () => {
    const year = selectedYear.value;
    const month = reportType.value === 'monthly' ? selectedMonth.value : null;
  
    const incomeData = await getIncomeData(year, month);
    const expenseData = await getExpenseData(year, month);
  
    showCharts.value = true;
  
    // 更新收入图表数据
    const incomeValues = Object.values(incomeData);
    const incomeLabels = Object.keys(incomeData);
    console.log("Setting income chart data:", incomeValues);
  
    if (incomeChartInstance) {
      incomeChartInstance.setOption({
        xAxis: {
          data: incomeLabels
        },
        series: [{
          data: incomeValues // 确保这里的数据格式与API返回的收入数据匹配
        }]
      });
    }
  
    const expenseCategories = Object.keys(expenseData);
    const expenseValues = Object.values(expenseData);
  
    // 更新支出图表数据
    console.log("Setting expense chart data:", expenseCategories, expenseValues);
  
    if (expenseChartInstance) {
      expenseChartInstance.setOption({
        xAxis: {
          data: expenseCategories
        },
        series: [{
          data: expenseValues
        }]
      });
    }
  };
  
  onMounted(() => {
    initCharts();
  
    // 监听窗口调整大小事件并调整图表大小
    window.addEventListener('resize', () => {
      if (incomeChartInstance) incomeChartInstance.resize();
      if (expenseChartInstance) expenseChartInstance.resize();
    });
  });














  //----------------------------------------------------发布公告---------------------------------------------------
  
  
  const announcements = ref([]);
const announcementForm = ref({

  title: '',
  content: ''
});

// 获取公告列表
const fetchAnnouncements = async () => {
  try {
    const response = await axios.get(`http://localhost:8081/api/family/${adminId.value}`);
    familyID.value = response.data;
  } catch (error) {
    console.error('获取家庭ID失败', error);
  }

  try {
    const response = await axios.get(`http://localhost:8081/api/notices/family/${familyID.value}`);  // 从后端获取公告列表
    console.log('公告列表:', response.data); // 调试信息
    announcements.value = response.data;
  } catch (error) {
    console.error('无法获取公告列表:', error);
  }
};

// 发布新公告
const publishAnnouncement = async () => {
  if (announcementForm.value.title && announcementForm.value.content) {
    const newAnnouncement = {
      familyId: familyID.value,
      adminId: adminId.value,
      title: announcementForm.value.title,
      content: announcementForm.value.content
    };
    console.log('发布新公告', newAnnouncement)

    try {
      const response = await axios.post('http://localhost:8081/api/notices', newAnnouncement);  // 发布公告到后端
      announcements.value.push(response.data);  // 更新本地公告列表
      // 重置表单
      announcementForm.value.title = '';
      announcementForm.value.content = '';
    } catch (error) {
      console.error('发布公告失败:', error);
    }
  } else {
    alert('标题和内容不能为空');
  }
};

// 删除公告
const deleteAnnouncement = async (id) => {
  try {
    await axios.delete(`http://localhost:8081/api/notices/${id}`);  // 向后端发送删除请求
    announcements.value = announcements.value.filter(announcement => announcement.id !== id);
    alert('公告已删除');
    await fetchAnnouncements(); // 重新获取公告列表
  } catch (error) {
    console.error('删除公告失败:', error);
  }


};

// 在组件挂载时获取公告列表
onMounted(fetchAnnouncements);


  //--------------------------------------------------------------------------------------------------------------------
    const handleMenuSelect = (index) => {
    activeMenu.value = index
  }
  const goBack = () => {
    router.back()
    // 返回上一页逻辑
  }



  //------------------------------------ 财务建议处理 ---------------------------------------------------------------
const suggestions = ref([]);
const detailVisible = ref(false);
const selectedSuggestion = ref(null);

// 获取建议列表
const fetchSuggestions = async () => {
  try {
    // 先获取家庭ID
    const familyRes = await axios.get(`http://localhost:8081/api/family/${adminId.value}`);
    const familyID = familyRes.data;
    
    // 获取该家庭的所有建议
    const response = await axios.get(`http://localhost:8081/api/suggestion/family/${familyID}`);
    suggestions.value = response.data;
  } catch (error) {
    ElMessage.error('获取建议失败');
    console.error("Error fetching suggestions:", error);
  }
};

// 更新建议状态
const updateSuggestionStatus = async (suggestion) => {
  try {
    await axios.put(`http://localhost:8081/api/suggestion/${suggestion.suggestionID}?status=${suggestion.status}`);
    ElMessage.success('状态已更新');
  } catch (error) {
    ElMessage.error('更新状态失败');
    console.error("Error updating suggestion:", error);
  }
};

// 显示建议详情
const showDetail = (suggestion) => {
  selectedSuggestion.value = suggestion;
  detailVisible.value = true;
};

// 通过UserID获取用户名
const getUserName = (userId) => {
  const user = users.value.find(u => u.userId === userId);
  return user ? user.fullName : '未知用户';
};

// 完整日期格式化
const formatDateFull = (dateStr) => {
  const date = new Date(dateStr);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 初始化时获取数据
onMounted(() => {
  fetchSuggestions();
  fetchUsers(); // 确保已获取所有用户数据
});

// 获取所有用户（补充到已有users获取逻辑）
const fetchUsers = async () => {
  try {
    const response = await axios.get('http://localhost:8081/api/users');
    users.value = response.data;
  } catch (error) {
    ElMessage.error('加载用户数据失败');
  }
};
  </script>
  

 


  <style scoped>
  /* 固定页面头部到顶部，并使其宽度占满整个浏览器宽度 */
.page-header {
  position: fixed;   /* 固定定位 */
  top: 0;             /* 靠近顶部 */
  left: 0;            /* 靠近左侧 */
  width: 100%;        /* 宽度等于浏览器的宽度 */
  height: 5%;
  z-index: 1000;      /* 确保在其他元素上面 */
  background-color: #fff; /* 可选，确保头部背景为白色 */
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); /* 可选，添加阴影效果 */
}


.el-menu--horizontal {
  --el-menu-horizontal-height: 70px;
  margin-top: 70px;
}





  </style>
  