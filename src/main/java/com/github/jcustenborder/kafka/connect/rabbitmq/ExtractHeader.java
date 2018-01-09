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

import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.Title;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.ConnectRecord;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.transforms.Transformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class ExtractHeader<R extends ConnectRecord<R>> implements Transformation<R> {
  private static final Logger log = LoggerFactory.getLogger(ExtractHeader.class);
  ExtractHeaderConfig config;

  @Title("ExtractHeader(Key)")
  @Description("This transformation is used to extract a header from the message and use it as a key.")
  public static class Key<R extends ConnectRecord<R>> extends ExtractHeader<R> {

    @Override
    public R apply(R r) {
      final SchemaAndValue headerValue = extractHeader(r);

      return r.newRecord(
          r.topic(),
          r.kafkaPartition(),
          headerValue.schema(),
          headerValue.value(),
          r.valueSchema(),
          r.value(),
          r.timestamp()
      );
    }
  }

  @Title("ExtractHeader(Value)")
  @Description("This transformation is used to extract a header from the message and use it as a value.")
  public static class Value<R extends ConnectRecord<R>> extends ExtractHeader<R> {
    @Override
    public R apply(R r) {
      final SchemaAndValue headerValue = extractHeader(r);

      return r.newRecord(
          r.topic(),
          r.kafkaPartition(),
          r.keySchema(),
          r.key(),
          headerValue.schema(),
          headerValue.value(),
          r.timestamp()
      );
    }
  }


  @Override
  public ConfigDef config() {
    return ExtractHeaderConfig.config();
  }

  protected SchemaAndValue extractHeader(R record) {
    final Struct input = (Struct) record.value();

    if (null == record.value()) {
      throw new DataException("value() cannot be null.");
    }

    final Struct basicProperties = input.getStruct(MessageConverter.FIELD_MESSAGE_BASICPROPERTIES);

    if (null == basicProperties) {
      throw new DataException(
          String.format("Struct does not contain '%s'", MessageConverter.FIELD_MESSAGE_BASICPROPERTIES)
      );
    }

    final Map<String, Struct> headers = basicProperties.getMap(MessageConverter.FIELD_BASIC_PROPERTIES_HEADERS);

    if (null == headers) {
      throw new DataException(
          String.format("Struct(%s) does not contain '%s'", MessageConverter.FIELD_MESSAGE_BASICPROPERTIES, MessageConverter.FIELD_BASIC_PROPERTIES_HEADERS)
      );
    }

    final Struct header = headers.get(this.config.headerName);

    if (null == header) {
      throw new DataException(
          String.format("Headers does does not contain '%s' header.", this.config.headerName)
      );
    }

    final String type = header.getString(MessageConverter.FIELD_BASIC_PROPERTIES_TYPE);
    final Field storageField = header.schema().field(type);
    final Object value = header.get(storageField);
    final Schema schema = SchemaBuilder.type(storageField.schema().type()).build();
    return new SchemaAndValue(schema, value);
  }

  @Override
  public void close() {

  }

  @Override
  public void configure(Map<String, ?> settings) {
    this.config = new ExtractHeaderConfig(settings);
  }
}
