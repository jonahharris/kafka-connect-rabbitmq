package com.github.themeetgroup.kafka.connect.rabbitmq.sink.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.github.themeetgroup.kafka.connect.rabbitmq.sink.format.TestData.createSinkRecord;
import static com.github.themeetgroup.kafka.connect.rabbitmq.sink.format.TestData.paymentValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class JsonRecordFormatterTest {

  private final RecordFormatter jsonRecordFormatter = new JsonRecordFormatter();
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void givenAStringSchemaAndValue_whenFormattingWithJsonRecordFormatter_expectQuotedString() {
    String value = "test";
    SinkRecord sinkRecord = TestData.createSinkRecord(Schema.STRING_SCHEMA, value);

    byte[] output = jsonRecordFormatter.format(sinkRecord);

    assertEquals("\"test\"", new String(output, StandardCharsets.UTF_8));
  }

  @Test
  void givenAnIntSchemaAndValue_whenFormattingWithJsonRecordFormatter_expectQuotedInt() {
    int value = 44;
    SinkRecord sinkRecord = TestData.createSinkRecord(Schema.INT32_SCHEMA, value);

    byte[] output = jsonRecordFormatter.format(sinkRecord);

    assertEquals("44", new String(output, StandardCharsets.UTF_8));
  }

  @Test
  void givenAStruct_whenFormattingWithJsonRecordFormatter_expectStructToJson() throws IOException {
    Struct payment = paymentValue(1, true, Currency.EURO, "testSender");
    SinkRecord sinkRecord = createSinkRecord(TestData.paymentSchema(), payment);

    byte[] output = jsonRecordFormatter.format(sinkRecord);

    Map map = objectMapper.readValue(output, Map.class);
    assertEquals(5, map.size());
    assertEquals(1, map.get("id"));
    assertEquals(true, map.get("isCashPayment"));
    assertEquals("testSender", map.get("sender"));
    assertNull(map.get("comment"));
    assertEquals("EURO", map.get("currency"));
  }

  @Test
  void givenASchemalessValue_whenFormattingWithJsonRecordFormatter_expectMapToJson() throws IOException {
    Map<String, Object> schemalessValue = new HashMap<>();
    schemalessValue.put("id", 1);
    schemalessValue.put("sender", "testSender");
    SinkRecord sinkRecord = createSinkRecord(null, schemalessValue);

    byte[] output = jsonRecordFormatter.format(sinkRecord);

    Map map = objectMapper.readValue(output, Map.class);
    assertEquals(2, map.size());
    assertEquals(1, map.get("id"));
    assertEquals("testSender", map.get("sender"));
  }
}