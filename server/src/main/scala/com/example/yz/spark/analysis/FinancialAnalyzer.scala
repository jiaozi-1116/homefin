package com.example.yz.spark.analysis

import com.example.yz.spark.data.DataLoader
import com.example.yz.spark.recommendation.RecommendationEngine
import org.apache.spark.sql.{DataFrame, SparkSession}

class FinancialAnalyzer(spark: SparkSession) {

  private val dataLoader = new DataLoader(spark)

  // 获取基础数据
  private def getBudgetData(): DataFrame = dataLoader.loadTable("budgets")
  private def getExpenseData(): DataFrame = dataLoader.loadTable("expenses")
  private def getIncomeData(): DataFrame = dataLoader.loadTable("incomes")

  // 核心分析方法
  def analyzeFamilyFinance(familyID: Int): String = {
    val budgetDF = getBudgetData().filter(s"FamilyID = $familyID")
    val expenseDF = getExpenseData().filter(s"FamilyID = $familyID")
    val incomeDF = getIncomeData().filter(s"FamilyID = $familyID")

    // 实现分析逻辑（示例）
    val monthlyExpense = expenseDF.groupBy("Category").sum("Amount")
    val budgetComparison = budgetDF.join(monthlyExpense, "Category")

    // 生成建议
    new RecommendationEngine().generateRecommendations(budgetComparison)
  }
}
