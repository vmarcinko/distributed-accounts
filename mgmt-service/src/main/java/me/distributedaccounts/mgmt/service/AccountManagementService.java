package me.distributedaccounts.mgmt.service;

import java.util.UUID;

public interface AccountManagementService {
    void open(UUID id, String description);

    void close(UUID id);

    void withdrawMoney(UUID id, float amount) throws InsufficientFundsException;

    void depositMoney(UUID id, float amount);

    Account find(UUID id);
}
