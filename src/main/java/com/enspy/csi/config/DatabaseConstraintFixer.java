package com.enspy.csi.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Automatically drops the legacy constraint fkf5jedl4jfcwyr2ubg0legfofo on startup
 * if it exists in the database. This allows consultations to be saved with specialist
 * IDs (which aren't present in the generaliste table).
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseConstraintFixer {

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void dropOldConstraint() {
        try {
            log.info("Checking database for outdated constraint fkf5jedl4jfcwyr2ubg0legfofo...");
            jdbcTemplate.execute("ALTER TABLE consultation DROP CONSTRAINT IF EXISTS fkf5jedl4jfcwyr2ubg0legfofo");
            log.info("Successfully dropped constraint fkf5jedl4jfcwyr2ubg0legfofo (or it did not exist).");
        } catch (Exception e) {
            log.warn("Unable to drop constraint fkf5jedl4jfcwyr2ubg0legfofo: {}", e.getMessage());
        }
    }
}
