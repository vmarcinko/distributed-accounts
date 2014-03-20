package me.distributedaccounts.mgmt.service.event.processor;

import org.apache.avro.generic.GenericRecord;

public interface AccountEventProcessor {
    void processEvent(GenericRecord event);
}
