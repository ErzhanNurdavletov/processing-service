package kg.bakaibank.processingservice.webclient.payload.response;

import kg.bakaibank.processingservice.webclient.payload.enums.ClientType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RemoteClientResponse(
    UUID id,
    String firstname,
    String lastname,
    String patronymic,
    ClientType type,
    OffsetDateTime createdAt,
    OffsetDateTime deletedAt,
    String phoneNumber
) {
}
