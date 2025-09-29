package com.nexus.jobboard.infrastructure.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration following SRP
 * - Single responsibility: Configure message queues and exchanges
 */
@Configuration
public class RabbitMQConfig {
    
    // Queue names
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String JOB_RECOMMENDATION_QUEUE = "job.recommendation.queue";
    public static final String APPLICATION_PROCESSING_QUEUE = "application.processing.queue";
    
    // Exchange names
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String JOB_EXCHANGE = "job.exchange";
    
    // Routing keys
    public static final String EMAIL_ROUTING_KEY = "notification.email";
    public static final String SMS_ROUTING_KEY = "notification.sms";
    public static final String JOB_APPLICATION_ROUTING_KEY = "job.application";
    public static final String JOB_RECOMMENDATION_ROUTING_KEY = "job.recommendation";
    
    // Exchanges
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }
    
    @Bean
    public TopicExchange jobExchange() {
        return new TopicExchange(JOB_EXCHANGE);
    }
    
    // Queues
    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(EMAIL_QUEUE).build();
    }
    
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(NOTIFICATION_QUEUE).build();
    }
    
    @Bean
    public Queue jobRecommendationQueue() {
        return QueueBuilder.durable(JOB_RECOMMENDATION_QUEUE).build();
    }
    
    @Bean
    public Queue applicationProcessingQueue() {
        return QueueBuilder.durable(APPLICATION_PROCESSING_QUEUE).build();
    }
    
    // Bindings
    @Bean
    public Binding emailBinding() {
        return BindingBuilder
                .bind(emailQueue())
                .to(notificationExchange())
                .with(EMAIL_ROUTING_KEY);
    }
    
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationQueue())
                .to(notificationExchange())
                .with("notification.*");
    }
    
    @Bean
    public Binding jobRecommendationBinding() {
        return BindingBuilder
                .bind(jobRecommendationQueue())
                .to(jobExchange())
                .with(JOB_RECOMMENDATION_ROUTING_KEY);
    }
    
    @Bean
    public Binding applicationProcessingBinding() {
        return BindingBuilder
                .bind(applicationProcessingQueue())
                .to(jobExchange())
                .with(JOB_APPLICATION_ROUTING_KEY);
    }
    
    // Message converter
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    // RabbitTemplate configuration
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
    
    // Listener container factory
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        return factory;
    }
}
