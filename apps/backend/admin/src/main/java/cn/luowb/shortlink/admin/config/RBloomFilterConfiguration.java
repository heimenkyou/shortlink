package cn.luowb.shortlink.admin.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 管理端布隆过滤器配置。
 */
@Configuration
public class RBloomFilterConfiguration {

    /**
     * 创建用户名布隆过滤器。
     *
     * @param redissonClient Redisson 客户端
     * @return 用户名布隆过滤器
     */
    @Bean
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(1_0000_0000L, 0.001);
        return cachePenetrationBloomFilter;
    }
}
