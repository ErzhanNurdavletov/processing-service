package kg.bakaibank.processingservice.webclient.payload.enums;

import lombok.Getter;

@Getter
public enum ClientType {
    INDIVIDUAL("40701"),
    LEGAL("40702");

    private final String code;

    ClientType(String code) {
        this.code = code;
    }
}
