package kg.bakaibank.processingservice.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.bakaibank.processingservice.exception.ErrorResponse;
import kg.bakaibank.processingservice.payload.request.PaymentRequest;
import kg.bakaibank.processingservice.payload.response.PaymentResponse;
import kg.bakaibank.processingservice.payload.response.PaymentShortResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Tag(name = "Payments",
    description = "execute payment from card to card")
public interface PaymentControllerApi {

    @Operation(summary = "Make a payment",
        description = "payment from cardId to cardId")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
            description = "successful, limit_exceed, or insufficient-fund status payment",
            content = @Content(schema = @Schema(implementation = PaymentShortResponse.class))),
        @ApiResponse(responseCode = "404",
            description = "one or two cards not found or idempotency-key error",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<PaymentShortResponse> executePayment(
        @Parameter(description = "Idempotency-Key of payment", required = true) UUID idempotencyKey,
        @RequestBody(
            description = "Payment making request", required = true,
            content = @Content(schema = @Schema(
                implementation = PaymentRequest.class))) PaymentRequest request);

    @Operation(summary = "Find payment by id",
        description = "Payment response")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "returns all info about payment by id",
            content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
        @ApiResponse(responseCode = "404",
            description = "Payment not found by id",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<PaymentResponse> findById(
        @Parameter(description = "Payment id (UUID)", required = true) UUID paymentId);
}
