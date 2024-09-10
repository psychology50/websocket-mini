package com.example.socket.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

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
