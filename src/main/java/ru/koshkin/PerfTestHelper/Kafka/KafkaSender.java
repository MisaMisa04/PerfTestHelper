package ru.koshkin.PerfTestHelper.Kafka;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSender {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(KafkaMessage message) {
        var record = new ProducerRecord<>(message.getTopic(), message.getKey(), message.getValue());
        kafkaTemplate.send(record);
    }
}