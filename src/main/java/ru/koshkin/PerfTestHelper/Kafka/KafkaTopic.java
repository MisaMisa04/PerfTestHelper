package ru.koshkin.PerfTestHelper.Kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopic {

    public static final String AUTHENTIFICATION_TOPIC ="authentification";
    public static final String REGISTRATION_TOPIC="registration";

    @Bean
    public NewTopic authTopic() {
        return TopicBuilder.name(AUTHENTIFICATION_TOPIC).partitions(2).build();
    }

    @Bean
    public NewTopic regTopic() {
        return TopicBuilder.name(REGISTRATION_TOPIC).partitions(2).build();
    }

}
