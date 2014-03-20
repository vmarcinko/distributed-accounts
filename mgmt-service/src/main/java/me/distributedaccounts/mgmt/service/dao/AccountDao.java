package me.distributedaccounts.mgmt.service.dao;

import me.distributedaccounts.mgmt.service.Account;
import me.distributedaccounts.mgmt.service.event.AccountCreatedEffect;
import me.distributedaccounts.mgmt.service.event.AccountDeletedEffect;
import me.distributedaccounts.mgmt.service.event.AccountUpdatedEffect;

import java.util.UUID;

public interface AccountDao {
    Account find(UUID id);

    void applyStateEffect(AccountCreatedEffect effect);

    void applyStateEffect(AccountUpdatedEffect effect);

    void applyStateEffect(AccountDeletedEffect effect);
}
