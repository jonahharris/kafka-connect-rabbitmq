/**
 * Copyright © 2017 Kyumars Sheykh Esmaili (kyumarss@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.themeetgroup.kafka.connect.rabbitmq.sink;

import com.rabbitmq.client.AMQP;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class RabbitMQSinkHeaderParser {
  private static final String HEADER_SEPARATOR = ",";
  private static final String KEY_VALUE_SEPARATOR = ":";


  private static final Map<String, Supplier<Object>> DEFAULT_HEADERS = new HashMap<>();

  static {
    DEFAULT_HEADERS.put("JMSExpiration", () -> 0);
    DEFAULT_HEADERS.put("JMSMessageID", () -> UUID.randomUUID().toString());
    DEFAULT_HEADERS.put("JMSPriority", () -> 4);
    DEFAULT_HEADERS.put("JMSTimestamp", System::currentTimeMillis);
    DEFAULT_HEADERS.put("JMSType", () -> "TextMessage");

  }

  static AMQP.BasicProperties parse(final String headerConfig) {
    final Map<String, Object> headerTemp = DEFAULT_HEADERS.entrySet()
            .stream()
            .map(entry -> new Pair<>(entry.getKey(), entry.getValue().get()))
            .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    if (headerConfig != null && !headerConfig.isEmpty()) {
      final Map<String, Object> headers = Arrays.stream(headerConfig.split(HEADER_SEPARATOR))
              .map(header -> header.split(KEY_VALUE_SEPARATOR))
              .map(Pair::apply)
              .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
      headers.forEach((k, v) -> headerTemp.merge(k, v, (o, n) -> n));
    }
    return new AMQP.BasicProperties.Builder().headers(headerTemp).build();
  }

  private static final class Pair<K, V> extends AbstractMap.SimpleEntry<K, V> {

    private Pair(K key, V value) {
        super(key, value);
    }

    static Pair<String, String> apply(String[] array2) {
      if (array2.length == 2) {
        return new Pair<>(array2[0], array2[1]);
      } else {
        throw new RuntimeException("Wrong header format");
      }
    }
  }
}
