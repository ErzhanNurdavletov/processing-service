package kg.bakaibank.processingservice.service.implementation.facade;

import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.Transaction;
import kg.bakaibank.processingservice.entity.enums.PaymentCurrency;
import kg.bakaibank.processingservice.entity.enums.PaymentDeclineReason;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;
import kg.bakaibank.processingservice.mapper.PaymentMapper;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
import kg.bakaibank.processingservice.service.api.service.AccountService;
import kg.bakaibank.processingservice.service.api.service.OutboxService;
import kg.bakaibank.processingservice.service.api.service.PaymentService;
import kg.bakaibank.processingservice.service.api.service.TransactionService;
import kg.bakaibank.processingservice.webclient.CardWebClient;
import kg.bakaibank.processingservice.webclient.payload.enums.CardStatus;
import kg.bakaibank.processingservice.webclient.payload.request.RemotePaymentPermissionRequest;
import kg.bakaibank.processingservice.webclient.payload.response.RemoteCardLimitResponse;
import kg.bakaibank.processingservice.webclient.payload.response.RemoteCardResponse;
import kg.bakaibank.processingservice.webclient.payload.response.RemotePaymentPermissionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
public class DefaultPaymentFacadeTest {

    @Mock
    private PaymentService paymentService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private CardWebClient cardWebClient;
    @Mock
    private AccountService accountService;
    @Mock
    private OutboxService outboxService;
    @Mock
    private PaymentMapper paymentMapper;
    @InjectMocks
    private DefaultPaymentFacade paymentFacade;

    @Test
    public void executePayment_validRequest_successfulPayment() {
        // ARRANGE
        UUID sourceCardId = UUID.randomUUID();
        UUID destinationCardId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal(100);
        PaymentRequest request = new PaymentRequest(sourceCardId, destinationCardId, amount,
            PaymentCurrency.SOM, "test-payment");

        RemoteCardResponse sourceCard =
            new RemoteCardResponse(sourceCardId, null, null, null,
            CardStatus.ACTIVE, null, null, null, UUID.randomUUID());
        RemoteCardResponse destinationCard =
            new RemoteCardResponse(destinationCardId, null, null, null,
            CardStatus.ACTIVE, null, null, null, UUID.randomUUID());

        Account debit = Account.builder().id(sourceCard.accountId()).balance(new BigDecimal(1000)).build();
        Account credit = Account.builder().id(destinationCard.accountId()).balance(BigDecimal.ZERO).build();
        Account transit = Account.builder().balance(BigDecimal.ZERO).build();

        Payment payment = Payment.builder().id(UUID.randomUUID()).build();
        PaymentShortResponse expectedResponse =
            new PaymentShortResponse(payment.getId(), amount, null, null);

        Mockito.when(cardWebClient.getCardById(sourceCardId)).thenReturn(sourceCard);
        Mockito.when(cardWebClient.getCardById(destinationCardId)).thenReturn(destinationCard);
        Mockito.when(paymentService.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        Mockito.when(accountService.findByIdForUpdate(sourceCard.accountId())).thenReturn(debit);
        Mockito.when(accountService.findByIdForUpdate(destinationCard.accountId())).thenReturn(credit);
        Mockito.when(paymentService.openPayment(request, debit, credit, null)).thenReturn(payment);

        Mockito.when(paymentService.countTodayPaymentSum(debit.getId())).thenReturn(BigDecimal.ZERO);
        Mockito.when(paymentService.countTodayPayments(debit.getId())).thenReturn(0);

        List<RemoteCardLimitResponse> limits = List.of(
            new RemoteCardLimitResponse(UUID.randomUUID(),
                "transfer-limit", null, 0)
        );
        Mockito.when(cardWebClient.getCardCurrentLimits(sourceCardId)).thenReturn(limits);
        Mockito.when(cardWebClient.checkIfPaymentNotExceedLimit(
                eq(sourceCardId), any(UUID.class), any(RemotePaymentPermissionRequest.class)))
            .thenReturn(new RemotePaymentPermissionResponse(true, true));

        Mockito.when(accountService.getBankTransitAccount()).thenReturn(transit);
        Transaction debitToTransit = Transaction.builder().build();
        Transaction transitToCredit = Transaction.builder().build();
        Mockito.when(transactionService.initTransaction(eq(request), eq(debit), eq(transit), eq(payment)))
            .thenReturn(debitToTransit);
        Mockito.when(transactionService.initTransaction(eq(request), eq(transit), eq(credit), eq(payment)))
            .thenReturn(transitToCredit);

        Mockito.when(paymentMapper.toShortResponse(payment)).thenReturn(expectedResponse);

        // ACT
        PaymentShortResponse actualResponse = paymentFacade.executePayment(request, null);

        // ASSERT
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedResponse, actualResponse);
        Assertions.assertEquals(amount, actualResponse.amount());
        Assertions.assertTrue(debit.getBalance().compareTo(new BigDecimal(0)) >= 0);

        Mockito.verify(transactionService, Mockito.times(2)).closeTransaction(any());
        Mockito.verify(paymentService).completePayment(payment);
        Mockito.verify(outboxService).createOutbox(payment);
    }

    @Test
    public void executePayment_validRequest_limitExceed() {
        // ARRANGE
        UUID sourceCardId = UUID.randomUUID();
        UUID destinationCardId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal(100);
        PaymentRequest request = new PaymentRequest(sourceCardId, destinationCardId, amount,
            PaymentCurrency.SOM, "test-payment");

        RemoteCardResponse sourceCard = new RemoteCardResponse(sourceCardId, null,
            null, null, CardStatus.ACTIVE, null, null,
                null, UUID.randomUUID());
        RemoteCardResponse destinationCard = new RemoteCardResponse(destinationCardId, null,
            null, null, CardStatus.ACTIVE, null, null,
            null, UUID.randomUUID());

        Account debit = Account.builder()
            .id(sourceCard.accountId())
            .balance(new BigDecimal(1000))
            .build();
        Account credit = Account.builder()
            .id(destinationCard.accountId())
            .build();
        UUID paymentId = UUID.randomUUID();
        Payment payment = Payment.builder().id(paymentId).build();


        BigDecimal sumForToday = new BigDecimal(190_900);
        BigDecimal sumForTodayTotal = sumForToday.add(amount);
        int countForToday = 2_000;
        RemotePaymentPermissionRequest permissionRequest =
            new RemotePaymentPermissionRequest(sumForTodayTotal, countForToday);

        UUID limitId = UUID.randomUUID();
        List<RemoteCardLimitResponse> cardCurrentLimitsResponses =
            List.of(new RemoteCardLimitResponse(limitId,
                "transfer-limit", new BigDecimal(200_000), 2_000));

        RemotePaymentPermissionResponse permissionResponse =
            new RemotePaymentPermissionResponse(false, false);

        PaymentShortResponse expectedResponse =
            new PaymentShortResponse(payment.getId(), amount,
                PaymentStatus.DECLINED, PaymentDeclineReason.LIMIT_EXCEEDED);

        Mockito.when(paymentService.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        Mockito.when(cardWebClient.getCardById(eq(sourceCardId))).thenReturn(sourceCard);
        Mockito.when(cardWebClient.getCardById(eq(destinationCardId))).thenReturn(destinationCard);

        Mockito.when(accountService.findByIdForUpdate(eq(sourceCard.accountId()))).thenReturn(debit);
        Mockito.when(accountService.findByIdForUpdate(eq(destinationCard.accountId()))).thenReturn(credit);
        Mockito.when(paymentService.openPayment(eq(request), eq(debit), eq(credit), any(UUID.class))).thenReturn(payment);

        Mockito.when(paymentService.countTodayPaymentSum(eq(debit.getId()))).thenReturn(sumForToday);
        Mockito.when(paymentService.countTodayPayments(eq(debit.getId()))).thenReturn(countForToday);
        Mockito.when(cardWebClient.getCardCurrentLimits(eq(sourceCardId))).thenReturn(cardCurrentLimitsResponses);

        Mockito.when(cardWebClient.checkIfPaymentNotExceedLimit
            (eq(sourceCardId), eq(limitId), eq(permissionRequest)))
            .thenReturn(permissionResponse);
        Mockito.when(paymentMapper.toShortResponse(eq(payment))).thenReturn(expectedResponse);

        // ACT
        PaymentShortResponse actualResponse = paymentFacade.executePayment(request, UUID.randomUUID());

        // ASSERT
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedResponse, actualResponse);

        Mockito.verify(paymentService).declinePayment(payment, PaymentDeclineReason.LIMIT_EXCEEDED);
        Mockito.verify(outboxService).createOutbox(payment);
        Mockito.verify(paymentService, Mockito.never()).completePayment(payment);
        Mockito.verify(paymentMapper).toShortResponse(payment);
    }

    @Test
    public void executePayment_validRequest_insufficientFunds() {
        // ARRANGE
        UUID sourceCardId = UUID.randomUUID();
        UUID destinationCardId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal(80_000);
        PaymentRequest request = new PaymentRequest(sourceCardId, destinationCardId, amount,
            PaymentCurrency.SOM, "test-payment");

        RemoteCardResponse sourceCard = new RemoteCardResponse(sourceCardId, null,
            null, null, CardStatus.ACTIVE, null, null,
            null, UUID.randomUUID());
        RemoteCardResponse destinationCard = new RemoteCardResponse(destinationCardId, null,
            null, null, CardStatus.ACTIVE, null, null,
            null, UUID.randomUUID());

        Account debit = Account.builder()
            .id(sourceCard.accountId())
            .balance(new BigDecimal(0))
            .build();
        Account credit = Account.builder()
            .id(destinationCard.accountId())
            .build();
        UUID paymentId = UUID.randomUUID();
        Payment payment = Payment.builder().id(paymentId).build();

        PaymentShortResponse expectedResponse =
            new PaymentShortResponse(payment.getId(), amount,
                PaymentStatus.DECLINED, PaymentDeclineReason.INSUFFICIENT_FUNDS);

        Mockito.when(paymentService.findByIdempotencyKey(any())).thenReturn(Optional.empty());
        Mockito.when(cardWebClient.getCardById(eq(sourceCardId))).thenReturn(sourceCard);
        Mockito.when(cardWebClient.getCardById(eq(destinationCardId))).thenReturn(destinationCard);

        Mockito.when(accountService.findByIdForUpdate(eq(sourceCard.accountId()))).thenReturn(debit);
        Mockito.when(accountService.findByIdForUpdate(eq(destinationCard.accountId()))).thenReturn(credit);
        Mockito.when(paymentService.openPayment(eq(request), eq(debit), eq(credit), any(UUID.class))).thenReturn(payment);

        Mockito.when(paymentMapper.toShortResponse(eq(payment))).thenReturn(expectedResponse);

        // ACT
        PaymentShortResponse actualResponse = paymentFacade.executePayment(request, UUID.randomUUID());

        // ASSERT
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(PaymentStatus.DECLINED, actualResponse.status());
        Assertions.assertEquals(PaymentDeclineReason.INSUFFICIENT_FUNDS, actualResponse.declineReason());
        Assertions.assertEquals(amount, actualResponse.amount());

        Mockito.verify(paymentService).declinePayment(payment, PaymentDeclineReason.INSUFFICIENT_FUNDS);
        Mockito.verify(outboxService).createOutbox(payment);
        Mockito.verify(paymentService, Mockito.never()).completePayment(payment);
        Mockito.verify(paymentMapper).toShortResponse(payment);
    }
}
