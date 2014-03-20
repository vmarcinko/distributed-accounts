package me.distributedaccounts.mgmt.service.cluster;

import me.distributedaccounts.avrolog.consumer.KafkaAvroRecordLogConsumer;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.participant.statemachine.HelixStateModelFactory;

public class ManagementServiceStateModelFactory extends HelixStateModelFactory<ManagementServiceStateModel> {
    private KafkaAvroRecordLogConsumer kafkaAvroRecordLogConsumer;

    @Override
    public ManagementServiceStateModel createNewStateModel(PartitionId partitionId) {
        return new ManagementServiceStateModel(partitionId, kafkaAvroRecordLogConsumer);
    }

    public void setKafkaAvroRecordLogConsumer(KafkaAvroRecordLogConsumer kafkaAvroRecordLogConsumer) {
        this.kafkaAvroRecordLogConsumer = kafkaAvroRecordLogConsumer;
    }
}
