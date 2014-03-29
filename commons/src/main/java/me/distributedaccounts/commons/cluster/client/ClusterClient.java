package me.distributedaccounts.commons.cluster.client;

public interface ClusterClient {
    NodeInfo resolveNode(String resourceName, int partitionIndex, String partitionState, boolean required);

    Integer resolveNumberOfPartitions(String resourceName);
}
