package kg.bakaibank.processingservice.service;

import kg.bakaibank.processingservice.entity.Outbox;
import kg.bakaibank.processingservice.mapper.OutboxMapper;
import kg.bakaibank.processingservice.service.api.service.OutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxPoller {

    private final OutboxService outboxService;
    private final OutboxEventProducer outboxEventProducer;
    private final OutboxMapper outboxMapper;

    @Scheduled(fixedDelay = 20_000L)
    public void pollUnpublishedOutboxes() {
        Set<Outbox> outboxes = outboxService.findAllNotPublished();
        OffsetDateTime now = OffsetDateTime.now();
        outboxes.forEach(outbox -> outbox.setPublishedAt(now));
        List<Outbox> outboxList = outboxService.saveAll(outboxes);
        outboxList.stream()
            .map(outboxMapper::toEvent)
            .forEach(outboxEventProducer::sendOutbox);
        log.info("polled {} outboxes at {}", outboxes.size(), now);
    }
}
