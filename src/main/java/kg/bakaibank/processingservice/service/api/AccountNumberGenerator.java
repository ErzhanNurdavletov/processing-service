package kg.bakaibank.processingservice.service.api;

import kg.bakaibank.processingservice.entity.enums.PaymentCurrency;
import kg.bakaibank.processingservice.webclient.payload.enums.ClientType;

public interface AccountNumberGenerator {
    String generate(ClientType clientType, PaymentCurrency currency);
}
