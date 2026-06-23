package kg.bakaibank.processingservice.repository;

import jakarta.persistence.LockModeType;
import kg.bakaibank.processingservice.entity.Account;
import kg.bakaibank.processingservice.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    @Query("""
    SELECT a
         FROM Account a
              WHERE a.id = :accountId
    """)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByIdForUpdate(UUID accountId);

    Optional<Account> findByClientId(UUID clientId);
}
