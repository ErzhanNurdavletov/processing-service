package kg.bakaibank.processingservice.entity.enums;

import lombok.Getter;

@Getter
public enum PaymentCurrency {
    SOM("417");

    private final String code;

    PaymentCurrency(String code) {
        this.code = code;
    }
}
