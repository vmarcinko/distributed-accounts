package me.distributedaccounts.avrolog.schemarepo;

import me.distributedaccounts.avrolog.SchemaIdUtils;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.specific.SpecificData;
import org.springframework.beans.factory.InitializingBean;

import java.nio.ByteBuffer;
import java.util.*;

public class InMemorySchemaRepositoryImpl implements SchemaRepository, InitializingBean {
    private Set<Class<? extends GenericContainer>> avroClasses = new HashSet<>();

    private final Map<ByteBuffer, Schema> schemas = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        for (Class<? extends GenericContainer> avroClass : avroClasses) {
            Schema schema = SpecificData.get().getSchema(avroClass);
            byte[] id = SchemaIdUtils.generateSchemaId(schema);
            schemas.put(ByteBuffer.wrap(id), schema);
        }
    }

    @Override
    public Schema getSchema(byte[] id) {
        if (id == null) {
            throw new IllegalArgumentException("id is null");
        }
        if (id.length != 16) {
            throw new IllegalArgumentException("ID must be of length 16, but is: " + id);
        }

        ByteBuffer wrappedId = ByteBuffer.wrap(id);
        if (!schemas.containsKey(wrappedId)) {
            throw new IllegalArgumentException("There is no schema registered for ID: " + Arrays.toString(id));
        }
        return schemas.get(wrappedId);
    }

    public void setAvroClasses(Set<Class<? extends GenericContainer>> avroClasses) {
        this.avroClasses = avroClasses;
    }
}
