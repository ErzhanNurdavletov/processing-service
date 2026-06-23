package kg.bakaibank.processingservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(of = {"id", "clientId", "accountNumber", "balance"})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "creditAccount")
    private Set<Payment> creditPayments;

    @OneToMany(mappedBy = "debitAccount")
    private Set<Payment> debitPayments;

    @OneToMany(mappedBy = "creditAccount")
    private Set<Transaction> creditTransactions;

    @OneToMany(mappedBy = "debitAccount")
    private Set<Transaction> debitTransactions;

    @Column(name = "client_id", nullable = false, unique = true)
    private UUID clientId;

    @Column(name = "account_number", nullable = false, unique = true, length = 22)
    private String accountNumber;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "opened_at")
    private OffsetDateTime openedAt;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @Column(name = "ended_at")
    private OffsetDateTime endedAt;
}
