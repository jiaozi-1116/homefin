//package com.example.yz.server.config;
//
//
//import org.apache.spark.SparkConf;
//import org.apache.spark.sql.SparkSession;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SparkConfig {
//
//    @Bean
//    public SparkSession sparkSession() {
//        SparkConf conf = new SparkConf()
//                .setAppName("HomeFinancialAnalysis")
//                .setMaster("local[*]") // 生产环境应移除此项使用集群配置
//                .set("spark.driver.host", "localhost")
//                .set("spark.executor.memory", "2g")
//                .set("spark.driver.memory", "2g");
//
//        return SparkSession.builder()
//                .config(conf)
//                .getOrCreate();
//    }
//}
