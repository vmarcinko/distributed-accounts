package me.distributedaccounts.mgmt.service.cluster;

import org.apache.helix.HelixManager;
import org.apache.helix.HelixManagerFactory;
import org.apache.helix.InstanceType;
import org.apache.helix.api.id.StateModelDefId;
import org.apache.helix.participant.StateMachineEngine;
import org.apache.helix.participant.statemachine.HelixStateModelFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class HelixServiceClusterManager implements InitializingBean, DisposableBean {
    private String clusterName;
    private String instanceName;
    private String zkAddress;
    private HelixStateModelFactory helixStateModelFactory;

    private HelixManager helixManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        helixManager = HelixManagerFactory.getZKHelixManager(clusterName, instanceName, InstanceType.PARTICIPANT, zkAddress);
        StateMachineEngine stateMachineEngine = helixManager.getStateMachineEngine();

        stateMachineEngine.registerStateModelFactory(StateModelDefId.from("MasterSlave"), helixStateModelFactory);

//        helixManager.getMessagingService().registerMessageHandlerFactory(Message.MessageType.STATE_TRANSITION.toString(), stateMachineEngine);
//        helixManager.getMessagingService().registerMessageHandlerFactory(Message.MessageType.USER_DEFINE_MSG.toString(), new CustomMessageHandlerFactory());

        helixManager.connect();
    }

    @Override
    public void destroy() throws Exception {
        if (helixManager != null) {
            helixManager.disconnect();
        }
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public void setHelixStateModelFactory(HelixStateModelFactory helixStateModelFactory) {
        this.helixStateModelFactory = helixStateModelFactory;
    }
}
