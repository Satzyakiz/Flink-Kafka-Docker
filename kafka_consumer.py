import os
import json
from kafka import KafkaConsumer

kafka_server=os.getenv('KAFKA_SERVER')
kafka_topic=os.getenv('KAFKA_TOPIC')
print("Kafka server - " + kafka_server)
print("Kafka topic - " + kafka_topic)
consumer = KafkaConsumer(kafka_topic,
                         client_id='c1',
                         bootstrap_servers=[kafka_server],
                         group_id=None,
                         api_version=(0, 10, 1),
                         value_deserializer=lambda m: json.loads(m.decode('utf-8')))

print('Listening for topic ' + str(kafka_topic))
recv_count = 0
for message in consumer:
    print(message.value)
    recv_count += 1
    print('Messages received: ' + str(recv_count))
