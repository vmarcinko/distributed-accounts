package me.distributedaccounts.search.service;

public interface AccountDescriptionIndex {
    void addAccountDescription(String accountId, String description);

    void removeAccountDescription(String accountId);
}
