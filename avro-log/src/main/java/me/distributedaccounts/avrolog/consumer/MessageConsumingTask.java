package me.distributedaccounts.avrolog.consumer;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import me.distributedaccounts.avrolog.schemarepo.SchemaRepository;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class MessageConsumingTask implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(MessageConsumingTask.class);

    private final KafkaStream stream;
    private final SchemaRepository schemaRepository;
    private final String topic;
    private final AvroRecordConsumer avroRecordConsumer;

    public MessageConsumingTask(KafkaStream<byte[], byte[]> stream, String topic, SchemaRepository schemaRepository, AvroRecordConsumer avroRecordConsumer) {
        this.stream = stream;
        this.topic = topic;
        this.schemaRepository = Objects.requireNonNull(schemaRepository, "schemaRepository is null");
        this.avroRecordConsumer = Objects.requireNonNull(avroRecordConsumer, "avroRecordConsumer is null");
    }

    @Override
    public void run() {
        try {
            logger.info("Starting Kafka consuming thread on topic '" + topic + "' ...");

            ConsumerIterator<byte[], byte[]> consumerIterator = stream.iterator();
            while (consumerIterator.hasNext()) {
                MessageAndMetadata<byte[], byte[]> messageAndMetadata = consumerIterator.next();
                logger.info("Kafka message consumed at offset=" + messageAndMetadata.offset() + ", partition=" + messageAndMetadata.partition() + ", topic=" + messageAndMetadata.topic());
                byte[] messageBytes = messageAndMetadata.message();

                byte[] schemaId = Arrays.copyOf(messageBytes, 16); // MD5 Avro fingerprint is 16 bytes long
                byte[] avroRecordBytes = Arrays.copyOfRange(messageBytes, 16, messageBytes.length);

                Schema writerSchema = schemaRepository.getSchema(schemaId);
                Schema readerSchema = writerSchema;

                GenericRecord genericRecord = deserializeAvroRecord(avroRecordBytes, writerSchema, readerSchema);
                avroRecordConsumer.consumeRecord(genericRecord, messageAndMetadata.topic(), messageAndMetadata.partition(), messageAndMetadata.offset());
            }
            logger.info("Shutting down Kafka consuming thread on topic '" + topic + "'");

        } catch (Exception e) {
            if (!Thread.currentThread().isInterrupted()) {
                logger.error("Error while consuming Kafka message on topic '" + topic + "': " + e.getMessage(), e);
            }
        }
    }

    private GenericRecord deserializeAvroRecord(byte[] avroRecordBytes, Schema writerSchema, Schema readerSchema) throws IOException {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>(writerSchema, readerSchema);
        Decoder decoder = DecoderFactory.get().binaryDecoder(avroRecordBytes, null);
        return datumReader.read(null, decoder);
    }
}
