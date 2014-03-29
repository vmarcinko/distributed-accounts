package me.distributedaccounts.mgmt.client;

import me.distributedaccounts.commons.cluster.client.ClusterClient;
import me.distributedaccounts.commons.cluster.client.NodeInfo;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AccountManagementClientImpl implements AccountManagementClient {
    private RestTemplate restTemplate;
    private ClusterClient clusterClient;
    private String clusterResourceName;

    @Override
    public Map<String, Object> find(UUID accountId) {
        String url = constructRequestUrl("/accounts/{accountId}", accountId, MasterSlaveType.SLAVE);
        return restTemplate.getForObject(url, Map.class, accountId.toString());
    }

    @Override
    public List<Map<String, Object>> findAll() {
        String url = constructRequestUrl("/accounts", "0", MasterSlaveType.SLAVE);
        return restTemplate.getForObject(url, List.class);
    }

    @Override
    public UUID openAccount(String description) {
        UUID accountId = UUID.randomUUID();

        String url = constructRequestUrl("/accounts/open", accountId, MasterSlaveType.MASTER);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("id", accountId.toString());
        requestBody.put("description", description);
        restTemplate.postForObject(url, requestBody, Object.class);
        return accountId;
    }

    @Override
    public void closeAccount(UUID id) {
        String url = constructRequestUrl("/accounts/{accountId}/close", id, MasterSlaveType.MASTER);
        restTemplate.postForObject(url, new HashMap<String, Object>(), Object.class, id);
    }

    @Override
    public void withdrawMoney(UUID id, float amount) throws InsufficientMoneyException {
        String url = constructRequestUrl("/accounts/{accountId}/withdraw", id, MasterSlaveType.MASTER);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount);
        restTemplate.postForObject(url, requestBody, Object.class, id);
    }

    @Override
    public void depositMoney(UUID id, float amount) {
        String url = constructRequestUrl("/accounts/{accountId}/deposit", id, MasterSlaveType.MASTER);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount);
        restTemplate.postForObject(url, requestBody, Object.class, id);
    }

    private String constructRequestUrl(String path, Object partitionKey, MasterSlaveType masterSlaveType) {
        int partitionIndex = resolvePartitionIndex(partitionKey);

        boolean required = masterSlaveType.equals(MasterSlaveType.MASTER);
        NodeInfo nodeInfo = clusterClient.resolveNode(clusterResourceName, partitionIndex, masterSlaveType.name(), required);

        if (nodeInfo == null && !required) {
            nodeInfo = clusterClient.resolveNode(clusterResourceName, partitionIndex, MasterSlaveType.MASTER.name(), true);
        }

        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(nodeInfo.getHost())
                .port(nodeInfo.getPort())
                .path(path)
                .build();
        return uriComponents.toString();
    }

    private int resolvePartitionIndex(Object partitionKey) {
        Integer numberOfPartitions = clusterClient.resolveNumberOfPartitions(clusterResourceName);
        if (numberOfPartitions == null) {
            throw new IllegalStateException("Cannot find number of partitions in Helix/Zookeeper configuration for resource: " + clusterResourceName);
        }
        return partitionKey.hashCode() % numberOfPartitions;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setClusterClient(ClusterClient clusterClient) {
        this.clusterClient = clusterClient;
    }

    public void setClusterResourceName(String clusterResourceName) {
        this.clusterResourceName = clusterResourceName;
    }
}
