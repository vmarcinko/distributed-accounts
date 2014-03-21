package me.distributedaccounts.search.service;

import me.distributedaccounts.avrolog.consumer.AvroRecordConsumer;
import me.distributedaccounts.mgmt.service.event.AccountCreatedEffect;
import me.distributedaccounts.mgmt.service.event.AccountData;
import me.distributedaccounts.mgmt.service.event.AccountDeletedEffect;
import me.distributedaccounts.mgmt.service.event.AccountUpdatedEffect;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountEventAvroRecordConsumer implements AvroRecordConsumer {
    private final Logger logger = LoggerFactory.getLogger(AccountEventAvroRecordConsumer.class);

    private AccountDescriptionIndex accountDescriptionIndex;

    @Override
    public void consumeRecord(GenericRecord event, String topic, int partition, long offset) throws Exception {
        logger.info("Processing event '" + event.getSchema().getFullName() + "': " + event);

        GenericRecord stateEffect = extractStateEffectFromEvent(event);
        String stateEffectRecordFullName = stateEffect.getSchema().getFullName();

        switch (stateEffectRecordFullName) {
            case "me.distributedaccounts.mgmt.service.event.AccountCreatedEffect":
                AccountCreatedEffect accountCreatedEffect = convertToSpecificRecord(AccountCreatedEffect.class, stateEffect);
                AccountData accountData = accountCreatedEffect.getAccountData();
                accountDescriptionIndex.addAccountDescription(accountData.getId().toString(), accountData.getDescription().toString());
                break;

            case "me.distributedaccounts.mgmt.service.event.AccountUpdatedEffect":
                AccountUpdatedEffect accountUpdatedEffect = convertToSpecificRecord(AccountUpdatedEffect.class, stateEffect);
                break;

            case "me.distributedaccounts.mgmt.service.event.AccountDeletedEffect":
                AccountDeletedEffect accountDeletedEffect = convertToSpecificRecord(AccountDeletedEffect.class, stateEffect);
                accountDescriptionIndex.removeAccountDescription(accountDeletedEffect.getAccountId().toString());
                break;

            default:
                throw new IllegalArgumentException("Unknown stateEffect record full name: " + stateEffectRecordFullName + " in event: " + event);
        }
    }

    private <T extends SpecificRecord> T convertToSpecificRecord(Class<T> specificRecordClass, GenericRecord genericRecord) {
        Schema schema = SpecificData.get().getSchema(specificRecordClass);
        return (T) SpecificData.get().deepCopy(schema, genericRecord);
    }

    private GenericRecord extractStateEffectFromEvent(GenericRecord event) {
        String avroFieldName = "stateEffects";
        Object stateEffectInstance = event.get(avroFieldName);
        if (stateEffectInstance == null) {
            throw new IllegalArgumentException("No '" + avroFieldName + "' field in event: " + event);
        }
        if (!(stateEffectInstance instanceof GenericRecord)) {
            throw new IllegalArgumentException("Field  '" + avroFieldName + "' is not instance of GenericRecord in event: " + event);
        }
        return (GenericRecord) stateEffectInstance;
    }

    public void setAccountDescriptionIndex(AccountDescriptionIndex accountDescriptionIndex) {
        this.accountDescriptionIndex = accountDescriptionIndex;
    }
}
