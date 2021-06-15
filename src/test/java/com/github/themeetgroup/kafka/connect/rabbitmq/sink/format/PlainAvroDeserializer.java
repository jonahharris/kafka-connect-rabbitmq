package com.github.themeetgroup.kafka.connect.rabbitmq.sink.format;

import org.apache.avro.Schema;
import org.apache.avro.data.TimeConversions;
import org.apache.avro.generic.GenericData;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class PlainAvroDeserializer<T extends SpecificRecord> implements Deserializer<T> {

  static {
    SpecificData.get().addLogicalTypeConversion(new TimeConversions.TimestampMillisConversion());
    SpecificData.get().addLogicalTypeConversion(new TimeConversions.TimestampMicrosConversion());
    SpecificData.get().addLogicalTypeConversion(new TimeConversions.DateConversion());

    GenericData.get().addLogicalTypeConversion(new TimeConversions.TimestampMillisConversion());
    GenericData.get().addLogicalTypeConversion(new TimeConversions.TimestampMicrosConversion());
    GenericData.get().addLogicalTypeConversion(new TimeConversions.DateConversion());
  }

  private final DecoderFactory decoderFactory = DecoderFactory.get();
  private final DatumReader<T> datumReader;

  public PlainAvroDeserializer(Class<T> cls) {
    this(SpecificData.get().getSchema(cls));
  }

  public PlainAvroDeserializer(Schema schema) {
    datumReader = new SpecificDatumReader<>(schema);
  }

  @Override
  public T deserialize(String topic, byte[] data) {
    try {
      if (data == null) {
        return null;
      }

      try (ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
        BinaryDecoder binaryDecoder = decoderFactory.binaryDecoder(stream, null);
        return datumReader.read(null, binaryDecoder);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
