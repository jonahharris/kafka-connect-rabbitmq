/**
 * Copyright © 2020 Jan Uyttenhove (jan@insidin.com)
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
package com.github.jcustenborder.kafka.connect.rabbitmq.source.data;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.header.ConnectHeaders;
import org.apache.kafka.connect.header.Headers;

import static com.github.jcustenborder.kafka.connect.rabbitmq.source.data.MessageConverter.basicProperties;

public class BytesSourceMessageConverter implements SourceMessageConverter<String, byte[]> {

  @Override
  public byte[] value(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body) {
    return body;
  }

  @Override
  public Schema valueSchema() {
    return Schema.BYTES_SCHEMA;
  }

  @Override
  public String key(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body) {
    return basicProperties.getMessageId();
  }

  @Override
  public Schema keySchema() {
    return Schema.OPTIONAL_STRING_SCHEMA;
  }

  @Override
  public Headers headers(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] body) {
    return new ConnectHeaders().addStruct("amqp", basicProperties(basicProperties));
  }
}
