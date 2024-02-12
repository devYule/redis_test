package redis.redistest.databaselock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Supplier;

@Slf4j
@Component
public class DatabaseNamedLockWhenUserCallbackPattern {
    // 아직 JPA 를 사용하지 않고있으니 JDBC 로 직접 수행.
    // JdbcTemplate 은 conn 을 반납하는 문제가 있음
    // conn 을 내 마음대로 닫을  수 있는 JDBC 를 사용.
    public static final String GET_LOCK = "SELECT GET_LOCK(?, ?)";
    public static final String RELEASE_LOCK = "SELECT RELEASE_LOCK(?)";
    public static final String EX_MESSAGE = "LOCK FAIL";

    private final DataSource datasource;

    public DatabaseNamedLockWhenUserCallbackPattern(DataSource datasource) {
        this.datasource = datasource;
    }

    public <T> T executeWithLock(String userLockName,
                                 int timeoutSeconds,
                                 Supplier<T> supplier) {
        try (Connection conn = datasource.getConnection()) {

            try {
                log.debug("DatabaseNamedLockWhenUserCallbackPattern.executeWithLock()");
                getLock(conn, userLockName, timeoutSeconds);
                // callback pattern
                return supplier.get();
            } finally {
                releaseLock(conn, userLockName);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void getLock(Connection conn, String userLockName, int timeoutSeconds) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(GET_LOCK)) {
            ps.setString(1, userLockName);
            ps.setInt(2, timeoutSeconds);
            checkResultSet(userLockName, ps, "GetLock_");
        }
    }

    private void releaseLock(Connection conn, String userLockName) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(RELEASE_LOCK)) {
            ps.setString(1, userLockName);
            checkResultSet(userLockName, ps, "ReleaseLock_");
        }
    }

    private void checkResultSet(String userLockName,
                                PreparedStatement ps,
                                String type) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                throw new RuntimeException(EX_MESSAGE);
            }
            if (rs.getInt(1) != 1) {
                throw new RuntimeException(EX_MESSAGE);
            }

        }
    }
}
