package com.example.yz.spark.data

import com.example.yz.spark.config.DBConfig
import org.apache.spark.sql.{DataFrame, SparkSession}

class DataLoader(spark: SparkSession) {
  def loadTable(tableName: String): DataFrame = {
    spark.read
      .format("jdbc")
      .option("url", DBConfig.url)
      .option("dbtable", tableName)
      .option("user", DBConfig.user)
      .option("password", DBConfig.password)
      .option("driver", DBConfig.driver)
      .load()
  }
}
