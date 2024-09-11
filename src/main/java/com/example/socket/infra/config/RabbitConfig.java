package com.example.socket.infra.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {
    private final String CHAT_QUEUE_NAME;
    private final String CHAT_EXCHANGE_NAME;
    private final String CHAT_ROUTING_KEY;

    public RabbitConfig(
            @Value("${rabbitmq.chat-queue.name}") String CHAT_QUEUE_NAME,
            @Value("${rabbitmq.chat-exchange.name}") String CHAT_EXCHANGE_NAME,
            @Value("${rabbitmq.chat-routing.key}") String CHAT_ROUTING_KEY
    ) {
        this.CHAT_QUEUE_NAME = CHAT_QUEUE_NAME;
        this.CHAT_EXCHANGE_NAME = CHAT_EXCHANGE_NAME;
        this.CHAT_ROUTING_KEY = CHAT_ROUTING_KEY;
    }

    // durable 옵션에 대한 정의 https://m.blog.naver.com/joyblog-/221988661550
    // WebSocket 연결된 사용자 Queue 생성
    @Bean
    public Queue chatQueue() {
        return new Queue(CHAT_QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange chatExchange() {
        return new TopicExchange(CHAT_EXCHANGE_NAME);
    }

    // Exchange와 Queue를 연결
    @Bean
    public Binding chatBinding(Queue chatQueue, TopicExchange chatExchange) {
        return BindingBuilder
                .bind(chatQueue)
                .to(chatExchange)
                .with(CHAT_ROUTING_KEY);
    }

    // RabbitMQ와 메시지 담당할 클래스
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(createConnectionFactory());
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;
    }

    // RabbitMQ와 연결 설정
    @Bean
    public ConnectionFactory createConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("jayang");
        factory.setPassword("secret");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        
        return factory;
    }
    
    // 메시지를 JSON으로 직렬/역직렬화
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
