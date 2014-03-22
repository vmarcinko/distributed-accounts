package me.distributedaccounts.search.client;

import me.distributedaccounts.commons.cluster.client.ClusterClient;
import me.distributedaccounts.commons.cluster.client.NodeInfo;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

public class AccountSearchClientImpl implements AccountSearchClient {
    private RestTemplate restTemplate;
    private ClusterClient clusterClient;
    private String clusterResourceName;

    @Override
    public List<Map<String, Object>> findByDescription(String description) {
        String url = constructRequestUrl("/accounts/search/{description}");
        return restTemplate.getForObject(url, List.class, description);
    }

    private String constructRequestUrl(String path) {
        NodeInfo nodeInfo = clusterClient.resolveNode(clusterResourceName, 0, "ONLINE");
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host(nodeInfo.getHost())
                .port(nodeInfo.getPort())
                .path(path)
                .build();
        return uriComponents.toString();
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
