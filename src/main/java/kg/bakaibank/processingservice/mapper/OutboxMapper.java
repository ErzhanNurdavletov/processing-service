package kg.bakaibank.processingservice.mapper;

import kg.bakaibank.processingservice.entity.Outbox;
import kg.bakaibank.processingservice.kafka.events.OutboxEvent;
import org.springframework.stereotype.Component;

@Component
public class OutboxMapper {
    public OutboxEvent toEvent(Outbox outbox) {
        if (outbox == null) {
            return null;
        }
        return new OutboxEvent(outbox.getId(),
            outbox.getPayment().getId(),
            outbox.getPaymentStatus(),
            outbox.getAmount(),
            outbox.getPaymentDeclineReason(),
            outbox.getCreatedAt(),
            outbox.getPublishedAt());
    }
}
