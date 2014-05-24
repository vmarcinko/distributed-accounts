package me.distributedaccounts.mgmt.service.dao;

import me.distributedaccounts.mgmt.service.Account;
import me.distributedaccounts.mgmt.service.event.AccountCreatedEffect;
import me.distributedaccounts.mgmt.service.event.AccountData;
import me.distributedaccounts.mgmt.service.event.AccountDeletedEffect;
import me.distributedaccounts.mgmt.service.event.AccountUpdatedEffect;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InMemoryAccountDaoImpl implements AccountDao {
    private final ConcurrentMap<UUID, Map<String, Object>> accounts = new ConcurrentHashMap<UUID, Map<String, Object>>();

    @Override
    public Account find(UUID id) {
        Map<String, Object> map = accounts.get(id);
        if (map == null) {
            return null;
        }
        return deserialize(map);
    }

    @Override
    public List<Account> findAll() {
        Collection<Map<String, Object>> accountsAsMap = accounts.values();
        List<Account> list = new ArrayList<Account>();
        for (Map<String, Object> map : accountsAsMap) {
            list.add(deserialize(map));
        }
        return list;
    }

    @Override
    public void applyStateEffect(AccountCreatedEffect effect) {
        AccountData accountData = effect.getAccountData();
        UUID id = UUID.fromString(accountData.getId().toString());
        accounts.put(id, serialize(accountData));
    }

    @Override
    public void applyStateEffect(AccountUpdatedEffect effect) {
        AccountData accountData = effect.getAccountData();
        UUID id = UUID.fromString(accountData.getId().toString());
        accounts.put(id, serialize(accountData));
    }

    @Override
    public void applyStateEffect(AccountDeletedEffect effect) {
        UUID id = UUID.fromString(effect.getAccountId().toString());
        accounts.remove(id);
    }

    private Account deserialize(Map<String, Object> map) {
        Account account = new Account();
        account.setId((UUID) map.get("id"));
        account.setBalance((Float) map.get("balance"));
        account.setDescription((String) map.get("description"));
        account.setActive((Boolean) map.get("active"));
        return account;
    }

    private Map<String, Object> serialize(AccountData account) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", UUID.fromString(account.getId().toString()));
        map.put("balance", account.getBalance());
        map.put("description", account.getDescription().toString());
        map.put("active", account.getActive());
        return map;
    }
}
