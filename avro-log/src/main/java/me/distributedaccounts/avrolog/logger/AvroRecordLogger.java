package me.distributedaccounts.avrolog.logger;

import org.apache.avro.generic.GenericContainer;

public interface AvroRecordLogger {
    void logRecord(String topic, int partition, Object recordKey, GenericContainer avroRecord);
}
