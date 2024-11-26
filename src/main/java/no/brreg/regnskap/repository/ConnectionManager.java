package no.brreg.regnskap.repository;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.brreg.regnskap.config.properties.PostgresProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


@Component
public class ConnectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionManager.class);

    public static final String DB        = "postgres";
    public static final String DB_SCHEMA = "rregapi";

    private static final HashMap<String,DateTimeFormatter> dateTimeFormatters = new HashMap();

    private boolean databaseIsReady = false;
    private final Object databaseIsReadyLock = new Object();


    @Autowired
    PostgresProperties postgresProperties;


    public void updateDbUrl(final String dbUrl) {
        if ((postgresProperties.getDbUrl()==null && dbUrl!=null) ||
                !postgresProperties.getDbUrl().equals(dbUrl)) {
            postgresProperties.setDbUrl(dbUrl);
            initializeDatabase();
        }
    }

    public void initializeDatabase() {
        try (Connection connection = getConnection(true)) {
            try {
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                database.setLiquibaseSchemaName(ConnectionManager.DB_SCHEMA);
                Liquibase liquibase = new Liquibase("liquibase/changelog/changelog-master.xml", new ClassLoaderResourceAccessor(), database);
                liquibase.update(new Contexts(), new LabelExpression());
                LOGGER.info("Liquibase synced OK.");
                createRegularUser(connection);
                connection.commit();
                setDatabaseIsReady();
            } catch (LiquibaseException | SQLException e) {
                try {
                    LOGGER.error("Initializing DB failed: "+e.getMessage());
                    connection.rollback();
                    throw new SQLException(e);
                } catch (SQLException e2) {
                    LOGGER.error("Rollback after fail failed: "+e2.getMessage());
                    throw new SQLException(e2);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Getting connection for Liquibase update failed: "+e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            LOGGER.error("Generic error when getting connection for Liquibase failed: "+e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return getConnection(false);
    }

    public Connection getConnection(final boolean requireDboPermissions) throws SQLException {
        try {
            synchronized (this.databaseIsReadyLock) {
                while (!this.databaseIsReady && !requireDboPermissions) {
                    try {
                        this.databaseIsReadyLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

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
                LOGGER.info("postgres.rreg.db_url: " + postgresProperties.getDbUrl());
                LOGGER.info("postgres.rreg.dbo_user: " + postgresProperties.getDboUser());
                LOGGER.info("postgres.rreg.dbo_password length: " + postgresProperties.getDboPassword().length());
                LOGGER.info("postgres.rreg.user: " + postgresProperties.getUser());
                LOGGER.info("postgres.rreg.password length: " + postgresProperties.getPassword().length());
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
                    stmt.executeUpdate("CREATE USER " + safeUser + " WITH PASSWORD '" + safePassword + "'");
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

    public void setDatabaseIsReady() {
        synchronized (this.databaseIsReadyLock) {
            this.databaseIsReady = true;
            this.databaseIsReadyLock.notifyAll();
        }
    }

    public boolean getDatabaseIsReady() {
        synchronized (this.databaseIsReadyLock) {
            return this.databaseIsReady;
        }
    }

    public static Date toSqlDate(final String format, final String date) {
        if (date == null) {
            return null;
        }

        DateTimeFormatter formatter;
        synchronized (dateTimeFormatters) {
            if (!dateTimeFormatters.containsKey(format)) {
                dateTimeFormatters.put(format, DateTimeFormatter.ofPattern(format));
            }
            formatter = dateTimeFormatters.get(format);
        }
        return Date.valueOf(LocalDate.parse(date, formatter));
    }

}
