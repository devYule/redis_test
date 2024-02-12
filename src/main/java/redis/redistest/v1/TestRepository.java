package redis.redistest.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Slf4j
public class TestRepository {

    private Map<Long, TestModel> store;
    private AtomicLong id;

    public TestRepository() {
        this.store = new ConcurrentHashMap<>();
        this.id = new AtomicLong(0);
    }

    // 여기서는 의미 없지만, 2차 3차 프로젝트에서 이런 개념으로 사용할 수 있을듯 하다.
    @CacheEvict(value = "TestModel", key = "#testModel.id", cacheManager = "cacheManager")
    public Long save(TestModel testModel) {
        id.set(this.id.get() + 1);
        store.put(this.id.get(), testModel);
        Long id = this.id.get();
        return id;
    }

    @Cacheable(value = "TestModel", key = "#id", cacheManager = "cacheManager")
    public TestModel get(Long id) {
        log.info("Repository-get() 진입");
        return store.get(id);
    }

    @CachePut(value = "TestModel", key = "#id", cacheManager = "cacheManager")
    public int update(Long id, Integer userCode) {
        TestModel findTestModel = store.get(id);
        findTestModel.setUserCode(userCode);
        return 1;
    }

    @CacheEvict(value = "TestModel", key = "#id", cacheManager = "cacheManager")
    public int del(Long id) {
        store.remove(id);
        return 1;
    }

}
