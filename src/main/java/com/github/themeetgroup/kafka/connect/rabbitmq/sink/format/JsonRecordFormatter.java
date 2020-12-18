/**
 * Copyright © 2017 Kyumars Sheykh Esmaili (kyumarss@gmail.com)
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

package com.github.themeetgroup.kafka.connect.rabbitmq.sink.format;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.json.JsonConverter;
import org.apache.kafka.connect.sink.SinkRecord;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonRecordFormatter implements RecordFormatter {

  private final ObjectMapper mapper;
  private final JsonConverter converter;

  public JsonRecordFormatter() {
    this.mapper = new ObjectMapper();
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
    mapper.registerModule(new JavaTimeModule());
    converter = new JsonConverter();
    Map<String, Object> converterConfig = new HashMap<>();
    converterConfig.put("schemas.enable", "false");
    converterConfig.put("schemas.cache.size", "10");
    this.converter.configure(converterConfig, false);
  }

  @Override
  public byte[] format(SinkRecord record) {
    try {
      Object value = record.value();
      if (value instanceof Struct) {
        return converter.fromConnectData(
                record.topic(),
                record.valueSchema(),
                value
          );
      } else {
        return mapper.writeValueAsBytes(value);
      }
    } catch (IOException e) {
      throw new ConnectException(e);
    }
  }
}
