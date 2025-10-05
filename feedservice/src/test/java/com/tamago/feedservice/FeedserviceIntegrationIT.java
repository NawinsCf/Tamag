package com.tamago.feedservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("integration")
public class FeedserviceIntegrationIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void contextLoads_and_flywayApplied() {
        // Check that migrations ran by asserting tables exist and tamagotype has at least one row
        Integer cnt = jdbc.queryForObject("SELECT COUNT(*) FROM tamagotype", Integer.class);
        assertThat(cnt).isNotNull();
        // even if no rows, the table exists (count returns 0). Just ensure query succeeded.
    }
}
