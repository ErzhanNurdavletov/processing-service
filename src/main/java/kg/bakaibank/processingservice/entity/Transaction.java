package kg.bakaibank.processingservice.entity;

import jakarta.persistence.*;
import kg.bakaibank.processingservice.entity.enums.PaymentCurrency;
import kg.bakaibank.processingservice.entity.enums.TransactionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "debit_account_id", nullable = false)
    private Account debitAccount;

    @ManyToOne
    @JoinColumn(name = "credit_account_id", nullable = false)
    private Account creditAccount;

    @Column(name = "amount", scale = 15, precision = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentCurrency currency;

    @Column(name = "status", nullable = false)
    private TransactionStatus status;

    @Column(name = "comment")
    private String comment;
}
