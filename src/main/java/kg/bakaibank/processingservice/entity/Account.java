package kg.bakaibank.processingservice.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "account_number", nullable = false, length = 22)
    private String accountNumber;

    @Column(name = "cards")
    private Set<UUID> cards;
}
