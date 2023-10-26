import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;

import java.util.Properties;

public class CEP_Processor {

    public static void main(String[] args) throws Exception {
        // System.out.println("Hello World");
        // Set up the Flink execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Configure Kafka properties
        String kafkaServer = System.getenv("KAFKA_SERVER");
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", kafkaServer); // Replace with your Kafka broker(s) address
        properties.setProperty("group.id", "flink-consumer-group"); // Consumer group ID

        // Create a Flink Kafka Consumer with a String deserializer
        FlinkKafkaConsumer<String> kafkaConsumer = new FlinkKafkaConsumer<>(
                "data-from-file", // Replace with the name of your Kafka topic
                new SimpleStringSchema(),
                properties);

        // Add the Kafka Consumer as a data source to the Flink job
        DataStream<String> kafkaStream = env.addSource(kafkaConsumer);

        // You can now apply transformations or process the Kafka data stream as needed
        // For example, you can print the messages to the console:
        kafkaStream.print();

        // Execute the Flink job
        env.execute("Kafka Topic Reader");
    }
}
