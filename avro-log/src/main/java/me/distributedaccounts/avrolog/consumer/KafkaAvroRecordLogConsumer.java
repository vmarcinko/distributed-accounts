package me.distributedaccounts.avrolog.consumer;

public interface KafkaAvroRecordLogConsumer {
    void startConsumption(int partition);

    void stopConsumption(int partition);
}
