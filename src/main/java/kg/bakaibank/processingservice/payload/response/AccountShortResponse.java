package kg.bakaibank.processingservice.payload.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Response of short info of account")
public record AccountShortResponse(
    UUID accountId
) {
}
