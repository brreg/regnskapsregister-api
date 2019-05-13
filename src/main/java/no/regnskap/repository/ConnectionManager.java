package no.regnskap.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.text.MessageFormat;


public class ConnectionManager {

	private static Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

	public static final String DB_SCHEMA = "nsg";


	public static Connection getConnection() throws SQLException {
		return getConnection(false);
	}

	public static Connection getConnection(final boolean requireDboPermissions) throws SQLException {
		try {
			PropertyManager propertyManager = PropertyManager.getInstance();
			Class.forName(propertyManager.getProperty("driver")).newInstance();

			String safeHost = StringUtils.replace(System.getenv("NSG_POSTGRES_HOST"), "'", "''");
			String safeDb = StringUtils.replace(System.getenv("NSG_POSTGRES_DB"), "'", "''");
			if (safeDb==null) {
				safeDb = "nsg_db";
			}

			String username = null;
			String password = null;
			if (requireDboPermissions) {
				username = System.getenv("NSG_POSTGRES_DBO_USER");
				password = System.getenv("NSG_POSTGRES_DBO_PASSWORD");
			}
			if (username==null) {
				username = System.getenv("NSG_POSTGRES_USER");
				password = System.getenv("NSG_POSTGRES_PASSWORD");
			}

			if (safeHost==null || username==null || password==null) {
				throw new RuntimeException("System environment variable NSG_POSTGRES_HOST, NSG_POSTGRES_DB, NSG_POSTGRES_DBO_USER/NSG_POSTGRES_USER and NSG_POSTGRES_DBO_PASSWORD/NSG_POSTGRES_PASSWORD not set correctly.");
			}

			String dbUrl = MessageFormat.format(propertyManager.getProperty("nsg_db_url"), safeHost, safeDb, username, password);
			LOGGER.info(dbUrl);
			Connection connection = DriverManager.getConnection(dbUrl);
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

	public static void createRegularUser(final Connection connection) throws SQLException {
		try {
			// Is the regular user created?
			int user_count = 1;
			try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(1) FROM pg_user WHERE pg_user.usename=?")) {
				stmt.setString(1, System.getenv("NSG_POSTGRES_USER"));
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
					final String safeDb = StringUtils.replace(System.getenv("NSG_POSTGRES_DB"), "'", "''");
					final String safeUser = StringUtils.replace(System.getenv("NSG_POSTGRES_USER"), "'", "''");
					final String safePassword = StringUtils.replace(System.getenv("NSG_POSTGRES_PASSWORD"), "'", "''");

					LOGGER.info("Creating regular user " + safeUser);
					stmt.executeUpdate("CREATE USER " +	safeUser + " WITH PASSWORD '" + safePassword + "'");
					stmt.executeUpdate("GRANT CONNECT ON DATABASE " + safeDb + " TO " + safeUser);
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
