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
package com.github.jcustenborder.kafka.connect.rabbitmq;

import com.google.common.collect.ImmutableMap;
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Envelope;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.jcustenborder.kafka.connect.utils.AssertStruct.assertStruct;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MessageConverterTest {


  void assertField(final Object expected, final Struct struct, final String fieldName) {
    assertEquals(expected, struct.get(fieldName), fieldName + " does not match.");
  }

  @Test
  public void envelope() {
    final Envelope input = new Envelope(
        13246312L,
        true,
        "exchange",
        "routingKey"
    );

    final Struct actual = MessageConverter.envelope(input);
    assertNotNull(actual, "actual should not be null.");
    assertField(input.getDeliveryTag(), actual, MessageConverter.FIELD_ENVELOPE_DELIVERYTAG);
    assertField(input.getExchange(), actual, MessageConverter.FIELD_ENVELOPE_EXCHANGE);
    assertField(input.getRoutingKey(), actual, MessageConverter.FIELD_ENVELOPE_ROUTINGKEY);
    assertField(input.isRedeliver(), actual, MessageConverter.FIELD_ENVELOPE_ISREDELIVER);
  }


  @Test
  public void headers() {
    final Map<String, Object> INPUT_HEADERS = ImmutableMap.of(
        "int8", Byte.valueOf("1"),
        "int16", Short.valueOf("1"),
        "int32", Integer.valueOf("1"),
        "int64", Long.valueOf("1"),
        "string", "string"
    );

    final Map<String, Struct> expected = ImmutableMap.of(
        "int8", new Struct(MessageConverter.SCHEMA_HEADER_VALUE).put(Schema.Type.INT8.getName(), Byte.valueOf("1")),
        "int16", new Struct(MessageConverter.SCHEMA_HEADER_VALUE).put(Schema.Type.INT16.getName(), Short.valueOf("1")),
        "int32", new Struct(MessageConverter.SCHEMA_HEADER_VALUE).put(Schema.Type.INT32.getName(), Integer.valueOf("1")),
        "int64", new Struct(MessageConverter.SCHEMA_HEADER_VALUE).put(Schema.Type.INT64.getName(), Long.valueOf("1")),
        "string", new Struct(MessageConverter.SCHEMA_HEADER_VALUE).put(Schema.Type.STRING.getName(), "string")
    );

    BasicProperties basicProperties = mock(BasicProperties.class);
    when(basicProperties.getHeaders()).thenReturn(INPUT_HEADERS);
    final Map<String, Struct> actual = MessageConverter.headers(basicProperties);
    verify(basicProperties, only()).getHeaders();

    for(Map.Entry<String, Struct> kvp: expected.entrySet()) {
      assertStruct(kvp.getValue(), actual.get(kvp.getKey()), kvp.getKey());
    }
  }


}
