package me.distributedaccounts.mgmt.service.event.processor;

import me.distributedaccounts.avrolog.consumer.AvroRecordConsumer;
import org.apache.avro.generic.GenericRecord;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AccountEventAvroRecordConsumer implements AvroRecordConsumer {
    private AccountEventProcessor accountEventProcessor;

    private Set<Integer> masterPartitionIds = Collections.<Integer>synchronizedSet(new HashSet<Integer>());

    public void registerMasterPartitionId(int partitionId) {
        masterPartitionIds.add(partitionId);
    }

    public void unregisterMasterPartitionId(int partitionId) {
        masterPartitionIds.remove(partitionId);
    }

    @Override
    public void consumeRecord(GenericRecord avroRecord, String topic, int partition, long offset) throws Exception {
        // process record only if it's not coming from master partition for this node ...
        if (!masterPartitionIds.contains(partition)) {
            accountEventProcessor.processEvent(avroRecord);
        }
    }

    public void setAccountEventProcessor(AccountEventProcessor accountEventProcessor) {
        this.accountEventProcessor = accountEventProcessor;
    }
}
