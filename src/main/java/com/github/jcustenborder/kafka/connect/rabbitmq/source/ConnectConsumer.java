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
package com.github.jcustenborder.kafka.connect.rabbitmq.source;

import com.github.jcustenborder.kafka.connect.rabbitmq.source.data.SourceRecordBuilder;
import com.github.jcustenborder.kafka.connect.utils.data.SourceRecordConcurrentLinkedDeque;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

class ConnectConsumer implements Consumer {

  private static final Logger log = LoggerFactory.getLogger(ConnectConsumer.class);
  private final SourceRecordConcurrentLinkedDeque records;
  private final SourceRecordBuilder sourceRecordBuilder;

  ConnectConsumer(SourceRecordConcurrentLinkedDeque records, RabbitMQSourceConnectorConfig config) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    this.records = records;
    this.sourceRecordBuilder = new SourceRecordBuilder(config);
  }

  @Override
  public void handleConsumeOk(String s) {
    log.trace("handleConsumeOk({})", s);
  }

  @Override
  public void handleCancelOk(String s) {
    log.trace("handleCancelOk({})", s);
  }

  @Override
  public void handleCancel(String s) {
    log.trace("handleCancel({})", s);
  }

  @Override
  public void handleShutdownSignal(String s, ShutdownSignalException e) {
    log.trace("handleShutdownSignal({}, {})", s, e);
  }

  @Override
  public void handleRecoverOk(String s) {
    log.trace("handleRecoverOk({})", s);
  }

  @Override
  public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties basicProperties, byte[] bytes) {
    log.trace("handleDelivery({})", consumerTag);

    SourceRecord sourceRecord = this.sourceRecordBuilder.sourceRecord(consumerTag, envelope, basicProperties, bytes);
    this.records.add(sourceRecord);
  }
}
