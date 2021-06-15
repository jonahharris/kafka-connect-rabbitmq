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

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.github.jcustenborder.kafka.connect.utils.config.Description;
import com.github.jcustenborder.kafka.connect.utils.config.TaskConfigs;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.source.SourceConnector;
import org.apache.kafka.connect.util.ConnectorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.github.themeetgroup.kafka.connect.rabbitmq.source.RabbitMQSourceConnectorConfig.QUEUE_TOPIC_MAPPING_CONF;

@Description("Connector is used to read from a RabbitMQ Queue or Topic.")
public class RabbitMQSourceConnector extends SourceConnector {

  private Map<String, String> settings;
  private RabbitMQSourceConnectorConfig config;

  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }

  @Override
  public void start(Map<String, String> settings) {
    this.settings = settings;
    this.config = new RabbitMQSourceConnectorConfig(settings);
  }

  @Override
  public Class<? extends Task> taskClass() {
    return RabbitMQSourceTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    String queueToTopicMapping = settings.get(QUEUE_TOPIC_MAPPING_CONF);
    if (queueToTopicMapping == null || queueToTopicMapping.isEmpty()) {
      return TaskConfigs.multiple(this.settings, maxTasks);
    }

    List<String> listQueueToTopicMapping = Arrays.stream(queueToTopicMapping.split(",")).collect(Collectors.toList());
    List<List<String>> partitionedQueueToTopicMapping = ConnectorUtils.groupPartitions(listQueueToTopicMapping, maxTasks);

    List<Map<String, String>> connectorConfig = new ArrayList<>();
    for (List<String> partition : partitionedQueueToTopicMapping) {
      Map<String, String> taskConfig = new HashMap<>(settings);
      taskConfig.put(QUEUE_TOPIC_MAPPING_CONF, String.join(",", partition));
      connectorConfig.add(taskConfig);
    }

    return connectorConfig;
  }

  @Override
  public void stop() { }

  @Override
  public ConfigDef config() {
    return RabbitMQSourceConnectorConfig.config();
  }
}
