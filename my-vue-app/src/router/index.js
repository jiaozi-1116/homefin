import { createRouter, createWebHistory } from 'vue-router';
import Login from '../views/Login.vue';
import Register from '../views/register.vue';
import Hellow from '../components/HelloWorld.vue'
import Admin from '../views/Admin.vue';
import Member from '../views/Member.vue';
import Advisor from '../views/Advisor.vue';
import EditMember from '../components/EditMember.vue';
import FinancialOverview from '../components/advisor/FinancialOverview.vue';
import ManageClients from'../components/advisor/ManageClients.vue';
import FinancialAnalysis from'../components/advisor/FinancialAnalysis.vue';
import GenerateReports from'../components/advisor/GenerateReports.vue'
import Meetings from'../components/advisor/Meetings.vue'
import PersonalDetails from '../components/advisor/PersonalDetails.vue';
import PublishMaterials from '../components/advisor/PublishMaterials.vue';


const routes = [
  {
    path: '/',
    name: 'Home',
    component: Login
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  { path: '/admin', 
    name: 'Admin', 
    component: Admin,
    children:[
      {
        path:'manage-members',
        name:'ManageMembers',
        component: () => import('../components/admin/ManageMembers.vue')
      },
      {
        path:'manage-budget',
        name:'ManageBudget',
        component: () => import('../components/admin/ManageBudget.vue')
      },
      {
        path:'financial-report',
        name:'FinancialReport',
        component: () => import('../components/admin/FinancialReport.vue')
      },
      {
        path:'announcements',
        name:'announcements',
        component: () => import('../components/admin/Announcements.vue')
      },
      {
        path:'financial-suggestions',
        name:'FinancialSuggestions',
        component: () => import('../components/admin/FinancialSuggestions.vue')
      },
      {
        path:'personal-information',
        name:'PersonalInformation',
        component: () =>  import('../components/admin/PersonalInformation.vue')
      }
    ] 
  },
  {
    path:'/member/:memberName',
    name:'Member',
    component:Member,
    children:[
      {
        path:'financial-knowledge',
        name:'FinancialKnowledge',
        component: () => import('../components/member/FinancialKnowledge.vue')
      },
      {
        path:'record-income',
        name:'RecordIncome',
        component: () => import('../components/member/RecordIncome.vue')
      },
      {
        path:'record-expense',
        name:'RecordExpense',
        component: () => import('../components/member/RecordExpense.vue')
      },
      {
        path:'chart',
        name:'Chart',
        component: () => import('../components/member/Chart.vue')
      },
      {
        path:'suggestion',
        name:'Suggestion',
        component: () => import('../components/member/Suggestion.vue')
      },
      {
        path:'discussion',
        name:'Discussion',
        component: () => import('../components/member/Discussion.vue')
      },
      {
        path:'notice',
        name:'Notice',
        component: () => import('../components/member/Notice.vue')
      },
      {
        path:'budget',
        name:'Budget',
        component: () => import('../components/member/Budget.vue')
      },
      {
        path:'manage-account',
        name:'ManageAccount',
        component: () => import('../components/member/ManageAccount.vue')
      },
    ]
  },
  // {
  //   path:'/advisor',
  //   name:'/Advisor',
  //   component:Advisor
  // },
  {
    path: '/edit-member/:id',
    name: 'EditMember',
    component: EditMember,
    props: true, // 允许通过 URL 传递参数
  },
  {
    path: '/advisor/:advisorName',
    name:'Advisor',
    component: Advisor,
    children: [
      {
        path: 'financial-overview',
        component: FinancialOverview,
        props: true
      },
      {
        path: 'financial-analysis',
        component: FinancialAnalysis
      },
      {
        path: 'generate-reports',
        component: GenerateReports
      },
      // {
      //   path: 'online-consultation',
      //   component: OnlineConsultation
      // },
      // {
      //   path: 'recommend-products',
      //   component: RecommendProducts
      // },
      {
        path: 'manage-clients',
        component: ManageClients
      },
      {
        path: 'meetings',
        component: Meetings
      },
      {
        path: 'publish-materials',
        component: PublishMaterials
      },
      // 默认重定向到第一个菜单
      {
        path: 'personal-details',
        component: PersonalDetails
      },
      {
        path: '', // 默认子路由
        redirect: 'financial-overview' // 直接使用路径名，避免命名路由冲突
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory('/'),
  routes
});

export default router;


