package kg.bakaibank.processingservice.service.api.facade;

import kg.bakaibank.processingservice.payload.enums.PaymentAccountType;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
import kg.bakaibank.processingservice.payload.response.TransactionShortResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface AccountFacade {
    Page<PaymentShortResponse> getPayments(UUID accountId,
                                           Pageable pageable,
                                           PaymentAccountType type,
                                           OffsetDateTime from,
                                           OffsetDateTime to);
    Page<TransactionShortResponse> getTransactions(UUID accountId,
                                                   Pageable pageable,
                                                   OffsetDateTime from,
                                                   OffsetDateTime to);
}
