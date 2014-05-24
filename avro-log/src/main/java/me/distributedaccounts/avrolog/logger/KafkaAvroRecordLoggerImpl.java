package me.distributedaccounts.avrolog.logger;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import me.distributedaccounts.avrolog.SchemaIdUtils;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

public class KafkaAvroRecordLoggerImpl implements AvroRecordLogger, InitializingBean, DisposableBean {
    private final Logger logger = LoggerFactory.getLogger(KafkaAvroRecordLoggerImpl.class);

    private Properties kafkaProducerConfigProperties;
    private Producer<byte[], byte[]> producer;

    @Override
    public void afterPropertiesSet() throws Exception {
        ProducerConfig producerConfig = new ProducerConfig(kafkaProducerConfigProperties);
        producer = new Producer<byte[], byte[]>(producerConfig);
    }

    @Override
    public void destroy() throws Exception {
        if (producer != null) {
            producer.close();
        }
    }

    @Override
    public void logRecord(String topic, int partition, String recordKey, GenericContainer avroRecord) {
        logger.info("Logging Avro record '" + avroRecord.getSchema().getFullName() + "' to topic/partition " + topic + "/" + partition + " under key '" + recordKey + "': " + avroRecord);
        try {
            byte[] messageBytes = encodeAvroRecordWithSchemaId(avroRecord);
            producer.send(new KeyedMessage(topic, recordKey.getBytes("UTF8"), messageBytes));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] encodeAvroRecordWithSchemaId(GenericContainer avroRecord) throws IOException {
        byte[] id = SchemaIdUtils.generateSchemaId(avroRecord.getSchema());
        byte[] avroRecordBytes = serializeAvroRecord(avroRecord);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(id);
        baos.write(avroRecordBytes);

        return baos.toByteArray();
    }

    private byte[] serializeAvroRecord(GenericContainer avroRecord) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Encoder encoder = EncoderFactory.get().directBinaryEncoder(outputStream, null);
        SpecificDatumWriter<GenericContainer> writer = new SpecificDatumWriter<GenericContainer>(avroRecord.getSchema());
        writer.write(avroRecord, encoder);
        encoder.flush();
        outputStream.close();
        return outputStream.toByteArray();
    }

    public void setKafkaProducerConfigProperties(Properties kafkaProducerConfigProperties) {
        this.kafkaProducerConfigProperties = kafkaProducerConfigProperties;
    }
}
