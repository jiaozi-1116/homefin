<template>
  <div>
    <!----------------------------------------------------设置和管理预算 -------------------------------------------------->
    <section id="manage-budget">
      <el-card>
        <h2>设置和管理家庭预算</h2>

        <!-- 表格显示预算内容 -->
        <el-table :data="budgets" style="width: 100%">
          <el-table-column prop="amount" label="预算金额" width="150">
            <template #default="scope">
              <el-input 
                v-model="scope.row.amount" 
                type="number"
                @blur="updateBudget(scope.row)"
              ></el-input>
            </template>
          </el-table-column>

          <el-table-column prop="startDate" label="开始日期" width="250">
            <template #default="scope">
              <el-date-picker 
                v-model="scope.row.startDate" 
                type="date" 
                placeholder="选择日期" 
                format="YYYY-MM-DD"
                @change="updateBudget(scope.row)"
              ></el-date-picker>
            </template>
          </el-table-column>

          <el-table-column prop="endDate" label="结束日期" width="250">
            <template #default="scope">
              <el-date-picker 
                v-model="scope.row.endDate" 
                type="date" 
                placeholder="选择日期" 
                format="YYYY-MM-DD"
                @change="updateBudget(scope.row)"
              ></el-date-picker>
            </template>
          </el-table-column>

          <el-table-column prop="category" label="预算类别" width="200">
            <template #default="scope">
              <el-select v-model="scope.row.category" placeholder="请选择类别" @change="updateBudget(scope.row)">
                <el-option 
            v-for="category in expenseCategories" 
            :key="category" 
            :label="category" 
            :value="category"
          ></el-option>
              </el-select>
            </template>
          </el-table-column>

          <el-table-column label="预算进度" width="300">
            <template #default="scope">
              <el-progress 
                :text-inside="true" 
                :stroke-width="24"
                :percentage="calcPercentage(scope.row._categorySpent, scope.row.amount)"
                :status="getProgressStatus(scope.row)"
                :color="customColor"
              >
                <span class="progress-text">
                  {{ formatCurrency(scope.row._categorySpent) }} / {{ formatCurrency(scope.row.amount) }}
                </span>
              </el-progress>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button type="danger" @click="deleteBudget(scope.row)">删除</el-button>
              <el-button type="primary" @click="showExpenseDetails(scope.row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 添加新预算 -->
        <el-button type="primary" @click="showAddDialog" style="margin-top: 15px;">添加预算</el-button>

      </el-card>
    </section>


    <!-- 花费详情对话框 -->
    <el-dialog v-model="expenseDetailsVisible" title="花费详情" width="50%">
      <el-table :data="expenseDetails" style="width: 100%">
        
        <el-table-column prop="amount" label="花费金额">
          <template #default="scope">
            {{ formatCurrency(scope.row.amount) }}
          </template>
        </el-table-column>
        <el-table-column prop="date" label="时间" :formatter="formatDate"></el-table-column>
        <el-table-column prop="description" label="描述"></el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="expenseDetailsVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 添加预算对话框 -->
    <el-dialog v-model="dialogVisible" title="新增预算" width="30%">
      <el-form :model="newBudget" label-width="80px">
        <el-form-item label="金额" required>
          <el-input v-model.number="newBudget.amount" type="number"></el-input>
        </el-form-item>
        
        <el-form-item label="开始日期" required>
          <el-date-picker
            v-model="newBudget.startDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          ></el-date-picker>
        </el-form-item>

        <el-form-item label="结束日期" required>
          <el-date-picker
            v-model="newBudget.endDate"
            type="date"
            placeholder="选择日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          ></el-date-picker>
        </el-form-item>

        <el-form-item label="类别" required>
          <el-select v-model="newBudget.category" placeholder="请选择类别">
            <el-option 
          v-for="category in expenseCategories" 
          :key="category" 
          :label="category" 
          :value="category"
        ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddBudget">确定</el-button>
      </template>
    </el-dialog>
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

  const userInfo = JSON.parse(localStorage.getItem('userInfo'))

  const adminName = ref(userInfo.name)
  const adminId = ref(userInfo.id)
  const memberId = ref(userInfo.id)

  let familyID = ref(null);

  const budgets = ref([]);

  // 新增对话框相关状态
  const dialogVisible = ref(false)
  const newBudget = ref({
    amount: null,
    startDate: '',
    endDate: '',
    category: ''
  })

  // 显示添加对话框
  const showAddDialog = () => {
    resetForm()
    dialogVisible.value = true
  }

  // 确认添加预算
  const confirmAddBudget = async () => {
    if (!validateForm()) {
      ElMessage.warning('请填写所有必填字段')
      return
    }
    
    const budgetData = {
      ...newBudget.value,
      userId: memberId.value,
      isNew: true
    }
    
    try {
      // 直接调用保存方法
      const response = await axios.post('http://localhost:8081/api/budgets', budgetData)
      if (response.data) {
        ElMessage.success('预算添加成功')
        await fetchBudgets()
        dialogVisible.value = false
      }
    } catch (error) {
      console.error('添加预算失败:', error)
      ElMessage.error('添加预算失败')
    }
  }

  // 表单验证
  const validateForm = () => {
    return (
      newBudget.value.amount > 0 &&
      newBudget.value.startDate &&
      newBudget.value.endDate &&
      newBudget.value.category
    )
  }

  // 重置表单
  const resetForm = () => {
    newBudget.value = {
      amount: null,
      startDate: '',
      endDate: '',
      category: ''
    }
  }
  //---------------------------

  const fetchBudgets = async () => {
    try {
      //获取预算
      const memberId = userInfo.id;
      const response = await axios.get(`http://localhost:8081/api/budgets/${memberId}`);
      budgets.value = response.data;  // 替换预算数据，确保 UI 更新
      
      // 获取每个预算类别的总花费
      for (const budget of budgets.value) {
        const startDate = new Date(budget.startDate);
        const endDate = new Date(budget.endDate);
        const summaryResponse = await axios.get(`http://localhost:8081/api/expense/category-summary/member/${memberId}`, {
          params: {
            startDate: startDate.toISOString().split('T')[0],
            endDate: endDate.toISOString().split('T')[0]
          }
        });
        console.log(summaryResponse)
        budget._categorySpent = summaryResponse.data[budget.category] || 0;
      }
    } catch (error) {
      console.error("Error fetching budgets:", error);
    }
  };

  onMounted(() =>{
    fetchBudgets()
  })

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

  const updateBudget = async (budget) => {
    try {
      await axios.put(`http://localhost:8081/api/budgets/${budget.budgetId}`, budget);
      ElMessage.success('预算已更新');
      // 更新预算后重新获取预算列表
      await fetchBudgets();
    } catch (error) {
      ElMessage.error('更新失败');
    }
  };

  const categories = ref([
    { label: '餐饮', value: 'Food' },
    { label: '交通', value: 'Transportation' },
    { label: '娱乐', value: 'Entertainment' },
    { label: '服饰', value: 'Clothing' },
    { label: '购物', value: 'Shopping' }
  ])

  const calcPercentage = (spent, amount) => {
    if (!amount || amount <= 0) return 0;
    const percentage = (spent / amount) * 100;
    return Math.min(percentage, 100).toFixed(1); // 最大显示100%
  };

  const getProgressStatus = (row) => {
    if (row._categorySpent > row.amount) return 'exception';
    if (row._categorySpent / row.amount >= 0.8) return 'warning';
    return '';
  };

  const customColor = '#409eff'; // 自定义正常状态颜色

  const formatCurrency = (value) => {
    return '¥' + (value ? Number(value).toFixed(2) : '0.00');
  };

  const expenseDetailsVisible = ref(false)
  const expenseDetails = ref([])

    // 显示花费详情对话框
    const showExpenseDetails = async (budget) => {
    try {
      const startDate = new Date(budget.startDate);
      const endDate = new Date(budget.endDate);
      const response = await axios.get(`http://localhost:8081/api/expense/member-category/${adminId.value}`, {
        params: {
          startDate: startDate.toISOString().split('T')[0],
          endDate: endDate.toISOString().split('T')[0],
          category: budget.category
        }
      });
      expenseDetails.value = response.data;
      expenseDetailsVisible.value = true;
    } catch (error) {
      console.error('获取花费详情失败:', error);
      ElMessage.error('获取花费详情失败');
    }
  };

  const formatDate = (row, column, cellValue) => { 
      const date = new Date(cellValue); 
      const year = date.getFullYear(); 
      const month = (date.getMonth() + 1).toString().padStart(2, '0'); 
      const day = date.getDate().toString().padStart(2, '0'); 
      return `${year}-${month}-${day}`; 
    }


    const expenseCategories = ref([
  '餐饮',
  '购物',
  '日用',
  '交通',
  '蔬菜',
  '饮品',
  '水果',
  '零食',
  '服饰',
  '外卖',
  '买菜',
  '运动',
  '娱乐',
  '花费',
  '房租',
  '房贷',
  '社交',
  '礼物',
  '旅行',
  '烟酒',
  '汽车',
  '水电费',
  '办公',
  '礼金',
  '转账',
  '麻将',
  '其他',
]);
</script>    