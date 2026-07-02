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
import kg.bakaibank.processingservice.payload.enums.PaymentAccountType;
import kg.bakaibank.processingservice.payload.request.AccountCreateRequest;
import kg.bakaibank.processingservice.payload.response.*;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

@Tag(name = "Accounts",
    description = "Create, find account, get specified accounts payments and transactions")
public interface AccountControllerApi {

    @Operation(summary = "Create an account",
        description = "create account with balance, linked to specified client")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201",
            description = "Created account",
            content = @Content(schema = @Schema(implementation = AccountShortResponse.class))),
        @ApiResponse(responseCode = "404",
            description = "Created account",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<AccountShortResponse> createAccount(
        @RequestBody(
            description = "Account creation request",
            required = true,
            content = @Content(
                schema = @Schema(
                    implementation = AccountCreateRequest.class))) AccountCreateRequest request);


    @Operation(summary = "Get balance of specified account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Account is found, return balance",
            content = @Content(schema = @Schema(implementation = AccountBalanceResponse.class))),
        @ApiResponse(responseCode = "404",
            description = "Account by id not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<AccountBalanceResponse> getBalance(
        @Parameter(description = "Account id (UUID)", required = true) UUID accountId);


    @Operation(summary = "Get accounts's payments(page)" +
        " in specified period" +
        " with sorting(default sorting: createdAt ASC" +
        " accountType: DEBIT OR CREDIT (default: All)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Return account's payments",
            content = @Content(schema = @Schema(implementation = PaymentShortResponse.class))),
        @ApiResponse(responseCode = "404",
            description = "Account by id not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404",
            description = "Missing request params(sorting, accountType)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404",
            description = "Sorting params are not valid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Page<PaymentShortResponse>> getPaymentsPage(
        @Parameter(description = "Account id (UUID)", required = true) UUID accountId,
        @ParameterObject @Parameter(description = """
        Pagination parameters.
        - page: page index (0..N)
        - size: page size
        - sort: sorting, e.g. createdAt,desc
        """) Pageable pageable,
        @Parameter(description = "payments type: DEBIT, CREDIT") PaymentAccountType accountType,
        @Parameter(description = "from(time in OffsetDateTime", required = true) OffsetDateTime from,
        @Parameter(description = "to(time in OffsetDateTime", required = true) OffsetDateTime to);


    @Operation(summary = "Get accounts's transactions(page)" +
        " in specified period" +
        " with sorting(default sorting: createdAt ASC")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "Return account's payments",
            content = @Content(schema = @Schema(implementation = TransactionShortResponse.class))),
        @ApiResponse(responseCode = "404",
            description = "Account by id not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404",
            description = "Sorting properties not valid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Page<TransactionShortResponse>> getTransactionsPage(
        @Parameter(description = "Account id (UUID)", required = true) UUID accountId,
        @ParameterObject @Parameter(description = """
        Pagination parameters.
        - page: page index (0..N)
        - size: page size
        - sort: sorting, e.g. createdAt,desc
        """) Pageable pageable,
        @Parameter(description = "from(time in OffsetDateTime", required = true) OffsetDateTime from,
        @Parameter(description = "to(time in OffsetDateTime", required = true) OffsetDateTime to);



    @Operation(summary = "Check does account exist",
        description = "return true/false of account existence")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "return boolean of account existence",
            content = @Content(schema = @Schema(implementation = AccountExistsResponse.class)))
    })
    ResponseEntity<AccountExistsResponse> doesAccountExist(
        @Parameter(description = "Account id (UUID)", required = true) UUID accountId);


    @Operation(summary = "return info account response",
        description = "return true/false of account existence")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            description = "returns account response",
            content = @Content(schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "404",
            description = "Account by id not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<AccountResponse> findById(
        @Parameter(description = "Account id (UUID)", required = true) UUID accountId);
}
