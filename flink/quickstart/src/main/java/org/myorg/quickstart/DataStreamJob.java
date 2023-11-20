/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.myorg.quickstart;

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
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.cep.CEP;
import org.apache.flink.cep.pattern.Pattern;
import org.apache.flink.cep.pattern.conditions.SimpleCondition;
import org.apache.flink.cep.PatternStream;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.streaming.api.functions.ProcessFunction.Context;

import java.util.Properties;
import java.util.List;
import java.util.Map;
import java.io.*;

/**
 * Skeleton for a Flink DataStream Job.
 *
 * <p>For a tutorial how to write a Flink application, check the
 * tutorials and examples on the <a href="https://flink.apache.org">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class DataStreamJob {

    private static Logger logger = LoggerFactory.getLogger(DataStreamJob.class);

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

        DataStream<BasicEvent> eventStream = stream.map(data -> BasicEvent.fromString(data));

        Pattern<BasicEvent, ?> pattern = Pattern.<BasicEvent>begin("start")
        .where(SimpleCondition.of(event -> {
                return event.getValueRaw() > 440;
            }))
        .followedBy("end")
        .where(SimpleCondition.of(event -> {
                return event.getValueRaw() < 450;
            }));

        PatternStream<BasicEvent> patternStream = CEP.pattern(eventStream, pattern).inProcessingTime();

        DataStream<String> result = patternStream.process(
        new PatternProcessFunction<BasicEvent, String>() {
                @Override
                public void processMatch(Map<String, List<BasicEvent>> pattern,
                                        Context ctx,
                                        Collector<String> out) throws Exception {
                        List<BasicEvent> events = pattern.get("end");
                        for (BasicEvent event : events) {
                                logger.info("Adding event " + event.toString());
                                out.collect(event.toString());
                        }
                }
        });

        logger.info("Starting...");
        result.sinkTo(sink);
        logger.info("Stopping...");

        env.execute("Kafka to Kafka Flink Job");
    }
	
}
