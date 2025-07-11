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
    final String[] NOEKKELTALL_PATHS = {"/regnskapsregisteret/regnskap/**"};
    final String[] AARSREGNSKAP_PATHS = {"/regnskapsregisteret/regnskap/aarsregnskap/**"};

    final AarsregnskapCopyProperties aarsregnskapCopyProperties;

    public OpenAPIConfig(AarsregnskapCopyProperties aarsregnskapCopyProperties) {
        this.aarsregnskapCopyProperties = aarsregnskapCopyProperties;
    }

    @Bean
    public GroupedOpenApi noekkeltallApi() {
        return GroupedOpenApi.builder()
                .group("regnskapsregisteret")
                .pathsToMatch(NOEKKELTALL_PATHS)
                .pathsToExclude(AARSREGNSKAP_PATHS)
                .addOpenApiCustomizer(customizer -> {
                    customizer.info(customizer.getInfo().description("""
                            API-et har en åpen og en lukket del. Den åpne delen inneholder nøkkeltall fra sist innsendte årsregnskap, mens den lukkede delen inneholder nesten samtlige tall fra de tre siste årsregnskapene, inkludert tall fra konsernregnskapet. Den åpne delen er tilgjengelig for alle, mens den lukkede delen er kun for offentlig myndighet.
                            """));
                })
                .build();
    }

    @Bean
    public GroupedOpenApi aarsregnskapApi() {
        return GroupedOpenApi.builder()
                .group("aarsregnskap")
                .pathsToMatch(AARSREGNSKAP_PATHS)
                .addOpenApiCustomizer((customizer) -> {
                    customizer.info(customizer.getInfo()
                            .title("API for årsregnskap fra regnskapsregisteret")
                            .description("""
                                    Her kan du hente ut kopi av årsregnskap fra regnskapsregisteret.\s
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
