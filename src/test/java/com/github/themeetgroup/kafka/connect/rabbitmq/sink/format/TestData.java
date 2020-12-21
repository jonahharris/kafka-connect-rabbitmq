package com.github.themeetgroup.kafka.connect.rabbitmq.sink.format;

import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.sink.SinkRecord;

public class TestData {

  public static Schema paymentSchema() {
    return SchemaBuilder.struct()
        .name("com.github.themeetgroup.kafka.connect.rabbitmq.sink.format.Payment")
        .doc("Payment schema used in unit tests")
        .field("id", SchemaBuilder.int32().build())
        .field("isCashPayment", SchemaBuilder.bool().build())
        .field("sender", SchemaBuilder.string().build())
        .field("comment", SchemaBuilder.string().optional().build())
        .build();
  }

  public static Struct paymentValue(int id, boolean isCashPayment, String sender) {
    return paymentValue(id, isCashPayment, sender, null);
  }

  public static Struct paymentValue(int id, boolean isCashPayment, String sender, String comment) {
    return new Struct(paymentSchema())
        .put("id", id)
        .put("isCashPayment", isCashPayment)
        .put("sender", sender)
        .put("comment", comment);
  }

  public static SinkRecord createSinkRecord(Schema valueSchema, Object value) {
    return new SinkRecord("test", 0, null, null, valueSchema, value, 0);
  }
}
