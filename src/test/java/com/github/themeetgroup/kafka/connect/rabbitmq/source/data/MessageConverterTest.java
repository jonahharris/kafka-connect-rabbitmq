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
import com.rabbitmq.client.BasicProperties;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.LongString;
import com.rabbitmq.client.impl.LongStringHelper;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.github.jcustenborder.kafka.connect.utils.AssertStruct.assertStruct;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
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
  public void basicPropertiesNull() {
    Struct basicProperties = MessageConverter.basicProperties(null);
    assertNull(basicProperties);
  }


  Struct struct(Schema.Type type, Object value) {
    final String t = type.getName().toLowerCase();
    return new Struct(MessageConverter.SCHEMA_HEADER_VALUE)
        .put("type", t)
        .put(t, value);
  }

  static class HeaderTestCase {
    public final Object input;
    public final String type;
    public final Object expected;

    private HeaderTestCase(Object input, String type, Object expected) {
      this.input = input;
      this.type = type;
      this.expected = expected;
    }

    Struct expectedStruct() {
      final String field = this.type.toString().toLowerCase();
      return new Struct(MessageConverter.SCHEMA_HEADER_VALUE)
          .put("type", field)
          .put(field, this.expected);
    }

    @Override
    public String toString() {
      return String.format("%s - %s", this.type, this.input.getClass().getName());
    }

    public static final HeaderTestCase of(Object input, String type, Object expected) {
      return new HeaderTestCase(input, type, expected);
    }
  }

  @TestFactory
  public Stream<DynamicTest> headers() {
    final List<LongString> listHeader = new ArrayList<>();
    listHeader.add(LongStringHelper.asLongString("1"));
    listHeader.add(LongStringHelper.asLongString("2"));
    listHeader.add(LongStringHelper.asLongString("3"));
    listHeader.add(LongStringHelper.asLongString("4"));
    final List<String> listHeaderValue = new ArrayList<>();
    listHeaderValue.add("1");
    listHeaderValue.add("2");
    listHeaderValue.add("3");
    listHeaderValue.add("4");
    final List<HeaderTestCase> tests = Arrays.asList(
        HeaderTestCase.of(Byte.valueOf("1"), Schema.Type.INT8.toString().toLowerCase(), Byte.valueOf("1")),
        HeaderTestCase.of(Short.valueOf("1"), Schema.Type.INT16.toString().toLowerCase(), Short.valueOf("1")),
        HeaderTestCase.of(Integer.valueOf("1"), Schema.Type.INT32.toString().toLowerCase(), Integer.valueOf("1")),
        HeaderTestCase.of(Long.valueOf("1"), Schema.Type.INT64.toString().toLowerCase(), Long.valueOf("1")),
        HeaderTestCase.of(Float.valueOf("1"), Schema.Type.FLOAT32.toString().toLowerCase(), Float.valueOf("1")),
        HeaderTestCase.of(Double.valueOf("1"), Schema.Type.FLOAT64.toString().toLowerCase(), Double.valueOf("1")),
        HeaderTestCase.of("1", Schema.Type.STRING.toString().toLowerCase(), "1"),
        HeaderTestCase.of(LongStringHelper.asLongString("1"), Schema.Type.STRING.toString().toLowerCase(), "1"),
        HeaderTestCase.of(new Date(1500691965123L), "timestamp", new Date(1500691965123L)),
        HeaderTestCase.of(listHeader, "array", listHeaderValue)
    );

    return tests.stream().map(test -> dynamicTest(test.toString(), () -> {
      final Map<String, Object> INPUT_HEADERS = ImmutableMap.of("input", test.input);
      BasicProperties basicProperties = mock(BasicProperties.class);
      when(basicProperties.getHeaders()).thenReturn(INPUT_HEADERS);
      final Map<String, Struct> actual = MessageConverter.headers(basicProperties);
      verify(basicProperties, only()).getHeaders();
      assertNotNull(actual, "actual should not be null.");
      assertTrue(actual.containsKey("input"), "actual should contain key 'input'");
      Struct actualStruct = actual.get("input");
      actualStruct.validate();
      assertStruct(test.expectedStruct(), actualStruct);
    }));
  }


}
