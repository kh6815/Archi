package com.architecture.archi.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    private final ObjectMapper objectMapper;

    public LettuceConnectionFactory redisConnectionFactory(int database) {
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisHost,redisPort);
        lettuceConnectionFactory.setDatabase(database);
        return lettuceConnectionFactory;
    }

    @Bean
    public LettuceConnectionFactory springRedisFactory() {
        return redisConnectionFactory(0);
    }

    @Bean(name = "accessTokenBlackListFactory", autowireCandidate = false)
    public LettuceConnectionFactory accessTokenBlackListFactory() {
        return redisConnectionFactory(1);
    }

    @Bean(name = "refreshTokenFactory", autowireCandidate = false)
    public LettuceConnectionFactory refreshTokenFactory() {
        return redisConnectionFactory(2);
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(springRedisFactory());
        template.setKeySerializer(new StringRedisSerializer());

        // GenericJackson2JsonRedisSerializer 사용
        RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(valueSerializer);
        return template;
    }

    @Bean(name = "accessTokenBlackListTemplate")
    public RedisTemplate<String, Object> accessTokenBlackListTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(accessTokenBlackListFactory());
        template.setKeySerializer(new StringRedisSerializer());

        // GenericJackson2JsonRedisSerializer 사용
        RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(valueSerializer);
        return template;
    }

    @Bean(name = "refreshTokenRedisTemplate")
    public RedisTemplate<String, Object> refreshTokenRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(refreshTokenFactory());
        template.setKeySerializer(new StringRedisSerializer());

        // GenericJackson2JsonRedisSerializer 사용
        RedisSerializer<Object> valueSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(valueSerializer);

        return template;
    }
}
