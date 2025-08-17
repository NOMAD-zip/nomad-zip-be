package com.nz.nomadzip.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Configuration
//public class RedisConfig {
//
//    @Value("${spring.data.redis.port}")
//    public int port;
//
//    @Value("${spring.data.redis.host}")
//    public String host;
//
//    /* AWS Redis 8버전 미지원... */
////    @Value("${spring.data.redis.username}")
////    public String username;
////
////    @Value("${spring.data.redis.password}")
////    public String password;
//
//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory(){
//        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
//        config.setHostName(host);
////        config.setUsername(username);
////        config.setPassword(password);
//
//        return new LettuceConnectionFactory(config);
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer()); // JSON 포맷으로 저장
//
//        redisTemplate.setConnectionFactory(connectionFactory);
//        return redisTemplate;
//    }
//}
