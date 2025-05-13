package com.example.yz.spark.recommendation

import org.apache.spark.sql.DataFrame

class RecommendationEngine {
  def generateRecommendations(comparisonDF: DataFrame): String = {
    // 示例建议逻辑
    val overBudgetCategories = comparisonDF
      .where("sum(Amount) > Amount")
      .select("Category")
      .collect()
      .map(_.getString(0))
      .mkString(", ")

    if (overBudgetCategories.nonEmpty) {
      s"检测到以下类别超支：$overBudgetCategories。建议：1. 审查消费习惯 2. 调整预算分配 3. 设置支出提醒"
    } else {
      "当前预算执行良好，建议保持现有消费模式并定期检查财务状况。"
    }
  }
}
