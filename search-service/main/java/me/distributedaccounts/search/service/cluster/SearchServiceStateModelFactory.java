package me.distributedaccounts.search.service.cluster;

import me.distributedaccounts.avrolog.consumer.KafkaAvroRecordLogConsumer;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.participant.statemachine.HelixStateModelFactory;

public class SearchServiceStateModelFactory extends HelixStateModelFactory<SearchServiceStateModel> {
    private KafkaAvroRecordLogConsumer kafkaAvroRecordLogConsumer;

    @Override
    public SearchServiceStateModel createNewStateModel(PartitionId partitionId) {
        return new SearchServiceStateModel(partitionId, kafkaAvroRecordLogConsumer);
    }

    public void setKafkaAvroRecordLogConsumer(KafkaAvroRecordLogConsumer kafkaAvroRecordLogConsumer) {
        this.kafkaAvroRecordLogConsumer = kafkaAvroRecordLogConsumer;
    }
}
