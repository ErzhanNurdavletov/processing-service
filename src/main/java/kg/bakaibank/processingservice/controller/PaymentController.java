package kg.bakaibank.processingservice.controller;

import jakarta.validation.Valid;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentResponse;
import kg.bakaibank.processingservice.service.PaymentFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentFacade paymentFacade;
    @PostMapping
    public ResponseEntity<?> executePayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentFacade.executePayment(request);
        log.info("POST /api/v1/payments - executePayment response={}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
