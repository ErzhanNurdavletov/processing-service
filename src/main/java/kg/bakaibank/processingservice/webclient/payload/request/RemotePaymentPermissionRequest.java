package kg.bakaibank.processingservice.webclient.payload.request;

import java.math.BigDecimal;

public record RemotePaymentPermissionRequest(
    BigDecimal todayPaymentAmount,
    int todayPaymentCount
) {
}
