package me.distributedaccounts.mgmt.client.cluster;

public interface ClusterClient {
    NodeInfo resolveNode(String resourceName, int partitionIndex, String partitionState);

    Integer resolveNumberOfPartitions(String resourceName);
}
