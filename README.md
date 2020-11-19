
# Introduction

# Source Connectors


## RabbitMQSourceConnector

Connector is used to read from a RabbitMQ Queue or Topic.






### Configuration

##### `kafka.topic`
*Importance:* High

*Type:* String


Kafka topic to write the messages to.
##### `rabbitmq.queue`
*Importance:* High

*Type:* List


rabbitmq.queue
##### `rabbitmq.host`
*Importance:* High

*Type:* String

*Default Value:* localhost


The RabbitMQ host to connect to. See `ConnectionFactory.setHost(java.lang.String) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setHost-java.lang.String->`_
##### `rabbitmq.password`
*Importance:* High

*Type:* String

*Default Value:* guest


The password to authenticate to RabbitMQ with. See `ConnectionFactory.setPassword(java.lang.String) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setPassword-java.lang.String->`_
##### `rabbitmq.username`
*Importance:* High

*Type:* String

*Default Value:* guest


The username to authenticate to RabbitMQ with. See `ConnectionFactory.setUsername(java.lang.String) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setUsername-java.lang.String->`_
##### `rabbitmq.virtual.host`
*Importance:* High

*Type:* String

*Default Value:* /

Converter to compose the Kafka message.
##### `message.converter`
*Importance:* Medium

*Type:* String

*Default Value:* com.github.themeetgroup.kafka.connect.rabbitmq.source.data.MessageConverter

The virtual host to use when connecting to the broker. See `ConnectionFactory.setVirtualHost(java.lang.String) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setVirtualHost-java.lang.String->`_
##### `rabbitmq.port`
*Importance:* Medium

*Type:* Int

*Default Value:* 5672


The RabbitMQ port to connect to. See `ConnectionFactory.setPort(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setPort-int->`_
##### `rabbitmq.prefetch.count`
*Importance:* Medium

*Type:* Int

*Default Value:* 0


Maximum number of messages that the server will deliver, 0 if unlimited. See `Channel.basicQos(int, boolean) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/Channel.html#basicQos-int-boolean->`_
##### `rabbitmq.prefetch.global`
*Importance:* Medium

*Type:* Boolean

*Default Value:* false


True if the settings should be applied to the entire channel rather than each consumer. See `Channel.basicQos(int, boolean) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/Channel.html#basicQos-int-boolean->`_
##### `rabbitmq.automatic.recovery.enabled`
*Importance:* Low

*Type:* Boolean

*Default Value:* true


Enables or disables automatic connection recovery. See `ConnectionFactory.setAutomaticRecoveryEnabled(boolean) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setAutomaticRecoveryEnabled-boolean->`_
##### `rabbitmq.connection.timeout.ms`
*Importance:* Low

*Type:* Int

*Default Value:* 60000


Connection TCP establishment timeout in milliseconds. zero for infinite. See `ConnectionFactory.setConnectionTimeout(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setConnectionTimeout-int->`_
##### `rabbitmq.handshake.timeout.ms`
*Importance:* Low

*Type:* Int

*Default Value:* 10000


The AMQP0-9-1 protocol handshake timeout, in milliseconds. See `ConnectionFactory.setHandshakeTimeout(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setHandshakeTimeout-int->`_
##### `rabbitmq.network.recovery.interval.ms`
*Importance:* Low

*Type:* Int

*Default Value:* 10000


See `ConnectionFactory.setNetworkRecoveryInterval(long) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setNetworkRecoveryInterval-long->`_
##### `rabbitmq.requested.channel.max`
*Importance:* Low

*Type:* Int

*Default Value:* 0


Initially requested maximum channel number. Zero for unlimited. See `ConnectionFactory.setRequestedChannelMax(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setRequestedChannelMax-int->`_
##### `rabbitmq.requested.frame.max`
*Importance:* Low

*Type:* Int

*Default Value:* 0


Initially requested maximum frame size, in octets. Zero for unlimited. See `ConnectionFactory.setRequestedFrameMax(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setRequestedFrameMax-int->`_
##### `rabbitmq.requested.heartbeat.seconds`
*Importance:* Low

*Type:* Int

*Default Value:* 60


Set the requested heartbeat timeout. Heartbeat frames will be sent at about 1/2 the timeout interval. If server heartbeat timeout is configured to a non-zero value, this method can only be used to lower the value; otherwise any value provided by the client will be used. See `ConnectionFactory.setRequestedHeartbeat(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setRequestedHeartbeat-int->`_
##### `rabbitmq.shutdown.timeout.ms`
*Importance:* Low

*Type:* Int

*Default Value:* 10000


Set the shutdown timeout. This is the amount of time that Consumer implementations have to continue working through deliveries (and other Consumer callbacks) after the connection has closed but before the ConsumerWorkService is torn down. If consumers exceed this timeout then any remaining queued deliveries (and other Consumer callbacks, *including* the Consumer's handleShutdownSignal() invocation) will be lost. See `ConnectionFactory.setShutdownTimeout(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setShutdownTimeout-int->`_
##### `rabbitmq.topology.recovery.enabled`
*Importance:* Low

*Type:* Boolean

*Default Value:* true


Enables or disables topology recovery. See `ConnectionFactory.setTopologyRecoveryEnabled(boolean) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setTopologyRecoveryEnabled-boolean->`_

#### Examples

##### Standalone Example

This configuration is used typically along with [standalone mode](http://docs.confluent.io/current/connect/concepts.html#standalone-workers).

```properties
name=RabbitMQSourceConnector1
connector.class=com.github.themeetgroup.kafka.connect.rabbitmq.source.RabbitMQSourceConnector
tasks.max=1
kafka.topic=< Required Configuration >
rabbitmq.queue=< Required Configuration >
```

##### Distributed Example

This configuration is used typically along with [distributed mode](http://docs.confluent.io/current/connect/concepts.html#distributed-workers).
Write the following json to `connector.json`, configure all of the required values, and use the command below to
post the configuration to one the distributed connect worker(s).

```json
{
  "config" : {
    "name" : "RabbitMQSourceConnector1",
    "connector.class" : "com.github.themeetgroup.kafka.connect.rabbitmq.source.RabbitMQSourceConnector",
    "tasks.max" : "1",
    "kafka.topic" : "< Required Configuration >",
    "rabbitmq.queue" : "< Required Configuration >"
  }
}
```

Use curl to post the configuration to one of the Kafka Connect Workers. Change `http://localhost:8083/` the the endpoint of
one of your Kafka Connect worker(s).

Create a new instance.
```bash
curl -s -X POST -H 'Content-Type: application/json' --data @connector.json http://localhost:8083/connectors
```

Update an existing instance.
```bash
curl -s -X PUT -H 'Content-Type: application/json' --data @connector.json http://localhost:8083/connectors/TestSinkConnector1/config
```



# Sink Connectors


## RabbitMQSinkConnector

Connector is used to read data from a Kafka topic and publish it on a RabbitMQ exchange and routing key pair.






### Configuration

##### `rabbitmq.exchange`
*Importance:* High

*Type:* String


exchange to publish the messages on.
##### `rabbitmq.routing.key`
*Importance:* High

*Type:* String


routing key used for publishing the messages.
##### `topics`
*Importance:* High

*Type:* String


Kafka topic to read the messages from.
##### `rabbitmq.host`
*Importance:* High

*Type:* String

*Default Value:* localhost


The RabbitMQ host to connect to. See `ConnectionFactory.setHost(java.lang.String) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setHost-java.lang.String->`_
##### `rabbitmq.password`
*Importance:* High

*Type:* String

*Default Value:* guest


The password to authenticate to RabbitMQ with. See `ConnectionFactory.setPassword(java.lang.String) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setPassword-java.lang.String->`_
##### `rabbitmq.username`
*Importance:* High

*Type:* String

*Default Value:* guest


The username to authenticate to RabbitMQ with. See `ConnectionFactory.setUsername(java.lang.String) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setUsername-java.lang.String->`_
##### `rabbitmq.virtual.host`
*Importance:* High

*Type:* String

*Default Value:* /


The virtual host to use when connecting to the broker. See `ConnectionFactory.setVirtualHost(java.lang.String) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setVirtualHost-java.lang.String->`_
##### `rabbitmq.port`
*Importance:* Medium

*Type:* Int

*Default Value:* 5672


The RabbitMQ port to connect to. See `ConnectionFactory.setPort(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setPort-int->`_
##### `rabbitmq.automatic.recovery.enabled`
*Importance:* Low

*Type:* Boolean

*Default Value:* true


Enables or disables automatic connection recovery. See `ConnectionFactory.setAutomaticRecoveryEnabled(boolean) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setAutomaticRecoveryEnabled-boolean->`_
##### `rabbitmq.connection.timeout.ms`
*Importance:* Low

*Type:* Int

*Default Value:* 60000


Connection TCP establishment timeout in milliseconds. zero for infinite. See `ConnectionFactory.setConnectionTimeout(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setConnectionTimeout-int->`_
##### `rabbitmq.handshake.timeout.ms`
*Importance:* Low

*Type:* Int

*Default Value:* 10000


The AMQP0-9-1 protocol handshake timeout, in milliseconds. See `ConnectionFactory.setHandshakeTimeout(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setHandshakeTimeout-int->`_
##### `rabbitmq.network.recovery.interval.ms`
*Importance:* Low

*Type:* Int

*Default Value:* 10000


See `ConnectionFactory.setNetworkRecoveryInterval(long) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setNetworkRecoveryInterval-long->`_
##### `rabbitmq.requested.channel.max`
*Importance:* Low

*Type:* Int

*Default Value:* 0


Initially requested maximum channel number. Zero for unlimited. See `ConnectionFactory.setRequestedChannelMax(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setRequestedChannelMax-int->`_
##### `rabbitmq.requested.frame.max`
*Importance:* Low

*Type:* Int

*Default Value:* 0


Initially requested maximum frame size, in octets. Zero for unlimited. See `ConnectionFactory.setRequestedFrameMax(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setRequestedFrameMax-int->`_
##### `rabbitmq.requested.heartbeat.seconds`
*Importance:* Low

*Type:* Int

*Default Value:* 60


Set the requested heartbeat timeout. Heartbeat frames will be sent at about 1/2 the timeout interval. If server heartbeat timeout is configured to a non-zero value, this method can only be used to lower the value; otherwise any value provided by the client will be used. See `ConnectionFactory.setRequestedHeartbeat(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setRequestedHeartbeat-int->`_
##### `rabbitmq.shutdown.timeout.ms`
*Importance:* Low

*Type:* Int

*Default Value:* 10000


Set the shutdown timeout. This is the amount of time that Consumer implementations have to continue working through deliveries (and other Consumer callbacks) after the connection has closed but before the ConsumerWorkService is torn down. If consumers exceed this timeout then any remaining queued deliveries (and other Consumer callbacks, *including* the Consumer's handleShutdownSignal() invocation) will be lost. See `ConnectionFactory.setShutdownTimeout(int) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setShutdownTimeout-int->`_
##### `rabbitmq.topology.recovery.enabled`
*Importance:* Low

*Type:* Boolean

*Default Value:* true


Enables or disables topology recovery. See `ConnectionFactory.setTopologyRecoveryEnabled(boolean) <https://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/ConnectionFactory.html#setTopologyRecoveryEnabled-boolean->`_

#### Examples

##### Standalone Example

This configuration is used typically along with [standalone mode](http://docs.confluent.io/current/connect/concepts.html#standalone-workers).

```properties
name=RabbitMQSinkConnector1
connector.class=com.github.themeetgroup.kafka.connect.rabbitmq.sink.RabbitMQSinkConnector
tasks.max=1
topics=< Required Configuration >
rabbitmq.exchange=< Required Configuration >
rabbitmq.routing.key=< Required Configuration >
topics=< Required Configuration >
```

##### Distributed Example

This configuration is used typically along with [distributed mode](http://docs.confluent.io/current/connect/concepts.html#distributed-workers).
Write the following json to `connector.json`, configure all of the required values, and use the command below to
post the configuration to one the distributed connect worker(s).

```json
{
  "config" : {
    "name" : "RabbitMQSinkConnector1",
    "connector.class" : "com.github.themeetgroup.kafka.connect.rabbitmq.sink.RabbitMQSinkConnector",
    "tasks.max" : "1",
    "topics" : "< Required Configuration >",
    "rabbitmq.exchange" : "< Required Configuration >",
    "rabbitmq.routing.key" : "< Required Configuration >"
  }
}
```

Use curl to post the configuration to one of the Kafka Connect Workers. Change `http://localhost:8083/` the the endpoint of
one of your Kafka Connect worker(s).

Create a new instance.
```bash
curl -s -X POST -H 'Content-Type: application/json' --data @connector.json http://localhost:8083/connectors
```

Update an existing instance.
```bash
curl -s -X PUT -H 'Content-Type: application/json' --data @connector.json http://localhost:8083/connectors/TestSinkConnector1/config
```


