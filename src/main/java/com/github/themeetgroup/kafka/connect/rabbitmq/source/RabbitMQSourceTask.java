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
import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordConcurrentLinkedDeque;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.RetriableException;
import org.apache.kafka.connect.source.SourceRecord;
import org.apache.kafka.connect.source.SourceTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class RabbitMQSourceTask extends SourceTask {

  private static final Logger log = LoggerFactory.getLogger(RabbitMQSourceTask.class);
  private SourceRecordConcurrentLinkedDeque records;
  private Channel channel;
  private Connection connection;

  @Override
  public String version() {
    return VersionUtil.version(this.getClass());
  }

  @Override
  public void start(Map<String, String> settings) {
    RabbitMQSourceConnectorConfig config = new RabbitMQSourceConnectorConfig(settings);
    this.records = new SourceRecordConcurrentLinkedDeque();
    ConnectConsumer consumer;
    try {
      consumer = new ConnectConsumer(this.records, config);
    } catch (Exception e) {
      throw new ConnectException(e);
    }

    ConnectionFactory connectionFactory = config.connectionFactory();
    try {
      log.info("Opening connection to {}:{}/{} (SSL: {})", config.host, config.port, config.virtualHost, config.useSsl);
      this.connection = connectionFactory.newConnection();
    } catch (IOException | TimeoutException e) {
      throw new ConnectException(e);
    }

    try {
      log.info("Creating Channel");
      this.channel = this.connection.createChannel();
      log.info("Declaring queues");
      for (String queue : config.queues) {
        this.channel.queueDeclare(queue, true, false, false, null);
      }
    } catch (IOException e) {
      throw new ConnectException(e);
    }

    for (String queue : config.queues) {
      try {
        log.info("Starting consumer");
        this.channel.basicConsume(queue, consumer);
        log.info("Setting channel.basicQos({}, {});", config.prefetchCount, config.prefetchGlobal);
        this.channel.basicQos(config.prefetchCount, config.prefetchGlobal);
      } catch (IOException ex) {
        throw new ConnectException(ex);
      }
    }

  }

  @Override
  public void commitRecord(SourceRecord record) {
    Long deliveryTag = (Long) record.sourceOffset().get("deliveryTag");
    try {
      this.channel.basicAck(deliveryTag, false);
    } catch (IOException e) {
      throw new RetriableException(e);
    }
  }

  @Override
  public List<SourceRecord> poll() throws InterruptedException {
    List<SourceRecord> batch = new ArrayList<>(4096);

    while (!this.records.drain(batch)) {
      Thread.sleep(1000);
    }

    return batch;
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
