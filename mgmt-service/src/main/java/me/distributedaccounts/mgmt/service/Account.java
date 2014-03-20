package me.distributedaccounts.mgmt.service;

import me.distributedaccounts.mgmt.service.event.AccountData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Account {
    private UUID id;
    private float balance;
    private boolean active;
    private String description;

    public Account() {
    }

    public Account(UUID id, String description) {
        this.id = id;
        this.description = description;

        this.active = true;
        this.balance = 0f;
    }

    public AccountData toAccountData() {
        return new AccountData(id.toString(), balance, active, description);
    }

    public Map<String, Object> toJacksonMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id.toString());
        map.put("balance", balance);
        map.put("active", active);
        map.put("description", description);
        return map;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Account{");
        sb.append("id=").append(id);
        sb.append(", balance=").append(balance);
        sb.append(", active=").append(active);
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
