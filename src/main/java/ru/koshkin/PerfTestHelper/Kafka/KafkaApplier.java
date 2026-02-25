package ru.koshkin.PerfTestHelper.Kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaApplier {

    @KafkaListener(topics = KafkaTopic.AUTHENTIFICATION_TOPIC, groupId = "consumer-group")
    private void authTopicListener(String data) {
        System.out.println(data);
    }


    @KafkaListener(topics = KafkaTopic.REGISTRATION_TOPIC, groupId = "consumer-group")
    private void regTopicListener(String data) {
        System.out.println(data);
    }
}
