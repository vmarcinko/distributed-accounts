package me.distributedaccounts.search.service.cluster;

import me.distributedaccounts.avrolog.consumer.KafkaAvroRecordLogConsumer;
import org.apache.helix.NotificationContext;
import org.apache.helix.api.id.PartitionId;
import org.apache.helix.model.Message;
import org.apache.helix.participant.statemachine.StateModel;
import org.apache.helix.participant.statemachine.StateModelInfo;
import org.apache.helix.participant.statemachine.Transition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

@StateModelInfo(initialState = "OFFLINE", states = {"OFFLINE", "ONLINE"})
public class SearchServiceStateModel extends StateModel {
    private final Logger logger = LoggerFactory.getLogger(SearchServiceStateModel.class);

    private final PartitionId partitionId;
    private final KafkaAvroRecordLogConsumer kafkaAvroRecordLogConsumer;

    public SearchServiceStateModel(PartitionId partitionId, KafkaAvroRecordLogConsumer kafkaAvroRecordLogConsumer) {
        this.partitionId = Objects.requireNonNull(partitionId, "partitionId is null");
        this.kafkaAvroRecordLogConsumer = Objects.requireNonNull(kafkaAvroRecordLogConsumer, "kafkaAvroRecordLogConsumer is null");
    }

    @Transition(from = "OFFLINE", to = "ONLINE")
    public void offlineToSlave(Message message, NotificationContext context) {
        logger.info("OFFLINE -> ONLINE on partition: " + partitionId);
        kafkaAvroRecordLogConsumer.startConsumption(extractPartition());
    }

    @Transition(from = "ONLINE", to = "OFFLINE")
    public void slaveToOffline(Message message, NotificationContext context) {
        logger.info("ONLINE -> OFFLINE on partition: " + partitionId);
        kafkaAvroRecordLogConsumer.stopConsumption(extractPartition());
    }

    @Transition(from = "OFFLINE", to = "DROPPED")
    public void offlineToDropped(Message message, NotificationContext context) {
        logger.info("OFFLINE -> DROPPED on partition: " + partitionId);
    }

    private int extractPartition() {
        String partitionIdString = partitionId.stringify();
        return Integer.parseInt(partitionIdString.substring(partitionIdString.indexOf("_") + 1));
    }
}