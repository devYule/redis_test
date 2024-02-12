package redis.redistest.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisUtils {

    private final RedisTemplate<String, Object> redisTemplate;

    public void 사용_가능한_모든_메소드들() {
        Long timeOut = 1L;
        TimeUnit timeOut_MILLISECONDS = TimeUnit.MILLISECONDS;
        TimeUnit timeOut_SECONDS = TimeUnit.SECONDS;
        TimeUnit timeOut_MINUTES = TimeUnit.MINUTES;
        TimeUnit timeOut_HOURS = TimeUnit.HOURS;
        TimeUnit timeOut_DAYS = TimeUnit.DAYS;
        String value = null;

        redisTemplate.opsForValue().set("key", value, timeOut, timeOut_SECONDS);
        redisTemplate.opsForValue().get("key");
        redisTemplate.opsForValue().getAndDelete("key");
        redisTemplate.delete("key");
    }

    public void set(String key, Object value) {
        long defaultTimeOut = 10;
        TimeUnit defaultDuration = TimeUnit.SECONDS;
        set(key, value, defaultTimeOut, defaultDuration);
    }

    public void set(String key, Object value, long timeOut, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeOut, timeUnit);
    }

    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public String getAndRemove(String key) {
        return (String) redisTemplate.opsForValue().getAndDelete(key);
    }

    public void remove(String key) {
        redisTemplate.delete(key);
    }

}
