package com.wzhwayne.flink;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;

public class MySQLBinlogSourceExample {
    public static void main(String[] args) throws Exception {
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
            .hostname("localhost")
            .port(3306)
            .databaseList("classicmodels") // set captured database
            .tableList("classicmodels.products") // set captured table
            .username("root")
            .password("wzhwayne123")
            .deserializer(new JsonDebeziumDeserializationSchema()) // converts SourceRecord to JSON String
            .build();
    
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        
        // enable checkpoint
        // env.enableCheckpointing(3000);
        
        env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
            // set 4 parallel source tasks
            .setParallelism(1)
            .print().setParallelism(1); // use parallelism 1 for sink to keep message ordering
        
        env.execute("Print MySQL Snapshot + Binlog");
    }
}