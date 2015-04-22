package org.talend.dataprep.api.dataset.json;

import java.io.IOException;

import org.talend.dataprep.api.dataset.ColumnMetadata;
import org.talend.dataprep.api.dataset.Quality;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

class ColumnMetadataJsonSerializer extends JsonSerializer<ColumnMetadata> {

    public ColumnMetadataJsonSerializer() {
    }

    @Override
    public void serialize(ColumnMetadata column, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        {
            // Column name
            generator.writeStringField("id", column.getId()); //$NON-NLS-1
            // Column quality
            Quality quality = column.getQuality();
            if (quality != null) {
                generator.writeFieldName("quality"); //$NON-NLS-1
                generator.writeStartObject();
                {
                    generator.writeNumberField("empty", quality.getEmpty()); //$NON-NLS-1
                    generator.writeNumberField("invalid", quality.getInvalid()); //$NON-NLS-1
                    generator.writeNumberField("valid", quality.getValid()); //$NON-NLS-1
                }
                generator.writeEndObject();
            }
            // Column type
            String typeName = column.getType() != null ? column.getType() : "N/A"; //$NON-NLS-1
            generator.writeStringField("type", typeName); //$NON-NLS-1
        }
        generator.writeEndObject();
    }
}
