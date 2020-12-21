package com.github.themeetgroup.kafka.connect.rabbitmq.sink.format;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static com.github.themeetgroup.kafka.connect.rabbitmq.sink.format.TestData.createSinkRecord;
import static com.github.themeetgroup.kafka.connect.rabbitmq.sink.format.TestData.paymentValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BytesRecordFormatterTest {

  private final RecordFormatter bytesRecordFormatter = new BytesRecordFormatter();

  @Test
  void givenABytesSchemaAndValue_expectCorrectFormat() {
    byte[] testString = "test".getBytes(StandardCharsets.UTF_8);
    SinkRecord record = createSinkRecord(Schema.BYTES_SCHEMA, testString);

    byte[] output = bytesRecordFormatter.format(record);

    assertEquals(testString, output);
  }

  // When using the BytesFormatter, the "org.apache.kafka.connect.converters.ByteArrayConverter" must be used as value converter
  // This is also the default behaviour when not specifying a formatter
  @Test
  void givenAStruct_whenFormattingWithBytesRecordFormatter_expectDataException() {
    Struct payment = paymentValue(1, true, "testSender");
    SinkRecord record = createSinkRecord(TestData.paymentSchema(), payment);

    assertThrows(DataException.class, () -> bytesRecordFormatter.format(record));
  }
}