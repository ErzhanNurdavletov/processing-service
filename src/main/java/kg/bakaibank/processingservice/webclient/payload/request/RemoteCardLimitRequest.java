package kg.bakaibank.processingservice.webclient.payload.request;

import java.util.UUID;

public record RemoteCardLimitRequest(
    UUID limitId,
    UUID cardId
) {
}
