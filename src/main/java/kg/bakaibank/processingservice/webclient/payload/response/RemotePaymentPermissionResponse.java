package kg.bakaibank.processingservice.webclient.payload.response;

public record RemotePaymentPermissionResponse(
    boolean isAllowedByAmount,
    boolean isAllowedByCount
) {
}
