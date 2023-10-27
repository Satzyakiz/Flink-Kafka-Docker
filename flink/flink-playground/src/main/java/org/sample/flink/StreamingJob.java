package org.sample.flink;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.base.DeliveryGuarantee;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.slf4j.*;
import org.slf4j.LoggerFactory;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;

import java.util.Properties;

public class StreamingJob {

    private static Logger logger = LoggerFactory.getLogger(StreamingJob.class);
    public static void main(String[] args) throws Exception {
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        String kafkaServer = System.getenv("KAFKA_SERVER");
        String sourceTopic =  System.getenv("KAFKA_SOURCE_TOPIC");
        String sinkTopic =  System.getenv("KAFKA_SINK_TOPIC");
        logger.info("Kafka server: " + kafkaServer);
        logger.info("Kafka source topic: " + sourceTopic);
        logger.info("Kafka sink topic: " + sinkTopic);

        logger.info("Flink connecting to producer");
        KafkaSource<String> source = KafkaSource.<String>builder()
                .setBootstrapServers(kafkaServer)
                .setTopics(sourceTopic)
                .setStartingOffsets(OffsetsInitializer.earliest())
                .setValueOnlyDeserializer(new SimpleStringSchema())
                .build();
        DataStream<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");
        logger.info("Flink connected to producer");

        logger.info("Flink connecting to consumer");
        KafkaSink<String> sink = KafkaSink.<String>builder()
                .setBootstrapServers(kafkaServer)
                .setRecordSerializer(KafkaRecordSerializationSchema.builder()
                        .setTopic(sinkTopic)
                        .setValueSerializationSchema(new SimpleStringSchema())
                        .build()
                )
                .setDeliveryGuarantee(DeliveryGuarantee.AT_LEAST_ONCE)
                .build();
        logger.info("Flink connected to consumer");

        logger.info("Starting...");
        stream.sinkTo(sink);
        logger.info("Stopping...");

        env.execute("Kafka to Kafka Flink Job");
    }
}
