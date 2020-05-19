package no.regnskap;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import no.regnskap.repository.ConnectionManager;
import no.regnskap.spring.CachableDispatcherServlet;
import org.apache.jena.riot.RIOT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.DispatcherServlet;

import java.sql.Connection;
import java.sql.SQLException;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({PostgresProperties.class, SftpProperties.class, SlackProperties.class})
public class Application {

    private static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Autowired
    private ConnectionManager connectionManager;

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new CachableDispatcherServlet();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        try (Connection connection = connectionManager.getConnection(true)) {
            try {
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                database.setLiquibaseSchemaName(ConnectionManager.DB_SCHEMA);
                Liquibase liquibase = new Liquibase("liquibase/changelog/changelog-master.xml", new ClassLoaderResourceAccessor(), database);
                liquibase.update(new Contexts(), new LabelExpression());
                LOGGER.info("Liquibase synced OK.");
                connectionManager.createRegularUser(connection);
                connection.commit();
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
            System.exit(-1);
        } catch (Exception e) {
            LOGGER.error("Generic error when getting connection for Liquibase failed: "+e.getMessage(), e);
            System.exit(-2);
        }
    }

    public static void main(String[] args) {
        RIOT.init();
        SpringApplication.run(Application.class, args);
    }
}