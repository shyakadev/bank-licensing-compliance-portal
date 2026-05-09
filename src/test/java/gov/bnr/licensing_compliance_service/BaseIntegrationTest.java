package gov.bnr.licensing_compliance_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public abstract class BaseIntegrationTest {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected int countRowsTable(String tableName) {
        return JdbcTestUtils.countRowsInTable(jdbcTemplate, tableName);
    }

    protected Integer getApplicationVersion(Long applicantId) {
        return jdbcTemplate.queryForObject("SELECT VERSION FROM APPLICATIONS WHERE ID = ?", Integer.class, applicantId);
    }
}
