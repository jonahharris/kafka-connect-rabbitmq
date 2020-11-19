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

import com.github.jcustenborder.kafka.connect.utils.VersionUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.sink.SinkRecord;
import org.apache.kafka.connect.sink.SinkTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.github.themeetgroup.kafka.connect.rabbitmq.sink.RabbitMQSinkConnectorConfig.HEADER_CONF;

public class RabbitMQSinkTask extends SinkTask {
  private static final Logger log = LoggerFactory.getLogger(RabbitMQSinkTask.class);
  RabbitMQSinkConnectorConfig config;

  Channel channel;
  Connection connection;


  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }

  @Override
  public void put(Collection<SinkRecord> sinkRecords) {
    for (SinkRecord record : sinkRecords) {
      log.trace("current sinkRecord value: " + record.value());
      if (!(record.value() instanceof byte[])) {
        throw new ConnectException("the value of the record has an invalid type (must be of type byte[])");
      }
      try {
        channel.basicPublish(this.config.exchange, this.config.routingKey,
              RabbitMQSinkHeaderParser.parse(config.getString(HEADER_CONF)), (byte[]) record.value());
      } catch (IOException e) {
        log.error("There was an error while publishing the outgoing message to RabbitMQ");
        throw new RetriableException(e);
      }
    }
  }

  @Override
  public void start(Map<String, String> settings) {
    this.config = new RabbitMQSinkConnectorConfig(settings);
    ConnectionFactory connectionFactory = this.config.connectionFactory();
    try {
      log.info("Opening connection to {}:{}/{} (SSL: {})", this.config.host, this.config.port, this.config.virtualHost, this.config.useSsl);
      this.connection = connectionFactory.newConnection();
    } catch (IOException | TimeoutException e) {
      throw new ConnectException(e);
    }

    try {
      log.info("Creating Channel");
      this.channel = this.connection.createChannel();
      log.info("Declaring queue");
      this.channel.queueDeclare(this.config.routingKey, true, false, false, null);
    } catch (IOException e) {
      throw new ConnectException(e);
    }
  }

  @Override
  public void stop() {
    try {
      this.connection.close();
    } catch (IOException e) {
      log.error("Exception thrown while closing connection.", e);
    }
  }

}
