package me.distributedaccounts.avrolog;

import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;

import java.security.NoSuchAlgorithmException;

public class SchemaIdUtils {
    private SchemaIdUtils() {
    }

    public static byte[] generateSchemaId(Schema schema) {
        try {
            return SchemaNormalization.parsingFingerprint("MD5", schema);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hile generating Avro schema fingerprint: " + e.getMessage(), e);
        }
    }
}
