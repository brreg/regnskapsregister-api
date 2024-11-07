package no.brreg.regnskap.config;

import com.hazelcast.config.*;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig.DurationConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig.ExpiryPolicyType;
import no.brreg.regnskap.config.properties.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    public static String
            CACHE_AAR_REQUEST_BUCKET = "aarsregnskapCopyBucket",
            CACHE_AAR_COPY_FILEMETA = "aarsregnskapCopyFileMeta";

    private final CacheProperties cacheProperties;

    public CacheConfig(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Bean
    public Config config() {
        return new Config()
                .setNetworkConfig(getNetworkConfig())
                .addCacheConfig(createAarRequestBucketCacheConfig())
                .addCacheConfig(createAarCopyFileMetaCacheConfig());
    }

    private NetworkConfig getNetworkConfig() {
        JoinConfig join = new JoinConfig()
                .setMulticastConfig(new MulticastConfig().setEnabled(false))
                .setKubernetesConfig(new KubernetesConfig().setEnabled(false))
                .setAutoDetectionConfig(new AutoDetectionConfig().setEnabled(false));

        return new NetworkConfig()
                .setJoin(join);
    }

    private CacheSimpleConfig createAarRequestBucketCacheConfig() {
        return new CacheSimpleConfig(CACHE_AAR_REQUEST_BUCKET)
                .setExpiryPolicyFactoryConfig(createExpiryConfig(ExpiryPolicyType.ACCESSED, cacheProperties.ttlAfterAccessedAarRequests()));
    }

    private CacheSimpleConfig createAarCopyFileMetaCacheConfig() {
        return new CacheSimpleConfig(CACHE_AAR_COPY_FILEMETA)
                .setExpiryPolicyFactoryConfig(createExpiryConfig(ExpiryPolicyType.CREATED, cacheProperties.ttlAfterCreatedAarCopyFilemeta()));
    }

    private ExpiryPolicyFactoryConfig createExpiryConfig(ExpiryPolicyType expiryPolicyType, long seconds) {
        DurationConfig duration = new DurationConfig(seconds, TimeUnit.SECONDS);
        TimedExpiryPolicyFactoryConfig expiryPolicy = new TimedExpiryPolicyFactoryConfig(expiryPolicyType, duration);

        return new ExpiryPolicyFactoryConfig(expiryPolicy);
    }
}
