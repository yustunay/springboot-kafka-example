## Kafka Docker Compose Configuration

This repository provides a `docker-compose.yml` file to easily set up a Kafka environment using Docker.

### Prerequisites
- Docker installed on your machine.

### Getting Started
1. Clone this repository to your local machine.
2. Open a terminal and navigate to the cloned repository directory.

### Usage
Run the following command to start the Kafka environment:


### Configuration Details
The `docker-compose.yml` file includes the following services:

```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.3.2
    container_name: zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - 2181:2181
    networks:
      - kafka-network

  broker:
    image: confluentinc/cp-kafka:7.3.2
    container_name: broker
    ports:
      - "9092:9092"
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_INTERNAL:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092,PLAINTEXT_INTERNAL://broker:29092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
    ports:
      - 9092:9092
    networks:
      - kafka-network

networks:
  kafka-network:
    driver: bridge
```

Run Docker Compose with the following command
```shell
$ docker-compose up -d
```

Now let's use the **nc command to verify that both the servers are listening on the respective ports**:
```shell
$ nc -z localhost 2181
Connection to localhost port 2181 [tcp/*] succeeded!
$ nc -z localhost 9092
Connection to localhost port 9092 [tcp/*] succeeded!
```

### Create a topic
```shell
$ docker exec broker \
  kafka-topics --bootstrap-server broker:9092 \
  --create \
  --topic test-topic

Created topic test-topic.  
```

### Write messages to the topic
```shell
$ docker exec --interactive --tty broker \
  kafka-console-producer --bootstrap-server broker:9092 \
                       --topic test-topic
```
Type in some lines of text. Each line is a new message.
```
> this is my first kafka message
> hello world!
> this is my third kafka message
```
When you’ve finished, enter Ctrl-C to return to your command prompt.

### Read messages from the topic
```shell
$ docker exec --interactive --tty broker \
  kafka-console-consumer --bootstrap-server broker:9092 \
                       --topic test-topic \
                       --from-beginning
```

### Test The Api After Starting The Spring Boot service
```shell
$ curl -X GET http://localhost:9090/kafka/publish/new-message
```

### Stop the Kafka broker
```shell
$ docker compose down
```

### Kafka Features
Apache Kafka is a distributed streaming platform that provides the following key features:

1. **Pub-Sub Messaging**: Kafka enables the publish-subscribe messaging model, where producers publish messages to topics, and consumers subscribe to topics to receive the messages.

2. **Scalability**: Kafka is designed to handle large-scale data streams. It can scale horizontally across multiple machines and support high-throughput and low-latency message processing.

3. **Fault-tolerance**: Kafka maintains replicated copies of data across multiple brokers, ensuring fault-tolerance and high availability. If a broker fails, other brokers in the cluster can take over its responsibilities.

4. **Durability**: Kafka persists messages on disk, allowing data to be stored for a configurable retention period. This durability ensures that messages are not lost in case of system failures.

5. **Stream Processing**: Kafka integrates well with stream processing frameworks like Apache Spark, Apache Flink, and Apache Samza. This enables real-time data processing and analysis on streaming data.

6. **Reliability**: Kafka guarantees message delivery by providing configurable delivery semantics, such as at least once, at most once, or exactly once. This ensures that messages are not lost or duplicated during processing.

### Kafka Components

Apache Kafka consists of several key components that work together to provide a distributed streaming platform:

#### Topics
A **topic** is a category or feed name to which messages are published by producers. Topics are partitioned and replicated across multiple brokers in a Kafka cluster. Producers write messages to topics, and consumers read messages from topics.

#### Producers
**Producers** are applications or processes that publish messages to Kafka topics. They write data to Kafka brokers, which in turn distribute the data to the appropriate topic partitions. Producers can send messages to specific partitions or let Kafka assign partitions automatically based on a specified key.

#### Consumers
**Consumers** are applications or processes that read messages from Kafka topics. They subscribe to one or more topics and consume messages from the partitions assigned to them. Consumers can be part of a consumer group, where each consumer in the group reads from a different subset of partitions for parallel processing.

#### Brokers
**Brokers** are the Kafka servers that form a cluster. Brokers are responsible for storing and replicating the published messages in the topics they manage. They handle producer writes, consumer reads, and other administrative tasks. A Kafka cluster typically consists of multiple brokers for scalability and fault tolerance.

#### ZooKeeper
ZooKeeper is a centralized service used by Kafka for distributed coordination and maintaining cluster metadata. Kafka relies on ZooKeeper to manage the dynamic changes in the cluster, such as broker registration, topic creation, and consumer group coordination. ZooKeeper helps ensure the high availability and fault tolerance of Kafka.

#### Partitions
**Partitions** are the unit of parallelism in Kafka. Topics are divided into multiple partitions to allow for scalability and parallel processing of messages. Each partition is ordered and immutable, meaning messages within a partition are assigned a unique offset and retain the order in which they were produced.

#### Replication
Kafka provides **replication** of partitions across multiple brokers for fault tolerance and high availability. Each partition has one leader and multiple followers. The leader handles all read and write requests for the partition, while followers replicate the data for redundancy. If a leader fails, one of the followers is elected as the new leader.

#### Consumer Groups
**Consumer groups** are logical groups of consumers that jointly consume messages from one or more topics. Each consumer in a group processes a unique subset of partitions. Kafka ensures that each message in a partition is delivered to only one consumer within a consumer group, allowing for parallel processing of messages across multiple consumers.

#### Offsets
**Offsets** are unique identifiers assigned to each message within a partition. Offsets indicate the position of a message in the partition's log. Consumers keep track of the offset of the last consumed message, allowing them to read messages from where they left off. Offsets are stored in a special Kafka topic called the "offsets topic."

These components work together to create a reliable, scalable, and distributed streaming platform for handling real-time data processing and messaging requirements.

For more detailed information on Apache Kafka and its components, refer to the [official documentation](https://kafka.apache.org/documentation/).
