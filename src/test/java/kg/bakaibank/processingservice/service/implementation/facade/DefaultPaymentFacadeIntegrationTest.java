package kg.bakaibank.processingservice.service.implementation.facade;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.enums.PaymentCurrency;
import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
import kg.bakaibank.processingservice.repository.AccountRepository;
import kg.bakaibank.processingservice.webclient.CardWebClient;
import kg.bakaibank.processingservice.webclient.payload.enums.CardStatus;
import kg.bakaibank.processingservice.webclient.payload.response.RemoteCardLimitResponse;
import kg.bakaibank.processingservice.webclient.payload.response.RemoteCardResponse;
import kg.bakaibank.processingservice.webclient.payload.response.RemotePaymentPermissionResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
@Testcontainers
@Slf4j
public class DefaultPaymentFacadeIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private DefaultPaymentFacade defaultPaymentFacade;
    @Autowired
    private AccountRepository accountRepository;
    @MockBean
    private CardWebClient cardWebClient;

    @Test
    void executePayment_concurrentRequests_onlyOneSucceeds() throws InterruptedException {
        UUID sourceCardId = UUID.randomUUID();
        UUID destinationCardId = UUID.randomUUID();

        Account debit = accountRepository.save(Account.builder()
            .clientId(UUID.randomUUID())
            .accountNumber("11111")
            .balance(new BigDecimal("1000"))
            .build());

        Account credit = accountRepository.save(Account.builder()
            .clientId(UUID.randomUUID())
            .accountNumber("22222")
            .balance(BigDecimal.ZERO)
            .build());

        RemoteCardResponse sourceCard = new RemoteCardResponse(
            sourceCardId, null, null, null, CardStatus.ACTIVE, null, null, null, debit.getId());
        RemoteCardResponse destinationCard = new RemoteCardResponse(
            destinationCardId, null, null, null, CardStatus.ACTIVE, null, null, null, credit.getId());

        UUID limitId = UUID.randomUUID();
        List<RemoteCardLimitResponse> limits = List.of(
            new RemoteCardLimitResponse(limitId, "transfer-limit", new BigDecimal("20000"), 2000)
        );
        RemotePaymentPermissionResponse permission = new RemotePaymentPermissionResponse(true, true);

        Mockito.when(cardWebClient.getCardById(sourceCardId)).thenReturn(sourceCard);
        Mockito.when(cardWebClient.getCardById(destinationCardId)).thenReturn(destinationCard);
        Mockito.when(cardWebClient.getCardCurrentLimits(sourceCardId)).thenReturn(limits);
        Mockito.when(cardWebClient.checkIfPaymentNotExceedLimit(eq(sourceCardId), eq(limitId), any()))
            .thenReturn(permission);

        int threadCount = 2;
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        List<PaymentShortResponse> results = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    PaymentRequest request = new PaymentRequest(
                        sourceCardId, destinationCardId,
                        new BigDecimal("700"), PaymentCurrency.SOM, null
                    );
                    readyLatch.countDown();
                    startLatch.await();
                    log.info("executePayment() called at: {} in thread: {}",
                        LocalDateTime.now(), Thread.currentThread().getName());
                    PaymentShortResponse response =
                        defaultPaymentFacade.executePayment(request, UUID.randomUUID());
                    results.add(response);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }

        readyLatch.await();
        startLatch.countDown();
        boolean finished = doneLatch.await(15, TimeUnit.SECONDS);

        assertTrue(finished);
        assertEquals(2, results.size());

        long completed = results.stream()
            .filter(response -> response.status() == PaymentStatus.COMPLETED).count();
        long declined = results.stream()
            .filter(response -> response.status() == PaymentStatus.DECLINED).count();

        assertEquals(1, completed, "Only one payment can be COMPLETED");
        assertEquals(1, declined, "Only one payment can be DECLINED");

        results.stream()
            .filter(r -> r.status() == PaymentStatus.DECLINED)
            .findFirst()
            .ifPresent(r ->
                assertEquals(PaymentDeclineReason.INSUFFICIENT_FUNDS, r.declineReason())
            );

        Account updatedDebit = accountRepository.findById(debit.getId()).orElseThrow();
        Account updatedCredit = accountRepository.findById(credit.getId()).orElseThrow();

        UUID transitId = UUID.fromString("fb3cdca1-b5a9-4662-a248-f527d781f364");
        Account updatedTransit = accountRepository.findById(transitId).orElseThrow();

        assertEquals(new BigDecimal(300), updatedDebit.getBalance());
        assertEquals(new BigDecimal(700), updatedCredit.getBalance());
        assertEquals(BigDecimal.ZERO, updatedTransit.getBalance());
    }
}
