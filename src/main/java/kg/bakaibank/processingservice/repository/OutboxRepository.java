package kg.bakaibank.processingservice.repository;

import kg.bakaibank.processingservice.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<Outbox, UUID> {

    @Query("""
        SELECT o
                 FROM Outbox o
                          WHERE o.publishedAt IS NULL
        """)
    Set<Outbox> findAllByPublishedAtIsNull();
}
