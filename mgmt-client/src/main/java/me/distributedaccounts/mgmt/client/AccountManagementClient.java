package me.distributedaccounts.mgmt.client;

import java.util.Map;
import java.util.UUID;

public interface AccountManagementClient {
    Map<String, Object> find(UUID accountId);

    UUID openAccount(String description);

    void closeAccount(UUID id);

    void withdrawMoney(UUID id, float amount) throws InsufficientMoneyException;

    void depositMoney(UUID id, float amount);
}
