package kg.bakaibank.processingservice.payload.request;

import java.util.UUID;

public record AccountCreateRequest(
    UUID clientId,
    String accountNumber
) {
}
