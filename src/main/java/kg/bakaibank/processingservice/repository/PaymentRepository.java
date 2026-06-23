package kg.bakaibank.processingservice.repository;

import kg.bakaibank.processingservice.entity.Payment;
import kg.bakaibank.processingservice.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("""
                SELECT COALESCE(SUM(p.amount), 0)
                FROM Payment p
                WHERE p.debitAccount.id = :accountId
                AND p.status IN :paymentStatuses
                AND p.createdAt >= :from
                AND p.createdAt < :to
        """)
    BigDecimal sumAmountForTodayByAccountId(UUID accountId,
                                            OffsetDateTime from,
                                            OffsetDateTime to,
                                            Set<PaymentStatus> paymentStatuses);

    @Query("""
                    SELECT COUNT(*)
                    FROM Payment p
                    WHERE p.debitAccount.id = :debitAccountId
                    AND p.status IN :paymentStatuses
                    AND p.createdAt >= :from
                    AND p.createdAt < :to
        """)
    Integer countPaymentByDebitAccount(UUID debitAccountId,
                                       OffsetDateTime from,
                                       OffsetDateTime to,
                                       Set<PaymentStatus> paymentStatuses);

    @Query("""
             SELECT p
             FROM Payment p
             WHERE p.debitAccount.id = :debitAccountId
             AND p.createdAt >= :from
             AND p.createdAt < :to
        """)
    Page<Payment> findByDebitAccountId(UUID debitAccountId, OffsetDateTime from,
                                       OffsetDateTime to, Pageable pageable);

    @Query("""
             SELECT p
             FROM Payment p
             WHERE p.creditAccount.id = :creditAccountId
             AND p.createdAt >= :from
             AND p.createdAt < :to
        """)
    Page<Payment> findByCreditAccountId(UUID creditAccountId, OffsetDateTime from,
                                        OffsetDateTime to, Pageable pageable);

    @Query("""
             SELECT p
             FROM Payment p
             WHERE (p.debitAccount.id = :accountId
             OR p.creditAccount.id = :accountId)
             AND p.createdAt >= :from
             AND p.createdAt < :to
        """)
    Page<Payment> findByAccountId(UUID accountId, OffsetDateTime from,
                                  OffsetDateTime to, Pageable pageable);
}
