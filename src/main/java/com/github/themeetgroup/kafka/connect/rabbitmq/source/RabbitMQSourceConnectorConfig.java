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
import org.apache.kafka.connect.errors.ConnectException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

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

  public static final String QUEUE_TOPIC_MAPPING_CONF = "rabbitmq.queue.topic.mapping";
  public static final String QUEUE_TOPIC_MAPPING_DOC = "A list containing a mapping between a RabbitMQ queue and a Kafka topic.\n" +
      " This setting is an alternative for the 'rabbitmq.queue' and 'kafka.topic' setting.\n" +
      "  When both settings are present. The 'rabbitmq.queue' and 'kafka.topic' will be used. Example of mapping config: 'queue1:topic1,queue2:topic2'";

  public final int prefetchCount;
  public final boolean prefetchGlobal;
  public final String messageConverter;
  public final Map<String, String> queueToTopicMap;

  public RabbitMQSourceConnectorConfig(Map<String, String> settings) {
    super(config(), settings);

    this.prefetchCount = this.getInt(PREFETCH_COUNT_CONF);
    this.prefetchGlobal = this.getBoolean(PREFETCH_GLOBAL_CONF);
    this.messageConverter = this.getString(MESSAGE_CONVERTER_CLASSNAME_CONF);

    String topic = this.getString(TOPIC_CONF);
    List<String> queues = this.getList(QUEUE_CONF);
    List<String> queueTopicMappingList = this.getList(QUEUE_TOPIC_MAPPING_CONF);

    if (!queues.isEmpty() && !topic.isEmpty()) {
      queueToTopicMap = queues.stream()
          .collect(Collectors.toMap(x -> x, x -> topic));
    } else if (!queueTopicMappingList.isEmpty()) {
      queueToTopicMap = queueTopicMappingList.stream()
          .map(x -> x.split(":"))
          .collect(toMap(x -> x[0], x -> x[1]));
    } else {
      throw new ConnectException("No valid queue / topic configuration has been found. Either use the combination of " +
          "" + TOPIC_CONF + " and " + QUEUE_CONF + " or use the " + QUEUE_TOPIC_MAPPING_CONF + " setting.");
    }
  }

  public static ConfigDef config() {
    return CommonRabbitMQConnectorConfig.config()
        .define(TOPIC_CONF, ConfigDef.Type.STRING, "", ConfigDef.Importance.HIGH, TOPIC_DOC)
        .define(PREFETCH_COUNT_CONF, ConfigDef.Type.INT, 0, ConfigDef.Importance.MEDIUM, PREFETCH_COUNT_DOC)
        .define(PREFETCH_GLOBAL_CONF, ConfigDef.Type.BOOLEAN, false, ConfigDef.Importance.MEDIUM, PREFETCH_GLOBAL_DOC)
        .define(QUEUE_CONF, ConfigDef.Type.LIST, new ArrayList<>(), ConfigDef.Importance.HIGH, QUEUE_DOC)
        .define(MESSAGE_CONVERTER_CLASSNAME_CONF, ConfigDef.Type.STRING, null, ConfigDef.Importance.MEDIUM, MESSAGE_CONVERTER_CLASSNAME_DOC)
        .define(QUEUE_TOPIC_MAPPING_CONF, ConfigDef.Type.LIST, new ArrayList<>(), ConfigDef.Importance.HIGH, QUEUE_TOPIC_MAPPING_DOC);
  }
}
