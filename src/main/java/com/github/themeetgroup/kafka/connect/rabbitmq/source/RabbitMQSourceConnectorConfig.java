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
package com.github.themeetgroup.kafka.connect.rabbitmq.source;

import com.github.themeetgroup.kafka.connect.rabbitmq.CommonRabbitMQConnectorConfig;
import org.apache.kafka.common.config.ConfigDef;

import java.util.List;
import java.util.Map;

public class RabbitMQSourceConnectorConfig extends CommonRabbitMQConnectorConfig {

  public static final String TOPIC_CONF = "kafka.topic";
  public static final String TOPIC_DOC = "Kafka topic to write the messages to.";

  public static final String QUEUE_CONF = "rabbitmq.queue";
  public static final String QUEUE_DOC = "rabbitmq.queue";

  public static final String PREFETCH_COUNT_CONF = "rabbitmq.prefetch.count";
  public static final String PREFETCH_COUNT_DOC = "Maximum number of messages that the server will deliver, 0 if unlimited. " +
      "See `Channel.basicQos(int, boolean) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/Channel.html#basicQos-int-boolean->`_";

  public static final String PREFETCH_GLOBAL_CONF = "rabbitmq.prefetch.global";
  public static final String PREFETCH_GLOBAL_DOC = "True if the settings should be applied to the entire channel rather " +
      "than each consumer. " +
      "See `Channel.basicQos(int, boolean) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/Channel.html#basicQos-int-boolean->`_";

  public static final String MESSAGE_CONVERTER_CLASSNAME_CONF = "message.converter";
  public static final String MESSAGE_CONVERTER_CLASSNAME_DOC = "Converter to compose the Kafka message. Optional, defaults to " +
      "com.github.themeetgroup.kafka.connect.rabbitmq.source.data.MessageConverter";

  public final String kafkaTopic;
  public final List<String> queues;
  public final int prefetchCount;
  public final boolean prefetchGlobal;
  public final String messageConverter;

  public RabbitMQSourceConnectorConfig(Map<String, String> settings) {
    super(config(), settings);

    this.kafkaTopic = this.getString(TOPIC_CONF);
    this.queues = this.getList(QUEUE_CONF);
    this.prefetchCount = this.getInt(PREFETCH_COUNT_CONF);
    this.prefetchGlobal = this.getBoolean(PREFETCH_GLOBAL_CONF);
    this.messageConverter = this.getString(MESSAGE_CONVERTER_CLASSNAME_CONF);
  }

  public static ConfigDef config() {
    return CommonRabbitMQConnectorConfig.config()
        .define(TOPIC_CONF, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, TOPIC_DOC)
        .define(PREFETCH_COUNT_CONF, ConfigDef.Type.INT, 0, ConfigDef.Importance.MEDIUM, PREFETCH_COUNT_DOC)
        .define(PREFETCH_GLOBAL_CONF, ConfigDef.Type.BOOLEAN, false, ConfigDef.Importance.MEDIUM, PREFETCH_GLOBAL_DOC)
        .define(QUEUE_CONF, ConfigDef.Type.LIST, ConfigDef.Importance.HIGH, QUEUE_DOC)
        .define(MESSAGE_CONVERTER_CLASSNAME_CONF, ConfigDef.Type.STRING, ConfigDef.Importance.MEDIUM, MESSAGE_CONVERTER_CLASSNAME_DOC);
  }
}
