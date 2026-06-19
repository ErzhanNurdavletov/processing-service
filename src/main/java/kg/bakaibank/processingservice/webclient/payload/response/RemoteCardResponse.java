package kg.bakaibank.processingservice.webclient.payload.response;

import kg.bakaibank.processingservice.webclient.payload.enums.CardStatus;
import kg.bakaibank.processingservice.webclient.payload.enums.CardType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RemoteCardResponse(
    UUID id,
    UUID clientId,
    String maskedPan,
    CardType type,
    CardStatus status,
    OffsetDateTime openedAt,
    OffsetDateTime closedAt,
    String cardIssueTypeName,
    UUID accountId
) {
}
