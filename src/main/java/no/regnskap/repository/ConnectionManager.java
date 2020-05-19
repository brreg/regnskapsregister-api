package no.regnskap.repository;

import no.regnskap.PostgresProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import java.sql.*;


@Component
public class ConnectionManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

	public static final String DB        = "postgres";
	public static final String DB_SCHEMA = "rreg";

	@Inject
	PostgresProperties postgresProperties;


	public Connection getConnection() throws SQLException {
		return getConnection(false);
	}

	public Connection getConnection(final boolean requireDboPermissions) throws SQLException {
		try {
			String username = null;
			String password = null;
			if (requireDboPermissions) {
				username = postgresProperties.getDboUser();
				password = postgresProperties.getDboPassword();
			}
			if (username==null) {
				username = postgresProperties.getUser();
				password = postgresProperties.getPassword();
			}

			if (postgresProperties.getDbUrl()==null || username==null || password==null) {
				throw new RuntimeException("System environment variable RRAPI_POSTGRES_DB_URL, RRAPI_POSTGRES_DBO_USER/RRAPI_POSTGRES_USER and RRAPI_POSTGRES_DBO_PASSWORD/RRAPI_POSTGRES_PASSWORD not set correctly.");
			}

			if (requireDboPermissions) { //This happens only at application startup. Do some extra logging
				LOGGER.info("postgres.rreg.db_url  : " + postgresProperties.getDbUrl());
				LOGGER.info("postgres.rreg.dbo_user: " + postgresProperties.getDboUser());
				LOGGER.info("postgres.rreg.user    : " + postgresProperties.getUser());
			}

			Connection connection = DriverManager.getConnection(postgresProperties.getDbUrl(), username, password);
			connection.setAutoCommit(false);

			if (requireDboPermissions) {
				try (Statement stmt = connection.createStatement()) {
					LOGGER.info("Creating schema " + DB_SCHEMA + " if not exists");
					stmt.executeUpdate("CREATE SCHEMA IF NOT EXISTS " + DB_SCHEMA);
					connection.commit();
				} catch (Exception e) {
					throw e;
				}
			}

			return connection;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

	public void createRegularUser(final Connection connection) throws SQLException {
		try {
			// Is the regular user created?
			int user_count = 1;
			try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(1) FROM pg_user WHERE pg_user.usename=?")) {
				stmt.setString(1, postgresProperties.getUser());
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					user_count = rs.getInt(1);
				}
			} catch (Exception e) {
				throw e;
			}

			// If not created, create it now
			if (user_count < 1) {
				try (Statement stmt = connection.createStatement()) {
					final String safeUser = StringUtils.replace(postgresProperties.getUser(), "'", "''");
					final String safePassword = StringUtils.replace(postgresProperties.getPassword(), "'", "''");

					LOGGER.info("Creating regular user " + safeUser);
					stmt.executeUpdate("CREATE USER " +	safeUser + " WITH PASSWORD '" + safePassword + "'");
					stmt.executeUpdate("GRANT CONNECT ON DATABASE " + DB + " TO " + safeUser);
					stmt.executeUpdate("GRANT USAGE ON SCHEMA " + DB_SCHEMA + " TO " + safeUser);
					stmt.executeUpdate("GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA " + DB_SCHEMA + " TO " + safeUser);
					stmt.executeUpdate("GRANT USAGE ON ALL SEQUENCES IN SCHEMA " + DB_SCHEMA + " TO " + safeUser);
				} catch (Exception e) {
					throw e;
				}
			}
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}

}
