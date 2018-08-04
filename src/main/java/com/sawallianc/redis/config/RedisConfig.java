package com.sawallianc.redis.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * Created by fingertap on 2017/6/8.
 */
@Configuration
public class RedisConfig {
    public RedisConfig(){

    }
    @Bean
    @ConditionalOnMissingBean(name={"redisTemplate"})
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template = new RedisTemplate();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new StringRedisSerializer());
        template.setKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
