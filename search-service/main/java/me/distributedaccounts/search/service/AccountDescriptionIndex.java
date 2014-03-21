package me.distributedaccounts.search.service;

import java.util.List;
import java.util.Map;

public interface AccountDescriptionIndex {
    void addAccountDescription(String accountId, String description);
    void removeAccountDescription(String accountId);

    List<Map<String, Object>> findByDescription(String description);
}
