package com.kkumteul.config;

import com.kkumteul.domain.event.service.TicketService;
import com.kkumteul.util.redis.RedisMessageSubscriber;
import java.time.Duration;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value(value = "${spring.data.redis.host}")
    private String host;

    @Value(value = "${spring.data.redis.port}")
    private int port;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379"); // Redis 주소 설정
        return Redisson.create(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());

        template.setDefaultSerializer(new StringRedisSerializer());
        return template;
    }

    // 추천 도서용 RedisTemplate
    @Bean(name = "recommendationRedisTemplate")
    public RedisTemplate<String, Object> recommendationRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key를 문자열로 직렬화
        template.setKeySerializer(new StringRedisSerializer());
        // Value를 JSON으로 직렬화 (GenericJackson2JsonRedisSerializer 사용)
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // HashKey와 HashValue도 설정 (필요한 경우)
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet(); // 초기화
        return template;
    }



    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory, MessageListenerAdapter eventStartListenerAdapter,
            MessageListenerAdapter ticketRemainListenerAdapter, ChannelTopic eventStartTopic, ChannelTopic ticketRemainTopic) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(eventStartListenerAdapter, eventStartTopic);
        container.addMessageListener(ticketRemainListenerAdapter, ticketRemainTopic);
        return container;
    }

    @Bean
    public MessageListenerAdapter eventStartListenerAdapter(TicketService ticketService) {
        return new MessageListenerAdapter(ticketService, "handleEventStartMessage");
    }

    @Bean
    public MessageListenerAdapter ticketRemainListenerAdapter(TicketService ticketService) {
        return new MessageListenerAdapter(ticketService, "handleTicketRemainMessage");
    }

    @Bean
    public ChannelTopic eventStartTopic() {
        return new ChannelTopic("eventStart");
    }

    @Bean
    public ChannelTopic ticketRemainTopic() {
        return new ChannelTopic("ticketRemain");
    }
}