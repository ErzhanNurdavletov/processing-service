package kg.bakaibank.processingservice.service.api;

import kg.bakaibank.processingservice.entity.Outbox;
import kg.bakaibank.processingservice.entity.Payment;

import java.util.List;
import java.util.Set;

public interface OutboxService {
    void createOutbox(Payment payment);
    Set<Outbox> findAllNotPublished();
    List<Outbox> saveAll(Set<Outbox> outboxes);
}
