package kg.bakaibank.processingservice.webclient;

import kg.bakaibank.processingservice.exception.ErrorResponse;
import kg.bakaibank.processingservice.exception.custom.RemoteLimitServiceException;
import kg.bakaibank.processingservice.webclient.payload.response.RemoteClientResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClientWebClient {

    private final WebClient webClient;

    public void checkIfClientByIdExists(UUID clientId) {
        webClient.get()
            .uri("/clients/{clientId}", clientId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                .flatMap(errorBody -> Mono.error(new RemoteLimitServiceException(
                    errorBody.getMessage(),
                    HttpStatus.resolve(errorBody.getStatus())
                ))))
            .bodyToMono(RemoteClientResponse.class)
            .block();
    }
}
