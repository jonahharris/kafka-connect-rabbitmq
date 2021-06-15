package com.github.themeetgroup.kafka.connect.rabbitmq.source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.themeetgroup.kafka.connect.rabbitmq.source.RabbitMQSourceConnectorConfig.QUEUE_CONF;
import static com.github.themeetgroup.kafka.connect.rabbitmq.source.RabbitMQSourceConnectorConfig.QUEUE_TOPIC_MAPPING_CONF;
import static com.github.themeetgroup.kafka.connect.rabbitmq.source.RabbitMQSourceConnectorConfig.TOPIC_CONF;
import static org.junit.jupiter.api.Assertions.*;

class RabbitMQSourceConnectorTest {

    private RabbitMQSourceConnector connector;

    @BeforeEach
    void setUp() {
        connector = new RabbitMQSourceConnector();
    }

    @Test
    void givenNormalTopicAndQueueConfig_whenCreatingTaskConfig_expectSameConfigForEveryTask() {
        Map<String, String> settings = new HashMap<>();
        settings.put(TOPIC_CONF, "test_topic");
        settings.put(QUEUE_CONF, "queue1,queue2,queue3");

        connector.start(settings);
        List<Map<String, String>> taskConfigs = connector.taskConfigs(2);

        assertEquals(2, taskConfigs.size());
        assertEquals(taskConfigs.get(0), taskConfigs.get(1));
    }

    @Test
    void givenQueueToTopicMappingConfig_whenCreatingTaskConfig_expectConfigSplitUpPerTask() {
        Map<String, String> settings = new HashMap<>();
        settings.put(QUEUE_TOPIC_MAPPING_CONF, "queue1:topic1,queue2:topic2,queue3:topic2");

        connector.start(settings);
        List<Map<String, String>> taskConfigs = connector.taskConfigs(3);

        assertEquals(3, taskConfigs.size());
        assertEquals("queue1:topic1", taskConfigs.get(0).get(QUEUE_TOPIC_MAPPING_CONF));
        assertEquals("queue2:topic2", taskConfigs.get(1).get(QUEUE_TOPIC_MAPPING_CONF));
        assertEquals("queue3:topic2", taskConfigs.get(2).get(QUEUE_TOPIC_MAPPING_CONF));
    }
}