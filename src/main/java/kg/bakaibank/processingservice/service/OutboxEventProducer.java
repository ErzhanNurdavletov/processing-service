package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.kafka.events.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventProducer {
    private final KafkaTemplate<UUID, Object> kafkaTemplate;

    @Value("${kafka.topic.payments}")
    private String topic;

    public void sendOutbox(OutboxEvent outboxEvent) {
        CompletableFuture<SendResult<UUID, Object>> future =
            kafkaTemplate.send(topic, outboxEvent.paymentId(), outboxEvent);

        future.whenComplete((result, throwable) -> {
            log.info("key: {}", outboxEvent.paymentId());
            log.info("value: {}", outboxEvent);
            if (throwable == null) {
                log.info("event published result: {}", result);
            } else {
                log.warn("exception while publishing event: {}", throwable.getMessage());
            }
        });
    }
}
