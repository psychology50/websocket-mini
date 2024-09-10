package com.example.socket.domains.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisConfig {
    private final String host;
    private final int port;
    private final String password;
    private final int defaultCacheIndex;
    private final int chatCacheIndex;

    public RedisConfig(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") int port,
            @Value("${spring.data.redis.password}") String password,
            @Value("${spring.data.redis.default-cache-index}") int defaultCacheIndex,
            @Value("${spring.data.redis.chat-cache-index}") int chatCacheIndex
    ) {
        this.host = host;
        this.port = port;
        this.password = password;
        this.defaultCacheIndex = defaultCacheIndex;
        this.chatCacheIndex = chatCacheIndex;
    }

    @Bean
    @Primary
    LettuceConnectionFactory defaultConnectionFactory() {
        return createConnectionFactoryWith(defaultCacheIndex);
    }

    @Bean
    public CacheManager cacheManager(RedisCacheManager cacheManager) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()));

        Map<String, RedisCacheConfiguration> cacheConfigurations = Map.of(
                "users", config.entryTtl(Duration.ofMinutes(5)),
                "rooms", config.entryTtl(Duration.ofMinutes(5))
        );

        return RedisCacheManager.builder(defaultConnectionFactory())
                .cacheDefaults(config)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    @Bean
    @Qualifier("chatServer")
    LettuceConnectionFactory chatConnectionFactory() {
        return createConnectionFactoryWith(chatCacheIndex);
    }

    private LettuceConnectionFactory createConnectionFactoryWith(int index) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setPassword(password);
        configuration.setDatabase(index);

        return new LettuceConnectionFactory(configuration);
    }
}
