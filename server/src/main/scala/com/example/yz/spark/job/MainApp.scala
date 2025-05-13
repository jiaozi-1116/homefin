package com.example.yz.spark.job


import com.example.yz.spark.analysis.FinancialAnalyzer
import org.apache.spark.sql.SparkSession

object MainApp {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("HomeFin Analysis")
      .master("local[*]")
      .getOrCreate()

    val analyzer = new FinancialAnalyzer(spark)
    val recommendation = analyzer.analyzeFamilyFinance(1)  // 分析FamilyID=1的家庭

    println("="*50)
    println("财务建议：")
    println(recommendation)
    println("="*50)

    spark.stop()
  }
}
