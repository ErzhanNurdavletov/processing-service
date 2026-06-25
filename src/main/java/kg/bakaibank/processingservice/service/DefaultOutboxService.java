package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.entity.Outbox;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;
import kg.bakaibank.processingservice.repository.OutboxRepository;
import kg.bakaibank.processingservice.service.api.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DefaultOutboxService implements OutboxService {

    private final OutboxRepository outboxRepository;

    @Override
    @Transactional
    public void createOutbox(Payment payment) {
        Outbox outbox = Outbox.builder()
            .payment(payment)
            .paymentStatus(payment.getStatus())
            .amount(payment.getAmount())
            .createdAt(OffsetDateTime.now())
            .build();

        if (payment.getStatus() == PaymentStatus.DECLINED) {
            outbox.setPaymentDeclineReason(payment.getDeclineReason());
        }
        outboxRepository.save(outbox);
    }

    @Override
    public Set<Outbox> findAllNotPublished() {
        return outboxRepository.findAllByPublishedAtIsNull();
    }

    @Override
    @Transactional
    public List<Outbox> saveAll(Set<Outbox> outboxes) {
        return outboxRepository.saveAll(outboxes);
    }
}
