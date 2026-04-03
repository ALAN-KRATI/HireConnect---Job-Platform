package com.hireconnect.application.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue applicationQueue() {
        return new Queue("application.notification.queue");
    }

    @Bean
    public DirectExchange applicationExchange() {
        return new DirectExchange("application.exchange");
    }
}
