package me.distributedaccounts.mgmt.service;

import me.distributedaccounts.mgmt.service.dao.AccountDao;
import me.distributedaccounts.mgmt.service.event.*;
import me.distributedaccounts.mgmt.service.event.processor.AccountEventProcessor;
import me.distributedaccounts.avrolog.logger.AvroRecordLogger;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class AccountManagementServiceImpl implements AccountManagementService {
    private final Logger logger = LoggerFactory.getLogger(AccountManagementServiceImpl.class);

    private AccountDao accountDao;
    private AccountEventProcessor accountEventProcessor;
    private AvroRecordLogger avroRecordLogger;
    private String accountEventsTopicName;

    @Override
    public void open(UUID id, String description) {
        logger.info("Opening account: id=" + id + ", description=" + description);

        Account account = new Account(id, description);

        AccountOpenedEvent event = new AccountOpenedEvent(
                constructEventData(),
                new AccountCreatedEffect(account.toAccountData()),
                id.toString(), description);

        logAndProcessEvent(id, event);
    }

    @Override
    public void close(UUID id) {
        logger.info("Closing account: id=" + id);

        AccountClosedEvent event = new AccountClosedEvent(
                constructEventData(),
                new AccountDeletedEffect(id.toString()),
                id.toString());

        logAndProcessEvent(id, event);
    }

    @Override
    public void withdrawMoney(UUID id, float amount) throws InsufficientFundsException {
        logger.info("Withdrawing money: id=" + id + ", amount=" + amount);

        Account account = accountDao.find(id);

        if (account.getBalance() < amount) {
            throw new InsufficientFundsException();
        }
        float newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);

        MoneyWithdrawnEvent event = new MoneyWithdrawnEvent(
                constructEventData(),
                new AccountUpdatedEffect(account.toAccountData()),
                id.toString(), amount);

        logAndProcessEvent(id, event);
    }

    @Override
    public void depositMoney(UUID id, float amount) {
        logger.info("Depositing money: id=" + id + ", amount=" + amount);

        Account account = accountDao.find(id);

        float newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);

        MoneyDepositedEvent event = new MoneyDepositedEvent(
                constructEventData(),
                new AccountUpdatedEffect(account.toAccountData()),
                id.toString(), amount);

        logAndProcessEvent(id, event);
    }

    @Override
    public Account find(UUID id) {
        return accountDao.find(id);
    }

    @Override
    public List<Account> findAll() {
        return accountDao.findAll();
    }

    private EventData constructEventData() {
        String eventId = UUID.randomUUID().toString();
        long timestampMillis = System.currentTimeMillis();
        return new EventData(eventId, timestampMillis);
    }

    private void logAndProcessEvent(UUID accountId, GenericRecord event) {
        avroRecordLogger.logRecord(accountEventsTopicName, 0, accountId.toString(), event);

        try {
            accountEventProcessor.processEvent(event);
        } catch (Exception e) {
            // todo: ???
            e.printStackTrace();
        }
    }

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    public void setAccountEventProcessor(AccountEventProcessor accountEventProcessor) {
        this.accountEventProcessor = accountEventProcessor;
    }

    public void setAvroRecordLogger(AvroRecordLogger avroRecordLogger) {
        this.avroRecordLogger = avroRecordLogger;
    }

    public void setAccountEventsTopicName(String accountEventsTopicName) {
        this.accountEventsTopicName = accountEventsTopicName;
    }
}
