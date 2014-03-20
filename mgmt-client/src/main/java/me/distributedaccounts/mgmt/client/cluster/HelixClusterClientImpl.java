package me.distributedaccounts.mgmt.client.cluster;

import org.apache.helix.*;
import org.apache.helix.model.IdealState;
import org.apache.helix.model.InstanceConfig;
import org.apache.helix.spectator.RoutingTableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HelixClusterClientImpl implements ClusterClient, InitializingBean, DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(HelixClusterClientImpl.class);

    private String clusterName;
    private String zkAddress;

    private HelixManager helixManager;
    private RoutingTableProvider routingTableProvider;

    private final Map<String, Integer> resourceNumberOfPartitions = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        String instanceName = UUID.randomUUID().toString();
        helixManager = HelixManagerFactory.getZKHelixManager(clusterName, instanceName, InstanceType.SPECTATOR, zkAddress);
        helixManager.connect();

        helixManager.addIdealStateChangeListener(constructIdealStateChangeListener());

        routingTableProvider = new RoutingTableProvider();
        helixManager.addExternalViewChangeListener(routingTableProvider);

    }

    private IdealStateChangeListener constructIdealStateChangeListener() {
        return new IdealStateChangeListener() {
            @Override
            public void onIdealStateChange(List<IdealState> idealState, NotificationContext changeContext) {
                logger.info("Helix IdealState changed - Fetching number of partitions for every resource");

                for (IdealState state : idealState) {
                    String resourceName = state.getResourceName();
                    int numberOfPartitions = state.getNumPartitions();

                    logger.info("Resource '" + resourceName + "' has " + numberOfPartitions + " partitions");
                    resourceNumberOfPartitions.put(resourceName, numberOfPartitions);
                }
            }
        };
    }

    @Override
    public Integer resolveNumberOfPartitions(String resourceName) {
        return resourceNumberOfPartitions.get(resourceName);
    }

    @Override
    public void destroy() throws Exception {
        if (helixManager != null) {
            helixManager.disconnect();
        }
    }

    @Override
    public NodeInfo resolveNode(String resourceName, int partitionIndex, String partitionState) {
        String partitionName = resourceName + "_" + partitionIndex;
        List<InstanceConfig> instanceConfigs = routingTableProvider.getInstances(resourceName, partitionName, partitionState);

        if (instanceConfigs.isEmpty()) {
            return null;
        } else {
            InstanceConfig instanceConfig = instanceConfigs.get(0);

            String nodeName = instanceConfig.getInstanceName();
            String host = instanceConfig.getHostName();
            int port = Integer.parseInt(instanceConfig.getPort());
            NodeInfo nodeInfo = new NodeInfo(nodeName, host, port);
            logger.info("Resolved node " + nodeInfo + " for resourceName=" + resourceName + ", partitionIndex=" + partitionIndex + ", partitionState=" + partitionState);
            return nodeInfo;
        }
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }
}
