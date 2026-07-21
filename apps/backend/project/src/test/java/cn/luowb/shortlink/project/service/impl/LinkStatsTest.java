package cn.luowb.shortlink.project.service.impl;

import cn.luowb.shortlink.project.component.IpSearcher;
import cn.luowb.shortlink.project.dao.entity.LinkAccessStatsDO;
import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dao.entity.LinkGotoDO;
import cn.luowb.shortlink.project.dao.entity.LinkLocaleStatsDO;
import cn.luowb.shortlink.project.dao.mapper.LinkAccessStatsMapper;
import cn.luowb.shortlink.project.dao.mapper.LinkGotoMapper;
import cn.luowb.shortlink.project.dao.mapper.LinkLocaleStatsMapper;
import cn.luowb.shortlink.project.dao.mapper.LinkMapper;
import cn.luowb.shortlink.project.mq.consumer.LinkStatsMessageHandler;
import cn.luowb.shortlink.project.mq.message.LinkStatsRecordMessage;
import cn.luowb.shortlink.project.mq.producer.LinkStatsMessageProducer;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Sql(scripts = "/sql/link-stats-test-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class LinkStatsTest {

    private static final String DOMAIN = "link-stats-it.local";
    private static final String ORIGIN_URL = "https://www.example.com";
    private static final String TEST_IP = "113.87.168.12";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LinkMapper linkMapper;
    @Autowired
    private LinkGotoMapper linkGotoMapper;
    @Autowired
    private LinkAccessStatsMapper linkAccessStatsMapper;
    @Autowired
    private LinkLocaleStatsMapper linkLocaleStatsMapper;
    @Autowired
    private IpSearcher ipSearcher;
    @Autowired
    private LinkStatsMessageHandler linkStatsMessageHandler;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;
    @MockBean
    private RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    @MockBean
    private RedissonClient redissonClient;
    @MockBean
    private LinkStatsMessageProducer linkStatsMessageProducer;

    private final ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    private final SetOperations<String, String> setOperations = mock(SetOperations.class);
    private final RLock lock = mock(RLock.class);
    private final Map<String, Set<String>> redisSetStore = new HashMap<>();

    private String gid;
    private String shortUrl;
    private String fullShortUrl;

    @BeforeEach
    void setUp() {
        String uniquePart = UUID.randomUUID().toString().replace("-", "");
        gid = "stats-" + uniquePart.substring(0, 12);
        shortUrl = uniquePart.substring(12, 18);
        fullShortUrl = DOMAIN + "/" + shortUrl;
        redisSetStore.clear();

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForSet()).thenReturn(setOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)).thenReturn(true);
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        doAnswer(invocation -> {
            LinkStatsRecordMessage message = invocation.getArgument(0);
            linkStatsMessageHandler.handle(message);
            return null;
        }).when(linkStatsMessageProducer).send(any(LinkStatsRecordMessage.class));
        when(setOperations.add(anyString(), any(String.class))).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            String value = invocation.getArgument(1);
            Set<String> values = redisSetStore.computeIfAbsent(key, each -> new HashSet<>());
            return values.add(value) ? 1L : 0L;
        });
    }

    @Test
    @DisplayName("短链接跳转应通过唯一索引聚合访问统计和地区统计")
    void shouldRedirectAndAggregateStatsThroughDatabase() throws Exception {
        insertShortLink();

        MvcResult firstVisit = performRedirect(null)
                .andExpect(status().isFound())
                .andExpect(header().string("Location", ORIGIN_URL))
                .andExpect(cookie().exists("uvFlag"))
                .andReturn();

        Cookie uvCookie = firstVisit.getResponse().getCookie("uvFlag");
        assertNotNull(uvCookie);

        performRedirect(uvCookie)
                .andExpect(status().isFound())
                .andExpect(header().string("Location", ORIGIN_URL))
                .andExpect(cookie().doesNotExist("uvFlag"));

        awaitStatsPersisted();

        List<LinkAccessStatsDO> accessStats = linkAccessStatsMapper.selectList(
                Wrappers.lambdaQuery(LinkAccessStatsDO.class)
                        .eq(LinkAccessStatsDO::getFullShortUrl, fullShortUrl)
                        .eq(LinkAccessStatsDO::getGid, gid));
        assertEquals(1, accessStats.size());
        assertEquals(2, accessStats.get(0).getPv());
        assertEquals(1, accessStats.get(0).getUv());
        assertEquals(1, accessStats.get(0).getUip());

        List<LinkLocaleStatsDO> localeStats = linkLocaleStatsMapper.selectList(
                Wrappers.lambdaQuery(LinkLocaleStatsDO.class)
                        .eq(LinkLocaleStatsDO::getFullShortUrl, fullShortUrl)
                        .eq(LinkLocaleStatsDO::getGid, gid));
        assertEquals(1, localeStats.size());
        assertEquals(2, localeStats.get(0).getCnt());
        assertEquals("中国", localeStats.get(0).getCountry());
        assertEquals("广东省", localeStats.get(0).getProvince());
        assertEquals("深圳市", localeStats.get(0).getCity());
        assertEquals("深圳市", localeStats.get(0).getAdcode());
    }

    @Test
    @DisplayName("相同分片内完整短链接重复写入应触发唯一索引")
    void shouldRejectDuplicateFullShortUrl() {
        insertShortLink();
        LinkDO duplicate = buildLinkDO();

        assertThrows(DuplicateKeyException.class, () -> linkMapper.insert(duplicate));
    }

    @ParameterizedTest(name = "{0} 应解析为 {1}-{2}-{3}-{4}")
    @CsvSource({
            "113.87.168.12, 中国, 广东省, 深圳市, 电信, 深圳市",
            "125.71.229.12, 中国, 四川省, 成都市, 电信, 成都市",
            "220.181.38.148, 中国, 北京, 北京市, 电信, 北京市",
            "115.236.118.12, 中国, 浙江省, 杭州市, 电信, 杭州市",
            "182.150.0.1, 中国, 四川省, 成都市, 电信, 成都市"
    })
    @DisplayName("真实 IpSearcher 应解析地区信息")
    void shouldResolveIpInfo(String ip, String country, String province, String city, String isp, String adcode) {
        IpSearcher.IpInfo actual = ipSearcher.searchInfo(ip);

        assertEquals(country, actual.getCountry());
        assertEquals(province, actual.getProvince());
        assertEquals(city, actual.getCity());
        assertEquals(isp, actual.getIsp());
        assertEquals(adcode, actual.getAdcode());
    }

    /**
     * 通过真实 MVC 入口访问短链接。
     *
     * @param uvCookie 已有访客标识，首次访问传 {@code null}
     * @return MVC 执行结果
     */
    private org.springframework.test.web.servlet.ResultActions performRedirect(Cookie uvCookie) throws Exception {
        var request = get("/{shortUrl}", shortUrl)
                .header("Host", DOMAIN)
                .header("X-Forwarded-For", TEST_IP)
                .header("User-Agent", "JUnit Integration Test")
                .with(currentRequest -> {
                    currentRequest.setServerName(DOMAIN);
                    return currentRequest;
                });
        if (uvCookie != null) {
            request.cookie(uvCookie);
        }
        return mockMvc.perform(request);
    }

    /**
     * 等待异步统计落库完成。
     */
    private void awaitStatsPersisted() throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline) {
            List<LinkAccessStatsDO> accessStats = linkAccessStatsMapper.selectList(
                    Wrappers.lambdaQuery(LinkAccessStatsDO.class)
                            .eq(LinkAccessStatsDO::getFullShortUrl, fullShortUrl)
                            .eq(LinkAccessStatsDO::getGid, gid)
            );
            List<LinkLocaleStatsDO> localeStats = linkLocaleStatsMapper.selectList(
                    Wrappers.lambdaQuery(LinkLocaleStatsDO.class)
                            .eq(LinkLocaleStatsDO::getFullShortUrl, fullShortUrl)
                            .eq(LinkLocaleStatsDO::getGid, gid)
            );
            boolean accessReady = accessStats.size() == 1
                    && accessStats.get(0).getPv() == 2
                    && accessStats.get(0).getUv() == 1
                    && accessStats.get(0).getUip() == 1;
            boolean localeReady = localeStats.size() == 1
                    && localeStats.get(0).getCnt() == 2;
            if (accessReady && localeReady) {
                return;
            }
            Thread.sleep(100);
        }
    }

    /**
     * 使用真实 Mapper 写入跳转所需的短链主记录与路由记录。
     */
    private void insertShortLink() {
        assertEquals(1, linkMapper.insert(buildLinkDO()));
        assertEquals(1, linkGotoMapper.insert(LinkGotoDO.builder()
                .gid(gid)
                .fullShortUrl(fullShortUrl)
                .build()));
    }

    /**
     * 构造落在当前测试分片中的短链记录。
     *
     * @return 短链实体
     */
    private LinkDO buildLinkDO() {
        LocalDateTime now = LocalDateTime.now();
        return LinkDO.builder()
                .domain(DOMAIN)
                .shortUri(shortUrl)
                .fullShortUrl(fullShortUrl)
                .originUrl(ORIGIN_URL)
                .clickNum(0)
                .gid(gid)
                .enableStatus(0)
                .createdType(0)
                .validDateType(0)
                .createTime(now)
                .updateTime(now)
                .delFlag(0)
                .build();
    }
}
