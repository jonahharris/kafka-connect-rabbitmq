package com.github.themeetgroup.kafka.connect.rabbitmq.source;

import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.DataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static com.github.themeetgroup.kafka.connect.rabbitmq.source.RabbitMQSourceConnectorConfig.QUEUE_CONF;
import static com.github.themeetgroup.kafka.connect.rabbitmq.source.RabbitMQSourceConnectorConfig.QUEUE_TOPIC_MAPPING_CONF;
import static com.github.themeetgroup.kafka.connect.rabbitmq.source.RabbitMQSourceConnectorConfig.TOPIC_CONF;
import static org.junit.jupiter.api.Assertions.*;

class RabbitMQSourceConnectorConfigTest {

    @Test
    void givenNoTopicAndQueueAndQueueToTopicConfig_whenCreatingConfig_expectConnectException() {
        Map<String, String> settings = new HashMap<>();

        assertThrows(ConnectException.class, () -> new RabbitMQSourceConnectorConfig(settings));
    }

    @Test
    void givenTopicAndQueuesCombination_whenCreatingConfig_expectEveryQueueMappedToSameTopic() {
        Map<String, String> settings = new HashMap<>();
        settings.put(TOPIC_CONF, "test_topic");
        settings.put(QUEUE_CONF, "queue1,queue2,queue3");

        RabbitMQSourceConnectorConfig config = new RabbitMQSourceConnectorConfig(settings);

        Map<String, String> queueToTopicMap = config.queueToTopicMap;
        assertEquals(3, queueToTopicMap.size());
        assertEquals("test_topic", queueToTopicMap.get("queue1"));
        assertEquals("test_topic", queueToTopicMap.get("queue2"));
        assertEquals("test_topic", queueToTopicMap.get("queue2"));
    }

    @Test
    void givenOnlyTopicToQueueMapping_whenCreatingConfig_expectTopicCorrectlyMapped() {
        Map<String, String> settings = new HashMap<>();
        settings.put(QUEUE_TOPIC_MAPPING_CONF, "queue1:topic1,queue2:topic2,queue3:topic2");

        RabbitMQSourceConnectorConfig config = new RabbitMQSourceConnectorConfig(settings);

        Map<String, String> queueToTopicMap = config.queueToTopicMap;
        assertEquals(3, queueToTopicMap.size());
        assertEquals("topic1", queueToTopicMap.get("queue1"));
        assertEquals("topic2", queueToTopicMap.get("queue2"));
        assertEquals("topic2", queueToTopicMap.get("queue3"));
    }
}