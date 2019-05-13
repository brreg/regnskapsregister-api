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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.DispatcherServlet;

import java.sql.Connection;
import java.sql.SQLException;


@SpringBootApplication
public class Application {

    private static Logger LOGGER = LoggerFactory.getLogger(Application.class);

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new CachableDispatcherServlet();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDatabase() {
        try (Connection connection = ConnectionManager.getConnection(true)) {
            try {
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
                database.setLiquibaseSchemaName(ConnectionManager.DB_SCHEMA);
                Liquibase liquibase = new Liquibase("liquibase/changelog/changelog-master.xml", new ClassLoaderResourceAccessor(), database);
                liquibase.update(new Contexts(), new LabelExpression());
                ConnectionManager.createRegularUser(connection);
                connection.commit();
                LOGGER.info("Liquibase synced OK.");
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
            LOGGER.error("Getting connection for Liquibase update failed: "+e.getMessage());
            System.exit(-1);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}