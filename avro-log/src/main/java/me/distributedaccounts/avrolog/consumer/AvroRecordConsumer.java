package me.distributedaccounts.avrolog.consumer;

import org.apache.avro.generic.GenericRecord;

public interface AvroRecordConsumer {
    void consumeRecord(GenericRecord avroRecord, String topic, int partition, long offset) throws Exception;
}
