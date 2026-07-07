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
    public void fixDatabase() {
        dropOldConstraint();
        updateConstraints();
        recalculateSpecialistReimbursements();
    }

    private void dropOldConstraint() {
        try {
            log.info("Checking database for outdated constraint fkf5jedl4jfcwyr2ubg0legfofo...");
            jdbcTemplate.execute("ALTER TABLE consultation DROP CONSTRAINT IF EXISTS fkf5jedl4jfcwyr2ubg0legfofo");
            log.info("Successfully dropped constraint fkf5jedl4jfcwyr2ubg0legfofo (or it did not exist).");
        } catch (Exception e) {
            log.warn("Unable to drop constraint fkf5jedl4jfcwyr2ubg0legfofo: {}", e.getMessage());
        }
    }

    private void updateConstraints() {
        recreateConstraintWithCascadeBehavior("consultation", "generaliste_id", "medecin", "id", "ON DELETE CASCADE");
        recreateConstraintWithCascadeBehavior("assure", "medecin_traitant_id", "generaliste", "id", "ON DELETE SET NULL");
        recreateConstraintWithCascadeBehavior("medecin", "medecin_traitant_id", "generaliste", "id", "ON DELETE SET NULL");
    }

    private void recreateConstraintWithCascadeBehavior(String tableName, String columnName, String referencedTable, String referencedColumn, String behavior) {
        try {
            log.info("Updating constraint on {}.{} referencing {} with behavior {}", tableName, columnName, referencedTable, behavior);
            String plpgsql = 
                "DO $$\n" +
                "DECLARE\n" +
                "    r RECORD;\n" +
                "BEGIN\n" +
                "    FOR r IN (\n" +
                "        SELECT tc.constraint_name\n" +
                "        FROM information_schema.table_constraints tc\n" +
                "        JOIN information_schema.key_column_usage kcu\n" +
                "          ON tc.constraint_name = kcu.constraint_name\n" +
                "          AND tc.table_schema = kcu.table_schema\n" +
                "        WHERE tc.constraint_type = 'FOREIGN KEY'\n" +
                "          AND tc.table_name = '" + tableName + "'\n" +
                "          AND kcu.column_name = '" + columnName + "'\n" +
                "    ) LOOP\n" +
                "        EXECUTE 'ALTER TABLE " + tableName + " DROP CONSTRAINT ' || quote_ident(r.constraint_name);\n" +
                "    END LOOP;\n" +
                "END $$;";
            
            jdbcTemplate.execute(plpgsql);
            
            String constraintName = "fk_" + tableName + "_" + columnName;
            String alterSql = String.format(
                "ALTER TABLE %s ADD CONSTRAINT %s FOREIGN KEY (%s) REFERENCES %s(%s) %s",
                tableName, constraintName, columnName, referencedTable, referencedColumn, behavior
            );
            jdbcTemplate.execute(alterSql);
            log.info("Successfully recreated constraint {}", constraintName);
        } catch (Exception e) {
            log.warn("Unable to update constraint on {}.{}: {}", tableName, columnName, e.getMessage());
        }
    }

    private void recalculateSpecialistReimbursements() {
        try {
            log.info("Recalculating reimbursement amounts for specialist consultations (80% rate)...");
            String sql = "UPDATE remboursement r " +
                         "SET montant = COALESCE((" +
                         "    SELECT SUM(fm.montant_soin * CASE WHEN s.id IS NOT NULL THEN 0.8 ELSE 1.0 END) " +
                         "    FROM feuille_maladie fm " +
                         "    JOIN consultation c ON fm.consultation_id = c.id " +
                         "    LEFT JOIN specialiste s ON c.generaliste_id = s.id " +
                         "    WHERE fm.remboursement_id = r.id" +
                         "), 0.0) " +
                         "WHERE r.statut IN ('EN_ATTENTE', 'EFFECTUE')";
            int rows = jdbcTemplate.update(sql);
            log.info("Successfully updated {} reimbursement records to respect the 80% specialist rate.", rows);
        } catch (Exception e) {
            log.warn("Unable to recalculate specialist reimbursements: {}", e.getMessage());
        }
    }
}
