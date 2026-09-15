package user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import user_service.dto.UserCacheDto;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UserCacheDto> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, UserCacheDto> template =
                new RedisTemplate<>();

        template.setConnectionFactory(redisConnectionFactory);

        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        JacksonJsonRedisSerializer<UserCacheDto> jsonSerializer =
                new JacksonJsonRedisSerializer<>(
                        UserCacheDto.class
                );

        template.setKeySerializer(
                new StringRedisSerializer()
        );

        template.setValueSerializer(jsonSerializer);

        template.setHashKeySerializer(
                new StringRedisSerializer()
        );

        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();

        return template;
    }
}