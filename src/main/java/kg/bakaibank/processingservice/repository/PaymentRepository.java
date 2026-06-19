package kg.bakaibank.processingservice.repository;

import kg.bakaibank.processingservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("SELECT SUM(p.amount)" +
        " FROM Payment p" +
        " WHERE p.debitAccount.id = :accountId" +
        " AND p.createdAt >= :from" +
        " AND p.createdAt < :to")
    BigDecimal sumAmountForTodayByAccountId(UUID accountId, OffsetDateTime from, OffsetDateTime to);
}
