package me.distributedaccounts.avrolog.consumer;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import me.distributedaccounts.avrolog.schemarepo.SchemaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KafkaAvroRecordLogConsumerImpl implements KafkaAvroRecordLogConsumer, InitializingBean, DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(KafkaAvroRecordLogConsumerImpl.class);

    private ConsumerConnector consumerConnector;
    private ExecutorService executorService;

    private Properties kafkaConsumerConfigProperties;
    private SchemaRepository schemaRepository;
    private String topic;
    private AvroRecordConsumer avroRecordConsumer;

    private Map<String, Future> partitionConsumingThreadFutures = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        ConsumerConfig consumerConfig = new ConsumerConfig(kafkaConsumerConfigProperties);
        consumerConnector = Consumer.createJavaConsumerConnector(consumerConfig);
        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public void destroy() throws Exception {
        if (consumerConnector != null) {
            consumerConnector.shutdown();
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }

    @Override
    public void startConsumption(int partition) {
        logger.info("Starting Kafka log consumption: topic=" + topic + ", partition=" + partition + ", avroRecordConsumer=" + avroRecordConsumer);

        if (partitionConsumingThreadFutures.containsKey(constructPartitionPath(partition, topic))) {
            throw new IllegalStateException("COnsuming thread for topic '" + topic + "' and partition " + partition + " already exists");
        }

        Map<String, Integer> topicThreadCounts = Collections.singletonMap(topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumerConnector.createMessageStreams(topicThreadCounts);

        for (Map.Entry<String, List<KafkaStream<byte[], byte[]>>> entry : consumerMap.entrySet()) {
            String streamTopic = entry.getKey();
            List<KafkaStream<byte[], byte[]>> streams = entry.getValue();

            for (KafkaStream<byte[], byte[]> stream : streams) {
                Future<?> future = executorService.submit(new MessageConsumingTask(stream, streamTopic, schemaRepository, avroRecordConsumer));
                partitionConsumingThreadFutures.put(constructPartitionPath(partition, streamTopic), future);
            }
        }
    }

    @Override
    public void stopConsumption(int partition) {
        logger.info("Stopping Kafka log consumption: topic=" + topic + ", partition=" + partition);
        Future future = partitionConsumingThreadFutures.remove(constructPartitionPath(partition, topic));
        if (future != null) {
            future.cancel(true);
        }
    }

    private String constructPartitionPath(int partition, String topic) {
        return topic + "--" + partition;
    }

    public void setKafkaConsumerConfigProperties(Properties kafkaConsumerConfigProperties) {
        this.kafkaConsumerConfigProperties = kafkaConsumerConfigProperties;
    }

    public void setSchemaRepository(SchemaRepository schemaRepository) {
        this.schemaRepository = schemaRepository;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setAvroRecordConsumer(AvroRecordConsumer avroRecordConsumer) {
        this.avroRecordConsumer = avroRecordConsumer;
    }
}
