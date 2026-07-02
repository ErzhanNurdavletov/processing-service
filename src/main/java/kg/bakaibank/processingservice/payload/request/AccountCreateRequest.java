package kg.bakaibank.processingservice.payload.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

@Schema(description = "Request to create account")
public record AccountCreateRequest(

    @Schema(description = "clientId who account will belong to",
        requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "clientId can't be null")
    UUID clientId

//    @Schema(description = "Unique number of account",
//        requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 22)
//    @NotNull(message = "accountNumber can't be null")
//    @Pattern(regexp = "^\\d{22}$",
//        message = "accountNumber must be unique, 22 digit length")
//    String accountNumber
) {
}
