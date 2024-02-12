package redis.redistest.v1;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class RedisUtilsTest {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @DisplayName("use String")
    void test() {
        String key = "key";
        String value = "1000";
        redisUtils.set(key, value);
        log.info("redis - 스트링으로 등록");
        log.info("redisUtils.get() = {}", redisUtils.get(key));

        redisUtils.remove("key");
    }

    @Test
    @DisplayName("when save Object")
    void test2() {
        TestModel testUser = TestModel.builder()
                .userCode(1)
                .userName("testUser")
                .createdAt(LocalDateTime.now())
                .build();

        String key = String.valueOf(testUser.getUserCode());
        String value;
        try {
            value = objectMapper.writeValueAsString(testUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisUtils.set(key, value);
        log.info("result = {}", redisUtils.get(key));
        TestModel resultTestModel = null;
        try {
            resultTestModel = objectMapper.readValue(value, TestModel.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("resultTestModel = {}", resultTestModel);

    }

    @Autowired
    TestRepository repository;

    @Test
    void cacheTest() {
        TestModel testModel = new TestModel(1, "test1", LocalDateTime.now());
        repository.save(testModel);
        log.info("saved");
        TestModel testModelResult = repository.get(1L);
        log.info("testModelResult = {}", testModelResult);

        TestModel testModelResult2 = repository.get(1L);
        log.info("testModelResult = {}", testModelResult2);

        repository.save(new TestModel(2, "test2", LocalDateTime.now()));
        /*
        : saved
        : Repository-get() 진입
        : testModelResult = TestModel(userCode=1, userName=test1, createdAt=2024-02-08T14:13:52.455532)
        : testModelResult = TestModel(userCode=1, userName=test1, createdAt=2024-02-08T14:13:52.455532)

        두번째 get() 에서는 repository 를 거치지 않는것을 볼 수 있다.
         */
    }

}