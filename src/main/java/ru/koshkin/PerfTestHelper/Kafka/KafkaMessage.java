package ru.koshkin.PerfTestHelper.Kafka;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class KafkaMessage {

    private String topic;
    private String key;
    private String value;
}
