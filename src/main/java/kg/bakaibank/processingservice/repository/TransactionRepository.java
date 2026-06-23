package kg.bakaibank.processingservice.repository;

import kg.bakaibank.processingservice.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("""
             SELECT t
              FROM Transaction t
               WHERE (t.creditAccount.id = :accountId
                OR t.debitAccount.id = :accountId)
                         AND t.createdAt >= :from
                                  AND t.createdAt < :to
        """)
    Page<Transaction> getTransactionsPage(UUID accountId,
                                          Pageable pageable,
                                          OffsetDateTime from,
                                          OffsetDateTime to);
}
