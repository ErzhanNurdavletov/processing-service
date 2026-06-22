package kg.bakaibank.processingservice.webclient;

import kg.bakaibank.processingservice.exception.ErrorResponse;
import kg.bakaibank.processingservice.exception.custom.RemoteLimitServiceException;
import kg.bakaibank.processingservice.webclient.payload.request.RemotePaymentPermissionRequest;
import kg.bakaibank.processingservice.webclient.payload.response.RemoteCardLimitResponse;
import kg.bakaibank.processingservice.webclient.payload.response.RemoteCardResponse;
import kg.bakaibank.processingservice.webclient.payload.response.RemotePaymentPermissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CardWebClient {

    private final WebClient webClient;

    public RemoteCardResponse getCardById(UUID cardId) {
        return webClient.get()
            .uri("/cards/{cardId}", cardId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                .flatMap(errorBody -> Mono.error(new RemoteLimitServiceException(
                    errorBody.getMessage(),
                    HttpStatus.resolve(errorBody.getStatus())
                ))))
            .bodyToMono(RemoteCardResponse.class)
            .block();
    }

    public List<RemoteCardLimitResponse> getCardCurrentLimits(UUID cardId) {
        return webClient.get()
            .uri("/cards/{cardId}/current-limits", cardId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToFlux(RemoteCardLimitResponse.class)
            .collectList()
            .block();
    }

    public RemotePaymentPermissionResponse checkIfPaymentNotExceedLimit(UUID cardId,
                                                                        UUID limitId,
                                              RemotePaymentPermissionRequest request) {
        return webClient.post()
            .uri("/cards/{cardId}/limits/{limitId}/limit-check", cardId, limitId)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(RemotePaymentPermissionResponse.class)
            .block();
    }
}
