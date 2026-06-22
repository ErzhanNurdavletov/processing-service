package kg.bakaibank.processingservice.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record AccountCreateRequest(

    @NotNull(message = "clientId can't be null")
    UUID clientId,

    @NotNull(message = "accountNumber can't be null")
    @Pattern(regexp = "^\\d{22}$",
        message = "accountNumber must be unique, 22 digit length")
    String accountNumber
) {
}
