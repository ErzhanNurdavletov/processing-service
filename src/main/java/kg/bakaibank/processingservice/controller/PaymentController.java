package kg.bakaibank.processingservice.controller;

import jakarta.validation.Valid;
import kg.bakaibank.processingservice.controller.api.PaymentControllerApi;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentResponse;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
import kg.bakaibank.processingservice.service.api.facade.PaymentFacade;
import kg.bakaibank.processingservice.service.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentControllerApi {

    private final PaymentFacade paymentFacade;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentShortResponse> executePayment(@RequestHeader(name = "Idempotency-Key") UUID idempotencyKey,
                                            @Valid @RequestBody PaymentRequest request) {
        PaymentShortResponse response = paymentFacade.executePayment(request, idempotencyKey);
        log.info("POST /api/v1/payments - executePayment response={}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable UUID paymentId) {
        PaymentResponse response = paymentService.findById(paymentId);
        log.info("GET /api/v1/payments/{paymentId} - findById response={}", response);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
