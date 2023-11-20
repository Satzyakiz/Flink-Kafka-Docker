#!/bin/bash
export COMPOSE_PROJECT_NAME=st-cep
docker compose --env-file config.env up -d --build 

source config.env
#######################
# create topic  
# Note: Kafka broker from host is accessible at broker:9092
# whereas on internal network it is accessible at broker:29092
#######################

# docker exec $KAFKA_BROKER_HOST \
#        kafka-topics --bootstrap-server $KAFKA_BROKER_HOST:9092 \
#        --delete \
#        --topic $KAFKA_SOURCE_TOPIC

# docker exec $KAFKA_BROKER_HOST \
#        kafka-topics --bootstrap-server $KAFKA_BROKER_HOST:9092 \
#        --delete \
#        --topic $KAFKA_SINK_TOPIC

docker exec $KAFKA_BROKER_HOST \
       kafka-topics --bootstrap-server $KAFKA_BROKER_HOST:9092 \
       --create \
       --topic $KAFKA_SOURCE_TOPIC

docker exec $KAFKA_BROKER_HOST \
       kafka-topics --bootstrap-server $KAFKA_BROKER_HOST:9092 \
       --create \
       --topic $KAFKA_SINK_TOPIC

# Internal command
#kafka-topics --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic $KAFKA_TOPIC

# # stop kafka broker
# docker-compose down
# docker stop $(docker ps -q) && docker rm $(docker ps -aq)
# docker rm $(docker ps -aq)