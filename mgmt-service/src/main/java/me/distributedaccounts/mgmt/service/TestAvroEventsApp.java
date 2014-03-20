package me.distributedaccounts.mgmt.service;

import me.distributedaccounts.avrolog.SchemaIdUtils;
import me.distributedaccounts.mgmt.service.event.*;
import me.distributedaccounts.avrolog.schemarepo.InMemorySchemaRepositoryImpl;
import me.distributedaccounts.avrolog.schemarepo.SchemaRepository;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.*;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;

public class TestAvroEventsApp {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        GenericRecord event = constructEvent();

        byte[] messageBytes = encodeAvroRecord(event);
        GenericRecord genericRecord = decodeAvroRecord(messageBytes);

        System.out.println("genericRecord = " + genericRecord);
        processEvent(genericRecord);
    }

    private static void processEvent(GenericRecord event) {
        GenericRecord stateEffect = extractStateEffectFromEvent(event);
        String stateEffectRecordFullName = stateEffect.getSchema().getFullName();

        switch (stateEffectRecordFullName) {
            case "me.distributedaccounts.mgmt.service.event.AccountCreatedEffect":
                AccountCreatedEffect accountCreatedEffect = convertToSpecificRecord(AccountCreatedEffect.class, stateEffect);
                System.out.println("accountCreatedEffect = " + accountCreatedEffect);
                break;

            case "me.distributedaccounts.mgmt.service.event.AccountUpdatedEffect":
                AccountUpdatedEffect accountUpdatedEffect = convertToSpecificRecord(AccountUpdatedEffect.class, stateEffect);
                System.out.println("accountUpdatedEffect = " + accountUpdatedEffect);
                break;

            case "me.distributedaccounts.mgmt.service.event.AccountDeletedEffect":
                AccountDeletedEffect accountDeletedEffect = convertToSpecificRecord(AccountDeletedEffect.class, stateEffect);
                System.out.println("accountDeletedEffect = " + accountDeletedEffect);
                break;

            default:
                throw new IllegalArgumentException("Unknown stateEffect record full name: " + stateEffectRecordFullName + " in event: " + event);
        }
    }

    private static <T extends SpecificRecord> T convertToSpecificRecord(Class<T> specificRecordClass, GenericRecord genericRecord) {
        Schema schema = SpecificData.get().getSchema(specificRecordClass);
        return (T) SpecificData.get().deepCopy(schema, genericRecord);
    }

    private static GenericRecord extractStateEffectFromEvent(GenericRecord event) {
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


    private static byte[] encodeAvroRecord(GenericContainer avroRecord) throws IOException {
        Schema schema = avroRecord.getSchema();
        byte[] id = SchemaIdUtils.generateSchemaId(schema);
        byte[] avroRecordBytes = serializeAvroRecord(avroRecord);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(id);
        baos.write(avroRecordBytes);

        return baos.toByteArray();
    }

    private static GenericRecord decodeAvroRecord(byte[] messageBytes) throws IOException {
        SchemaRepository schemaRepository = constructSchemaRepository();

        byte[] schemaId = Arrays.copyOf(messageBytes, 16); // MD5 Avro fingerprint is 16 bytes long
        byte[] avroRecordBytes = Arrays.copyOfRange(messageBytes, 16, messageBytes.length);

        Schema schema = schemaRepository.getSchema(schemaId);
        return deserializeAvroRecord(schema, avroRecordBytes);
    }

    private static SchemaRepository constructSchemaRepository() {
        InMemorySchemaRepositoryImpl repository = new InMemorySchemaRepositoryImpl();

        HashSet<Class<? extends GenericContainer>> avroClasses = new HashSet<>();
        avroClasses.add(AccountOpenedEvent.class);
        repository.setAvroClasses(avroClasses);

        try {
            repository.afterPropertiesSet();
            return repository;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static GenericRecord deserializeAvroRecord(Schema schema, byte[] bytes) throws IOException {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(schema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        return datumReader.read(null, decoder);
    }

    private static byte[] serializeAvroRecord(GenericContainer genericContainer) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        SpecificDatumWriter<GenericContainer> writer = new SpecificDatumWriter<GenericContainer>(genericContainer.getSchema());
        writer.write(genericContainer, encoder);
        encoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    private static AccountOpenedEvent constructEvent() {
        EventData eventData = new EventData("nekiMsgId", System.currentTimeMillis());
        AccountCreatedEffect stateEffects = new AccountCreatedEffect(new AccountData("nekiAccId", 0.5f, true, "nekiDesc"));
        return new AccountOpenedEvent(eventData, stateEffects, "nekiId", "nekiDescription");
    }
}
