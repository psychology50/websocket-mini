package com.example.socket.domains.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
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
    RedisConnectionFactory defaultConnectionFactory() {
        return createConnectionFactoryWith(defaultCacheIndex);
    }

    @Bean
    @Qualifier("chatServer")
    RedisConnectionFactory chatConnectionFactory() {
        return createConnectionFactoryWith(chatCacheIndex);
    }

    @Bean
    LettuceClientConfiguration clientConfiguration() {
        return LettuceClientConfiguration.builder().build();
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration redisCacheConfiguration =
                RedisCacheConfiguration.defaultCacheConfig()
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new StringRedisSerializer()))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new GenericJackson2JsonRedisSerializer()))
                        .entryTtl(Duration.ofHours(1L));

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(cf)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }

    private LettuceConnectionFactory createConnectionFactoryWith(int index) {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
        configuration.setPassword(password);
        configuration.setDatabase(index);

        return new LettuceConnectionFactory(configuration, clientConfiguration());
    }
}
