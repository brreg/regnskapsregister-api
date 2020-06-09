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
        connectionManager.initializeDatabase();
    }

    public static void main(String[] args) {
        RIOT.init();
        SpringApplication.run(Application.class, args);
    }
}