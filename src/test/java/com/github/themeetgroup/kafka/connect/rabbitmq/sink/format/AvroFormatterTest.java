package com.github.themeetgroup.kafka.connect.rabbitmq.sink.format;

import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.util.Utf8;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import static com.github.themeetgroup.kafka.connect.rabbitmq.sink.format.TestData.createSinkRecord;
import static com.github.themeetgroup.kafka.connect.rabbitmq.sink.format.TestData.paymentSchema;
import static com.github.themeetgroup.kafka.connect.rabbitmq.sink.format.TestData.paymentValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AvroFormatterTest {

  private final RecordFormatter avroRecordFormatter = new AvroFormatter();
  private final DecoderFactory decoderFactory = DecoderFactory.get();
  private final KafkaAvroDeserializer kafkaAvroDeserializer = new KafkaAvroDeserializer(new MockSchemaRegistryClient());

  private Schema schema;

  @BeforeEach
  void setUp() throws IOException, URISyntaxException {
    URL resource = this.getClass().getClassLoader().getResource("payment.avsc");
    schema = new Schema.Parser().parse(new File(resource.toURI()));
  }

  @Test
  void givenAStruct_whenFormattingWithAvroRecordFormatter_expectStructToJson() throws IOException {
    Struct payment = paymentValue(1, true, "testSender");
    SinkRecord sinkRecord = createSinkRecord(paymentSchema(), payment);

    byte[] output = avroRecordFormatter.format(sinkRecord);

    GenericRecord record = toGenericRecord(schema, output);
    assertEquals(1, record.get("id"));
    assertEquals(true, record.get("isCashPayment"));
    assertEquals(new Utf8("testSender"), record.get("sender"));
  }

  // The "avro" formatter is serializing data in NON-confluent avro, meaning the first bytes do not contain the schema id
  // see: https://docs.confluent.io/platform/current/schema-registry/serdes-develop/index.html#wire-format
  // If you want confluent avro bytes using the RabbitMQ sink connector
  // you can use the "org.apache.kafka.connect.converters.ByteArrayConverter" converter
  // after putting confluent avro serialized data on your topic
  @Test
  void validateExceptionIsThrown_whenTryingToDeserializeOutputWithKafkaAvroDeserializer() {
    Struct payment = paymentValue(1, true, "testSender");
    SinkRecord sinkRecord = createSinkRecord(paymentSchema(), payment);

    byte[] output = avroRecordFormatter.format(sinkRecord);

    assertThrows(SerializationException.class, () -> kafkaAvroDeserializer.deserialize("test", output));
  }

  private GenericRecord toGenericRecord(Schema schema, byte[] avroBytes) throws IOException {
    DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
    ByteArrayInputStream stream = new ByteArrayInputStream(avroBytes);
    BinaryDecoder binaryDecoder = decoderFactory.binaryDecoder(stream, null);
    return reader.read(null, binaryDecoder);
  }
}