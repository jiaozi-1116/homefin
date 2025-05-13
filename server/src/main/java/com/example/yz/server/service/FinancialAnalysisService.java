//package com.example.yz.server.service;
//
//import org.apache.spark.sql.*;
//import org.apache.spark.sql.functions;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service// 示例Spark分析服务
//public class FinancialAnalysisService {
//
//    public void analyzeFamilySpending(int familyId) {
//        SparkSession spark = SparkSession.builder()
//                .appName("FamilyFinancialAnalysis")
//                .master("local[*]")
//                .getOrCreate();
//
//        // 从数据库加载数据到DataFrame
//        Dataset<Row> expenses = spark.read()
//                .format("jdbc")
//                .option("url", "jdbc:mysql://localhost:3306/homefin")
//                .option("dbtable", "expenses")
//                .option("user", "username")
//                .option("password", "password")
//                .load()
//                .filter("FamilyID = " + familyId);
//
//        // 执行分析
//        Dataset<Row> categoryAnalysis = expenses.groupBy("Category")
//                .agg(functions.sum("Amount").as("TotalAmount"))
//                .orderBy(functions.desc("TotalAmount"));
//
//        // 保存结果或返回给前端
//        categoryAnalysis.show();
//    }
//}
//
