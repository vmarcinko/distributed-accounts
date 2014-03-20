package me.distributedaccounts.mgmt.service.cluster;

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

@StateModelInfo(initialState = "OFFLINE", states = {"OFFLINE", "SLAVE", "MASTER"})
public class ManagementServiceStateModel extends StateModel {
    private final Logger logger = LoggerFactory.getLogger(ManagementServiceStateModel.class);

    private final PartitionId partitionId;
    private final KafkaAvroRecordLogConsumer kafkaAvroRecordLogConsumer;

    public ManagementServiceStateModel(PartitionId partitionId, KafkaAvroRecordLogConsumer kafkaAvroRecordLogConsumer) {
        this.partitionId = Objects.requireNonNull(partitionId, "partitionId is null");
        this.kafkaAvroRecordLogConsumer = Objects.requireNonNull(kafkaAvroRecordLogConsumer, "kafkaAvroRecordLogConsumer is null");
    }

    @Transition(from = "OFFLINE", to = "SLAVE")
    public void offlineToSlave(Message message, NotificationContext context) {
        logger.info("OFFLINE -> SLAVE on partition: " + partitionId);
        kafkaAvroRecordLogConsumer.startConsumption(extractPartition());

/*
        HelixManager manager = context.getManager();
        ClusterMessagingService messagingService = manager.getMessagingService();
        Message requestBackupUriRequest = new Message(Message.MessageType.USER_DEFINE_MSG, MessageId.from(UUID.randomUUID().toString()));
        requestBackupUriRequest.setMsgSubType(ServiceStarter.REQUEST_BOOTSTRAP_URL);
        requestBackupUriRequest.setMsgState(Message.MessageState.NEW);

        Criteria recipientCriteria = new Criteria();
        recipientCriteria.setInstanceName("*");
        recipientCriteria.setRecipientInstanceType(InstanceType.PARTICIPANT);
        recipientCriteria.setResource(message.getResourceId().stringify());
        recipientCriteria.setPartition(message.getPartitionId().stringify());
        recipientCriteria.setSessionSpecific(true);

        // wait for 30 seconds
        int timeout = 30000;
        BootstrapReplyHandler responseHandler = new BootstrapReplyHandler();

        int sentMessageCount = messagingService.sendAndWait(recipientCriteria, requestBackupUriRequest, responseHandler, timeout);
        if (sentMessageCount == 0) {
            // could not find any other node hosting the partition

        } else if (responseHandler.getBootstrapUrl() != null) {
            System.out.println("Got bootstrap url:" + responseHandler.getBootstrapUrl());
            System.out.println("Got backup time:" + responseHandler.getBootstrapTime());
            // Got the url fetch it
        } else {
            // Either go to error state
            // throw new Exception("Cant find backup/bootstrap data");
            // Request some node to start backup process
        }
*/
    }

    @Transition(from = "SLAVE", to = "MASTER")
    public void slaveToMaster(Message message, NotificationContext context) {
        logger.info("SLAVE -> MASTER on partition: " + partitionId);

        kafkaAvroRecordLogConsumer.stopConsumption(extractPartition());

        // nastavi citati na kafka consumeru poruke u ovom partitionu sve dok više nema poruka (!?)
        // isključi kafka consumera
        // aktiviraj obradu service requesta za ovaj partition
    }

    @Transition(from = "MASTER", to = "SLAVE")
    public void masterToSlave(Message message, NotificationContext context) {
        logger.info("MASTER -> SLAVE on partition: ");

        kafkaAvroRecordLogConsumer.startConsumption(extractPartition());

        // deaktiviraj obradu service requesta za ovaj partition
        // uključi kafka consumera
    }

    @Transition(from = "SLAVE", to = "OFFLINE")
    public void slaveToOffline(Message message, NotificationContext context) {
        logger.info("SLAVE -> OFFLINE on partition: " + partitionId);

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