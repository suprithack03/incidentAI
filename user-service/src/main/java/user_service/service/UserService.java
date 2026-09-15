package user_service.service;

import user_service.dto.UserCacheDto;
import user_service.entity.User;
import user_service.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, UserCacheDto> redisTemplate;

    private static final String USER_CACHE_PREFIX = "user:";

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RedisTemplate<String, UserCacheDto> redisTemplate) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.redisTemplate = redisTemplate;
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User getUserById(Long id) {

        String cacheKey = USER_CACHE_PREFIX + id;

        try {
            UserCacheDto cachedUser =
                    redisTemplate.opsForValue().get(cacheKey);

            if (cachedUser != null) {
                System.out.println("CACHE HIT: " + cacheKey);
                return convertToUser(cachedUser);
            }

            System.out.println("CACHE MISS: " + cacheKey);

        } catch (Exception exception) {

            System.out.println(
                    "REDIS UNAVAILABLE - falling back to database: "
                            + exception.getMessage()
            );
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            UserCacheDto cacheDto = UserCacheDto.fromUser(user);

            redisTemplate.opsForValue().set(
                    cacheKey,
                    cacheDto,
                    10,
                    TimeUnit.MINUTES
            );

        } catch (Exception exception) {

            System.out.println(
                    "REDIS WRITE FAILED - user returned from database: "
                            + exception.getMessage()
            );
        }

        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User updatedUser) {

        User existingUser = getUserById(id);

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());

        User savedUser = userRepository.save(existingUser);

        String cacheKey = USER_CACHE_PREFIX + id;

        try {
            redisTemplate.delete(cacheKey);

            System.out.println(
                    "CACHE INVALIDATED: " + cacheKey
            );

        } catch (Exception exception) {

            System.out.println(
                    "REDIS INVALIDATION FAILED: "
                            + exception.getMessage()
            );
        }

        return savedUser;
    }

    public void deleteUser(Long id) {

        User existingUser = getUserById(id);

        userRepository.delete(existingUser);

        String cacheKey = USER_CACHE_PREFIX + id;

        try {
            redisTemplate.delete(cacheKey);

            System.out.println(
                    "CACHE INVALIDATED: " + cacheKey
            );

        } catch (Exception exception) {

            System.out.println(
                    "REDIS INVALIDATION FAILED: "
                            + exception.getMessage()
            );
        }
    }

    private User convertToUser(UserCacheDto cachedUser) {

        User user = new User();

        user.setId(cachedUser.getId());
        user.setFullName(cachedUser.getFullName());
        user.setEmail(cachedUser.getEmail());
        user.setRole(cachedUser.getRole());
        user.setCreatedAt(cachedUser.getCreatedAt());

        return user;
    }
}