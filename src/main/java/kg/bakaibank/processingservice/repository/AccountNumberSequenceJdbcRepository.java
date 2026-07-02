package kg.bakaibank.processingservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccountNumberSequenceJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    public Long getNextValue() {
        String query = """
            SELECT nextval('accounts_numbers_sequence')
            """;
        return jdbcTemplate.queryForObject(query, Long.class);
    }
}
