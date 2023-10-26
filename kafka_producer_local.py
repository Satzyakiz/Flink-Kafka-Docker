from confluent_kafka import Producer
import pandas as pd

# Define Kafka producer configuration
kafka_config = {
    'bootstrap.servers': 'localhost:9092',  # Replace with your Kafka broker address
    'client.id': 'csv_to_kafka_producer'
}

# Define the Kafka topic to publish the CSV data
kafka_topic = 'produced-data'

# Function to send data to Kafka topic
def send_to_kafka(topic, data):
    producer = Producer(kafka_config)

    def delivery_report(err, msg):
        if err is not None:
            print(f'Message delivery failed: {err}')
        else:
            print(f'Message delivered to {msg.topic()} [{msg.partition()}] at offset {msg.offset()}')

    for line in data:
        producer.produce(topic, key=None, value=line, callback=delivery_report)

    producer.flush()

# Read the CSV file using pandas
csv_file_path = 'smallerDataset.csv'
df = pd.read_csv(csv_file_path)

# Convert the DataFrame to a list of JSON records
json_records = df.to_dict(orient='records')

# Convert JSON records to strings
csv_data = [str(record) for record in json_records]

# Send data to Kafka
send_to_kafka(kafka_topic, csv_data)
