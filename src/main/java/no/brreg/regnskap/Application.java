package no.brreg.regnskap;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import no.brreg.regnskap.config.ApplicationInfo;
import no.brreg.regnskap.config.CachableDispatcherServlet;
import no.brreg.regnskap.config.properties.*;
import org.apache.jena.riot.RIOT;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.DispatcherServlet;

import java.time.Clock;


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
        SftpProperties.class,
        FileimportProperties.class,
        SlackProperties.class,
        IpProperties.class,
        JenaExternalUrlProperties.class,
        CacheProperties.class,
        AarsregnskapCopyProperties.class
})
public class Application {

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet() {
        return new CachableDispatcherServlet();
    }

    @Bean
    public Clock systemClock() {
        return Clock.systemDefaultZone();
    }

    public static void main(String[] args) {
        RIOT.init();
        SpringApplication.run(Application.class, args);
    }
}
