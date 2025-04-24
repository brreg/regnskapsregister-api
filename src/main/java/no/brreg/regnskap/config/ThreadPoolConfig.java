package no.brreg.regnskap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class ThreadPoolConfig {
    @Bean
    public ExecutorService tashExecutor() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() / 2);
    }
}
