package me.distributedaccounts.search.service;

import java.util.List;
import java.util.Map;

public interface AccountSearchService {
    List<Map<String, Object>> findByDescription(String description);
}
