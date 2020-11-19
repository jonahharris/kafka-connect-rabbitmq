/**
 * Copyright © 2017 Jeremy Custenborder (jcustenborder@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.themeetgroup.kafka.connect.rabbitmq.source.data;

import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.LongString;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.data.Timestamp;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;

public class MessageConverter implements SourceMessageConverter<Struct, Struct> {
  private static final Logger log = LoggerFactory.getLogger(MessageConverter.class);
  static final String FIELD_ENVELOPE_DELIVERYTAG = "deliveryTag";
  static final String FIELD_ENVELOPE_ISREDELIVER = "isRedeliver";
  static final String FIELD_ENVELOPE_EXCHANGE = "exchange";
  static final String FIELD_ENVELOPE_ROUTINGKEY = "routingKey";

  static final Schema SCHEMA_ENVELOPE = SchemaBuilder.struct()
      .name("com.github.themeetgroup.kafka.connect.rabbitmq.Envelope")
      .doc("Encapsulates a group of parameters used for AMQP's Basic methods. See " +
          "`Envelope <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/Envelope.html>`_")
      .field(FIELD_ENVELOPE_DELIVERYTAG, SchemaBuilder.int64().doc("The delivery tag included in this parameter envelope. See `Envelope.getDeliveryTag() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/Envelope.html#getDeliveryTag-->`_").build())
      .field(FIELD_ENVELOPE_ISREDELIVER, SchemaBuilder.bool().doc("The redelivery flag included in this parameter envelope. See `Envelope.isRedeliver() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/Envelope.html#isRedeliver-->`_").build())
      .field(FIELD_ENVELOPE_EXCHANGE, SchemaBuilder.string().optional().doc("The name of the exchange included in this parameter envelope. See `Envelope.getExchange() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/Envelope.html#getExchange-->`_"))
      .field(FIELD_ENVELOPE_ROUTINGKEY, SchemaBuilder.string().optional().doc("The routing key included in this parameter envelope. See `Envelope.getRoutingKey() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/Envelope.html#getRoutingKey-->`_").build())
      .build();

  static Struct envelope(Envelope envelope) {
    return new Struct(SCHEMA_ENVELOPE)
        .put(FIELD_ENVELOPE_DELIVERYTAG, envelope.getDeliveryTag())
        .put(FIELD_ENVELOPE_ISREDELIVER, envelope.isRedeliver())
        .put(FIELD_ENVELOPE_EXCHANGE, envelope.getExchange())
        .put(FIELD_ENVELOPE_ROUTINGKEY, envelope.getRoutingKey());
  }

  static final Schema SCHEMA_HEADER_VALUE;

  static {
    SchemaBuilder builder = SchemaBuilder.struct()
        .name("com.github.themeetgroup.kafka.connect.rabbitmq.BasicProperties.HeaderValue")
        .doc("Used to store the value of a header value. The `type` field stores the type of the data and the corresponding " +
            "field to read the data from.")
        .field("type", SchemaBuilder.string().doc("Used to define the type for the HeaderValue. " +
            "This will define the corresponding field which will contain the value in it's original type.").build()
        )
        .field("timestamp", Timestamp.builder().optional().doc("Storage for when the `type` field is set to `timestamp`. Null otherwise.").build())
        .field("array", SchemaBuilder.array(Schema.STRING_SCHEMA).optional().doc("Storage for when the `type` field is set to `array`. Null otherwise.").build());

    for (Schema.Type v : Schema.Type.values()) {
      if (Schema.Type.ARRAY == v || Schema.Type.MAP == v || Schema.Type.STRUCT == v) {
        continue;
      }
      final String doc = String.format("Storage for when the `type` field is set to `%s`. Null otherwise.", v.name().toLowerCase());

      Schema fieldSchema = SchemaBuilder.type(v)
          .doc(doc)
          .optional()
          .build();
      builder.field(v.name().toLowerCase(), fieldSchema);
    }

    SCHEMA_HEADER_VALUE = builder.build();
  }

  static final String FIELD_BASIC_PROPERTIES_CONTENTTYPE = "contentType";
  static final String FIELD_BASIC_PROPERTIES_CONTENTENCODING = "contentEncoding";
  static final String FIELD_BASIC_PROPERTIES_HEADERS = "headers";
  static final String FIELD_BASIC_PROPERTIES_DELIVERYMODE = "deliveryMode";
  static final String FIELD_BASIC_PROPERTIES_PRIORITY = "priority";
  static final String FIELD_BASIC_PROPERTIES_CORRELATIONID = "correlationId";
  static final String FIELD_BASIC_PROPERTIES_REPLYTO = "replyTo";
  static final String FIELD_BASIC_PROPERTIES_EXPIRATION = "expiration";
  static final String FIELD_BASIC_PROPERTIES_MESSAGEID = "messageId";
  static final String FIELD_BASIC_PROPERTIES_TIMESTAMP = "timestamp";
  static final String FIELD_BASIC_PROPERTIES_TYPE = "type";
  static final String FIELD_BASIC_PROPERTIES_USERID = "userId";
  static final String FIELD_BASIC_PROPERTIES_APPID = "appId";

  static final Schema SCHEMA_KEY = SchemaBuilder.struct()
      .name("com.github.themeetgroup.kafka.connect.rabbitmq.MessageKey")
      .doc("Key used for partition assignment in Kafka.")
      .field(
          FIELD_BASIC_PROPERTIES_MESSAGEID,
          SchemaBuilder.string().optional().doc("The value in the messageId field. " +
              "`BasicProperties.getMessageId() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getMessageId-->`_").build()
      )
      .build();

  static final Schema SCHEMA_BASIC_PROPERTIES = SchemaBuilder.struct()
      .name("com.github.themeetgroup.kafka.connect.rabbitmq.BasicProperties")
      .optional()
      .doc("Corresponds to the `BasicProperties <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html>`_")
      .field(
          FIELD_BASIC_PROPERTIES_CONTENTTYPE,
          SchemaBuilder.string().optional().doc("The value in the contentType field. " +
              "See `BasicProperties.getContentType() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getContentType-->`_")
              .build()
      )
      .field(
          FIELD_BASIC_PROPERTIES_CONTENTENCODING,
          SchemaBuilder.string().optional().doc("The value in the contentEncoding field. " +
              "See `BasicProperties.getContentEncoding() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getContentEncoding-->`_").build()
      )
      .field(
          FIELD_BASIC_PROPERTIES_HEADERS,
          SchemaBuilder.map(Schema.STRING_SCHEMA, SCHEMA_HEADER_VALUE).build()
      )
      .field(
          FIELD_BASIC_PROPERTIES_DELIVERYMODE,
          SchemaBuilder.int32().optional().doc("The value in the deliveryMode field. " +
              "`BasicProperties.html.getDeliveryMode() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getDeliveryMode-->`_ ").build()
      )
      .field(
          FIELD_BASIC_PROPERTIES_PRIORITY,
          SchemaBuilder.int32().optional().doc("The value in the priority field. " +
              "`BasicProperties.getPriority() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getPriority-->`_").build()
      )
      .field(
          FIELD_BASIC_PROPERTIES_CORRELATIONID,
          SchemaBuilder.string().optional().doc("The value in the correlationId field. " +
              "See `BasicProperties.getCorrelationId() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getCorrelationId-->`_").build()
      )
      .field(
          FIELD_BASIC_PROPERTIES_REPLYTO,
          SchemaBuilder.string().optional().doc("The value in the replyTo field. " +
              "`BasicProperties.getReplyTo() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getReplyTo-->`_")
      )
      .field(
          FIELD_BASIC_PROPERTIES_EXPIRATION,
          SchemaBuilder.string().optional().doc("The value in the expiration field. " +
              "See `BasicProperties.getExpiration() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getExpiration-->`_").build()
      )
      .field(
          FIELD_BASIC_PROPERTIES_MESSAGEID,
          SchemaBuilder.string().optional().doc("The value in the messageId field. " +
              "`BasicProperties.getMessageId() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getMessageId-->`_").build()
      )
      .field(
          FIELD_BASIC_PROPERTIES_TIMESTAMP, Timestamp.builder().optional().doc("The value in the timestamp field. " +
              "`BasicProperties.getTimestamp() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getTimestamp-->`_").build()
      )
      .field(
          FIELD_BASIC_PROPERTIES_TYPE, SchemaBuilder.string().optional().doc("The value in the type field. " +
              "`BasicProperties.getType() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getType-->`_").build()
      )
      .field(
          FIELD_BASIC_PROPERTIES_USERID,
          SchemaBuilder.string().optional().doc("The value in the userId field. " +
              "`BasicProperties.getUserId() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getUserId-->`_").build()
      )
      .field(
          FIELD_BASIC_PROPERTIES_APPID,
          SchemaBuilder.string().optional().doc("The value in the appId field. " +
              "`BasicProperties.getAppId() <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/BasicProperties.html#getAppId-->`_").build()
      )
      .build();

  static final Map<Class<?>, String> FIELD_LOOKUP;

  static {
    Map<Class<?>, String> fieldLookup = new HashMap<>();
    fieldLookup.put(String.class, Schema.Type.STRING.name().toLowerCase());
    fieldLookup.put(Byte.class, Schema.Type.INT8.name().toLowerCase());
    fieldLookup.put(Short.class, Schema.Type.INT16.name().toLowerCase());
    fieldLookup.put(Integer.class, Schema.Type.INT32.name().toLowerCase());
    fieldLookup.put(Long.class, Schema.Type.INT64.name().toLowerCase());
    fieldLookup.put(Float.class, Schema.Type.FLOAT32.name().toLowerCase());
    fieldLookup.put(Double.class, Schema.Type.FLOAT64.name().toLowerCase());
    fieldLookup.put(Boolean.class, Schema.Type.BOOLEAN.name().toLowerCase());
    fieldLookup.put(ArrayList.class, Schema.Type.ARRAY.name().toLowerCase());
    fieldLookup.put(Date.class, "timestamp");
    FIELD_LOOKUP = ImmutableMap.copyOf(fieldLookup);
  }

  static Map<String, Struct> headers(BasicProperties basicProperties) {
    Map<String, Object> input = basicProperties.getHeaders();
    Map<String, Struct> results = new LinkedHashMap<>();
    if (null != input) {
      for (Map.Entry<String, Object> kvp : input.entrySet()) {
        log.trace("headers() - key = '{}' value= '{}'", kvp.getKey(), kvp.getValue());
        final String field;
        final Object headerValue;

        if (kvp.getValue() instanceof LongString) {
          headerValue = kvp.getValue().toString();
        } else if (kvp.getValue() instanceof List) {
          final List<LongString> list = (List<LongString>) kvp.getValue();
          final List<String> values = new ArrayList<>(list.size());
          for (LongString l : list) {
            values.add(l.toString());
          }
          headerValue = values;
        } else {
          headerValue = kvp.getValue();
        }

        if (!FIELD_LOOKUP.containsKey(headerValue.getClass())) {
          log.warn(
              String.format("Could not determine the type for field '%s' type '%s', skipping", kvp.getKey(), headerValue.getClass().getName())
          );
          continue;
        } else {
          field = FIELD_LOOKUP.get(headerValue.getClass());
        }

        log.trace("headers() - Storing value for header in field = '{}' as {}", field, field);

        Struct value = new Struct(SCHEMA_HEADER_VALUE)
            .put("type", field)
            .put(field, headerValue);
        results.put(kvp.getKey(), value);
      }
    }
    return results;
  }

  static Struct basicProperties(BasicProperties basicProperties) {
    if (null == basicProperties) {
      log.trace("basicProperties() - basicProperties is null.");
      return null;
    }

    Map<String, Struct> headers = headers(basicProperties);
    return new Struct(SCHEMA_BASIC_PROPERTIES)
        .put(FIELD_BASIC_PROPERTIES_CONTENTTYPE, basicProperties.getContentType())
        .put(FIELD_BASIC_PROPERTIES_CONTENTENCODING, basicProperties.getContentEncoding())
        .put(FIELD_BASIC_PROPERTIES_HEADERS, headers)
        .put(FIELD_BASIC_PROPERTIES_DELIVERYMODE, basicProperties.getDeliveryMode())
        .put(FIELD_BASIC_PROPERTIES_PRIORITY, basicProperties.getPriority())
        .put(FIELD_BASIC_PROPERTIES_CORRELATIONID, basicProperties.getCorrelationId())
        .put(FIELD_BASIC_PROPERTIES_REPLYTO, basicProperties.getReplyTo())
        .put(FIELD_BASIC_PROPERTIES_EXPIRATION, basicProperties.getExpiration())
        .put(FIELD_BASIC_PROPERTIES_MESSAGEID, basicProperties.getMessageId())
        .put(FIELD_BASIC_PROPERTIES_TIMESTAMP, basicProperties.getTimestamp())
        .put(FIELD_BASIC_PROPERTIES_TYPE, basicProperties.getType())
        .put(FIELD_BASIC_PROPERTIES_USERID, basicProperties.getUserId())
        .put(FIELD_BASIC_PROPERTIES_APPID, basicProperties.getAppId());
  }

  static final String FIELD_MESSAGE_CONSUMERTAG = "consumerTag";
  static final String FIELD_MESSAGE_ENVELOPE = "envelope";
  static final String FIELD_MESSAGE_BASICPROPERTIES = "basicProperties";
  static final String FIELD_MESSAGE_BODY = "body";


  static final Schema SCHEMA_VALUE = SchemaBuilder.struct()
      .name("com.github.themeetgroup.kafka.connect.rabbitmq.Message")
      .doc("Message as it is delivered to the `RabbitMQ Consumer <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/Consumer.html#handleDelivery-java.lang.String-com.rabbitmq.client.Envelope-com.rabbitmq.client.AMQP.BasicProperties-byte:A->`_ ")
      .field(FIELD_MESSAGE_CONSUMERTAG, SchemaBuilder.string().doc("The consumer tag associated with the consumer").build())
      .field(FIELD_MESSAGE_ENVELOPE, SCHEMA_ENVELOPE)
      .field(FIELD_MESSAGE_BASICPROPERTIES, SCHEMA_BASIC_PROPERTIES)
      .field(FIELD_MESSAGE_BODY, SchemaBuilder.bytes().doc("The value body (opaque, client-specific byte array)").build())
      .build();

  @Override
  public Struct value(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body) {
    return new Struct(valueSchema())
        .put(FIELD_MESSAGE_CONSUMERTAG, consumerTag)
        .put(FIELD_MESSAGE_ENVELOPE, envelope(envelope))
        .put(FIELD_MESSAGE_BASICPROPERTIES, basicProperties(basicProperties))
        .put(FIELD_MESSAGE_BODY, body);
  }

  @Override
  public Schema valueSchema() {
    return SCHEMA_VALUE;
  }

  @Override
  public Struct key(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body) {
    return new Struct(keySchema())
        .put(FIELD_BASIC_PROPERTIES_MESSAGEID, basicProperties.getMessageId());
  }

  @Override
  public Schema keySchema() {
    return SCHEMA_KEY;
  }

  @Override
  public Headers headers(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body) {
    return new ConnectHeaders();
  }
}
