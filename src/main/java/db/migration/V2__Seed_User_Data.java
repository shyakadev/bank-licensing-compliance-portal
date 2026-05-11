package db.migration;

import java.sql.PreparedStatement;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import gov.bnr.licensing_compliance_service.auth.enums.UserRole;
import gov.bnr.licensing_compliance_service.auth.enums.UserType;

public class V2__Seed_User_Data extends BaseJavaMigration {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public void migrate(Context context) throws Exception {
        String hash = encoder.encode("password123");

        try (PreparedStatement statement = context.getConnection()
            .prepareStatement("INSERT INTO users (username, email, password_hash, full_name, role, user_type, position) " +
                "VALUES (?, ?, ?, ?, ?::user_role, ?::user_type, ?)")) {
            insertUser(statement, "admin", "d.john@bnr.rw", hash, "John Doe", UserRole.ADMIN, UserType.STAFF, "IT Admin");
            insertUser(statement, "reviewer", "m.eric@bnr.rw", hash, "Eric Marc", UserRole.REVIEWER, UserType.STAFF, "Analyst");
            insertUser(statement, "approver", "b.rebecca@bnr.rw", hash, "Rebecca Brenda", UserRole.APPROVER, UserType.STAFF, "Sr Analyst");
            insertUser(statement, "applicant", "g.mary@bnr.rw", hash, "Mary Grace", UserRole.APPLICANT, UserType.APPLICANT, "CFO");

            statement.executeBatch();
        }
    }

    private void insertUser(PreparedStatement st, String user, String email, String hash, String name, UserRole role, UserType type, String pos)
        throws Exception {
        st.setString(1, user);
        st.setString(2, email);
        st.setString(3, hash);
        st.setString(4, name);
        st.setString(5, String.valueOf(role));
        st.setString(6, String.valueOf(type));
        st.setString(7, pos);
        st.addBatch();
    }
}
