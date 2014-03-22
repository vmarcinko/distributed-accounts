package me.distributedaccounts.search.client;

import java.util.List;
import java.util.Map;

public interface AccountSearchClient {
    List<Map<String, Object>> findByDescription(String description);
}
