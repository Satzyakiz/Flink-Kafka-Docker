import os
import json
import glob
import gzip
import csv

from kafka import KafkaProducer

kafka_server=os.getenv('KAFKA_SERVER')
kafka_topic=os.getenv('KAFKA_TOPIC')
data_dir='/data/'

# create topic if does not exist
# auto.topic.create.enable=true
# from kafka import KafkaClient
# client = KafkaClient(bootstrap_servers=kafka_server, client_id='tweets_from_file_client')
print("Kafka server - " + kafka_server)
print("Kafka topic - " + kafka_topic)

producer = KafkaProducer(bootstrap_servers=kafka_server,
                        api_version=(0, 10, 1),
                         value_serializer=lambda v: json.dumps(v).encode('utf-8'))

total_records_count = 0

def stream_from_file(filename: str,
                     format_csv: bool = False,
                     compressed_json: bool = False) -> None:
    print("Starting stream function")
    file_record_count = 0
    fin = None
    if format_csv:
        fin = open(filename, 'r', encoding='UTF8')
        data_reader = csv.reader(fin)
    elif compressed_json:
        fin = gzip.open(filename, 'r')
        data_reader = json.loads(fin.read().decode('utf-8'))
    else:
        raise Exception('Unsupported input format')
    

        
    for data in data_reader:
        print("Data - ", data)
        future = producer.send(kafka_topic, data)
        result = future.get(timeout=60)
        file_record_count += 1
    #     pass
    # pass
    print('Records count: ' + str(file_record_count) + ' streamed from: ' + filename)
    producer.flush()

    global total_records_count
    total_records_count += file_record_count
    
    if fin: fin.close()
    
    pass

############################################
# Stream data from csv
############################################
csv_filename = data_dir + 'smallerDataset.csv'
print("File to be parsed  - " + csv_filename)
stream_from_file(csv_filename, format_csv=True)

############################################
# Stream data from compressed json
############################################
# filenames = [f for f in glob.glob(data_dir + '/*.gz')]
# for filename in filenames:
#     stream_from_file(filename, compressed_json=True)
#     pass

print('Total Records Streamed: ' + str(total_records_count))
