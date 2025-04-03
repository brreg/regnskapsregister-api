package no.brreg.regnskap.config;

import jakarta.servlet.http.HttpServletRequest;
import no.brreg.regnskap.config.properties.AarsregnskapCopyProperties;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Configuration
public class OpenAPIConfig {

    final AarsregnskapCopyProperties aarsregnskapCopyProperties;

    public OpenAPIConfig(AarsregnskapCopyProperties aarsregnskapCopyProperties) {
        this.aarsregnskapCopyProperties = aarsregnskapCopyProperties;
    }

    @Bean
    public GroupedOpenApi noekkeltallApi() {
        String[] paths = {"/regnskapsregisteret/regnskap/**"};
        return GroupedOpenApi.builder()
                .group("regnskapsregisteret")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi aarsregnskapApi() {
        String[] paths = {"/regnskapsregisteret/aarsregnskap/**"};
        return GroupedOpenApi.builder()
                .group("aarsregnskap")
                .pathsToMatch(paths)
                .addOpenApiCustomizer((customizer) -> {
                    customizer.info(customizer.getInfo().description("""
                            API for årsregnskap fra regnskapsregisteret.\s
                            \s
                            Det er kun mulig å hente ut årsregnskap for de siste %s årene.\s
                            \s""".formatted(aarsregnskapCopyProperties.yearsAvailable())));
                })
                .build();
    }

    @Bean
    public SwaggerIndexTransformer swaggerIndexTransformer(
            SwaggerUiConfigProperties swaggerUiConfigProperties,
            SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            SwaggerWelcomeCommon swaggerWelcomeCommon,
            ObjectMapperProvider objectMapperProvider) {

        return (HttpServletRequest request, Resource resource, ResourceTransformerChain transformer) -> {
            if (resource.toString().contains("swagger-ui.css")) {
                try (
                        final InputStream is = resource.getInputStream();
                        final InputStreamReader isr = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(isr)
                ) {
                    final String css = br.lines().collect(Collectors.joining());
                    String transformedCss = css + """
                            .swagger-ui .topbar {
                                display: none;
                            }
                            """;
                    return new TransformedResource(resource, transformedCss.getBytes());
                }
            } else {
                return new SwaggerIndexPageTransformer(
                        swaggerUiConfigProperties,
                        swaggerUiOAuthProperties,
                        swaggerWelcomeCommon,
                        objectMapperProvider
                ).transform(request, resource, transformer);
            }
        };
    }
}
