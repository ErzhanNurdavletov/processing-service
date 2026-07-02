package kg.bakaibank.processingservice.service.implementation;

import kg.bakaibank.processingservice.entity.enums.PaymentCurrency;
import kg.bakaibank.processingservice.repository.AccountNumberSequenceJdbcRepository;
import kg.bakaibank.processingservice.service.api.AccountNumberGenerator;
import kg.bakaibank.processingservice.webclient.payload.enums.ClientType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultAccountNumberGenerator implements AccountNumberGenerator {

    private static final String BRANCH_NUMBER = "0001";

    private final AccountNumberSequenceJdbcRepository accountNumberSequenceJdbcRepository;

    @Override
    @Transactional
    public String generate(ClientType clientType, PaymentCurrency currency) {

        StringBuilder accountNumber = new StringBuilder();
        accountNumber.append(clientType.getCode());   // size = 5
        accountNumber.append(currency.getCode());     // size = 3
        accountNumber.append(BRANCH_NUMBER);           // size = 4
        String uniqueSequenceId = String.format("%08d", getNextSequenceValue());
        accountNumber.append(uniqueSequenceId);       // size = 8
        return accountNumber.toString();
    }

    private long getNextSequenceValue() {
        return accountNumberSequenceJdbcRepository.getNextValue();
    }
}
