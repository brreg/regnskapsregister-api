package no.brreg.regnskap;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import no.brreg.regnskap.repository.ConnectionManager;
import no.brreg.regnskap.spring.ApplicationInfo;
import no.brreg.regnskap.spring.CachableDispatcherServlet;
import no.brreg.regnskap.spring.properties.*;
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


@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "RegnskapsRegister-API",
                version = ApplicationInfo.VERSION
        )
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
@EnableScheduling
@EnableConfigurationProperties({
        PostgresProperties.class,
        SftpProperties.class,
        FileimportProperties.class,
        SlackProperties.class,
        IpProperties.class,
        JenaExternalUrlProperties.class
})
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