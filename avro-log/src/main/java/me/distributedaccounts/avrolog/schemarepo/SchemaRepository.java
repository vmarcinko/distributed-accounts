package me.distributedaccounts.avrolog.schemarepo;

import org.apache.avro.Schema;

public interface SchemaRepository {
    Schema getSchema(byte[] id);
}
