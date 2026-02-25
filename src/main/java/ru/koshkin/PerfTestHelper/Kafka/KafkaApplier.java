package ru.koshkin.PerfTestHelper.Kafka;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        name = "kafka.self-consumer",
        havingValue = "true",
        matchIfMissing = false
)
public class KafkaApplier {

    @KafkaListener(topics = KafkaTopic.AUTHENTIFICATION_TOPIC, groupId = KafkaConsumerConfig.CONSUMER_GROUP_NAME)
    private void authTopicListener(String data) {
        System.out.println(data);
    }


    @KafkaListener(topics = KafkaTopic.REGISTRATION_TOPIC, groupId = KafkaConsumerConfig.CONSUMER_GROUP_NAME)
    private void regTopicListener(String data) {
        System.out.println(data);
    }
}
