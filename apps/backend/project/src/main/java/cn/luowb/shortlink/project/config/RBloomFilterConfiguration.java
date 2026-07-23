package cn.luowb.shortlink.project.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 项目端布隆过滤器配置。
 */
@Configuration
public class RBloomFilterConfiguration {

    /**
     * 创建短链接布隆过滤器。
     *
     * @param redissonClient Redisson 客户端
     * @return 短链接布隆过滤器
     */
    @Bean
    public RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("shortUriCreateCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(1_0000_0000L, 0.001);
        return cachePenetrationBloomFilter;
    }
}
