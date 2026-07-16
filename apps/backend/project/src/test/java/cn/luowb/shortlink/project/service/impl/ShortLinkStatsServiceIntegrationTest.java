package cn.luowb.shortlink.project.service.impl;

import cn.luowb.shortlink.common.constant.RedisCacheKeyEnum;
import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.common.biz.user.UserContext;
import cn.luowb.shortlink.project.common.biz.user.UserInfoDTO;
import cn.luowb.shortlink.project.dao.entity.LinkAccessLogsDO;
import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dao.entity.LinkGotoDO;
import cn.luowb.shortlink.project.dao.mapper.LinkAccessLogsMapper;
import cn.luowb.shortlink.project.dao.mapper.LinkGotoMapper;
import cn.luowb.shortlink.project.dao.mapper.LinkMapper;
import cn.luowb.shortlink.project.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import cn.luowb.shortlink.project.dto.req.ShortLinkGroupStatsReqDTO;
import cn.luowb.shortlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import cn.luowb.shortlink.project.dto.req.ShortLinkStatsReqDTO;
import cn.luowb.shortlink.project.dto.resp.ShortLinkStatsAccessDailyRespDTO;
import cn.luowb.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import cn.luowb.shortlink.project.dto.resp.ShortLinkStatsRespDTO;
import cn.luowb.shortlink.project.service.ShortLinkStatsService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@Rollback
class ShortLinkStatsServiceIntegrationTest {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String WINDOWS_CHROME_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";
    private static final String IPHONE_SAFARI_UA = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1";
    private static final String MAC_SAFARI_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_5) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.5 Safari/605.1.15";
    private static final String ANDROID_CHROME_UA = "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ShortLinkStatsService shortLinkStatsService;
    @Autowired
    private LinkMapper linkMapper;
    @Autowired
    private LinkGotoMapper linkGotoMapper;
    @Autowired
    private LinkAccessLogsMapper linkAccessLogsMapper;
    @SpyBean
    private ShortLinkStatsServiceImpl shortLinkStatsServiceImpl;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;
    @MockBean
    private RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;
    @MockBean
    private RedissonClient redissonClient;

    private final ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
    private final SetOperations<String, String> setOperations = mock(SetOperations.class);
    private final RLock lock = mock(RLock.class);

    private final Map<String, Set<String>> redisSetStore = new HashMap<>();
    private String username;
    private String gid;

    @BeforeEach
    void setUp() {
        String uniquePart = UUID.randomUUID().toString().replace("-", "");
        username = "stats_user_" + uniquePart.substring(0, 10);
        gid = "stats_gid_" + uniquePart.substring(10, 22);
        redisSetStore.clear();

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForSet()).thenReturn(setOperations);
        when(valueOperations.get(anyString())).thenReturn(null);
        when(redissonClient.getLock(anyString())).thenReturn(lock);
        doNothing().when(lock).lock();
        doNothing().when(lock).unlock();
        doNothing().when(shortLinkStatsServiceImpl).checkGroupBelongToUser(anyString());

        // 用内存集合模拟 Redis Set，保证 UV/UIP 行为与真实环境一致。
        when(setOperations.add(anyString(), any(String.class))).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            String value = invocation.getArgument(1);
            Set<String> values = redisSetStore.computeIfAbsent(key, each -> new HashSet<>());
            return values.add(value) ? 1L : 0L;
        });
    }

    @AfterEach
    void tearDown() {
        UserContext.removeUser();
    }

    @Test
    @DisplayName("单短链统计应覆盖访问量、地区、系统、浏览器和访客类型")
    void shouldAggregateStatsForSingleShortLink() throws Exception {
        ShortLinkFixture fixture = createShortLink("s.sl.cn", "sgl001", "https://example.com/single");
        Cookie oldVisitorCookie = visitAndGetCookie(fixture, "113.87.168.12", WINDOWS_CHROME_UA, null);
        performRedirect(fixture, "113.87.168.12", WINDOWS_CHROME_UA, oldVisitorCookie);
        performRedirect(fixture, "125.71.229.12", IPHONE_SAFARI_UA, null);

        insertHistoricalAccessLog(fixture, oldVisitorCookie.getValue(), "113.87.168.12", "Chrome", "Windows");

        loginAsGroupOwner();
        ShortLinkStatsRespDTO response = shortLinkStatsService.oneShortLinkStats(buildShortLinkStatsReq(fixture));

        assertNotNull(response);
        assertEquals(3, response.getPv());
        assertEquals(2, response.getUv());
        assertEquals(2, response.getUip());

        String today = LocalDate.now().format(DATE_FORMATTER);
        ShortLinkStatsAccessDailyRespDTO todayStats = response.getDaily().stream()
                .filter(each -> today.equals(each.getDate()))
                .findFirst()
                .orElseThrow();
        assertEquals(3, todayStats.getPv());
        assertEquals(2, todayStats.getUv());
        assertEquals(2, todayStats.getUip());

        assertEquals(2, response.getLocaleCnStats().size());
        assertLocaleCount(response, "广东省", 2, 0.67D);
        assertLocaleCount(response, "四川省", 1, 0.33D);

        int currentHour = LocalDateTime.now().getHour();
        assertEquals(24, response.getHourStats().size());
        assertEquals(3, response.getHourStats().get(currentHour));
        assertEquals(3, response.getHourStats().stream().mapToInt(Integer::intValue).sum());

        int currentWeekdayIndex = LocalDateTime.now().getDayOfWeek().getValue() - 1;
        assertEquals(7, response.getWeekdayStats().size());
        assertEquals(3, response.getWeekdayStats().get(currentWeekdayIndex));
        assertEquals(3, response.getWeekdayStats().stream().mapToInt(Integer::intValue).sum());

        assertEquals(2, response.getTopIpStats().size());
        assertEquals("113.87.168.12", response.getTopIpStats().get(0).getIp());
        assertEquals(2, response.getTopIpStats().get(0).getCnt());
        assertEquals("125.71.229.12", response.getTopIpStats().get(1).getIp());
        assertEquals(1, response.getTopIpStats().get(1).getCnt());

        assertBrowserCount(response, "Chrome", 2, 0.67D);
        assertBrowserCount(response, "Safari", 1, 0.33D);
        assertOsCount(response, "Windows", 2, 0.67D);
        assertOsCount(response, "iOS", 1, 0.33D);
        assertDeviceCount(response, "PC", 2, 0.67D);
        assertDeviceCount(response, "Mobile", 1, 0.33D);
        assertUvTypeCount(response, "oldUser", 1, 0.5D);
        assertUvTypeCount(response, "newUser", 1, 0.5D);

        assertEquals(3, response.getNetworkStats().stream().mapToInt(each -> each.getCnt()).sum());
    }

    @Test
    @DisplayName("分组统计应聚合同组多个短链的访问数据")
    void shouldAggregateStatsForGroup() throws Exception {
        ShortLinkFixture first = createShortLink("g1.sl.cn", "group1", "https://example.com/group-1");
        ShortLinkFixture second = createShortLink("g2.sl.cn", "group2", "https://example.com/group-2");

        Cookie sharedCookie = visitAndGetCookie(first, "113.87.168.12", WINDOWS_CHROME_UA, null);
        performRedirect(second, "113.87.168.12", WINDOWS_CHROME_UA, sharedCookie);
        performRedirect(second, "220.181.38.148", MAC_SAFARI_UA, null);
        performRedirect(first, "125.71.229.12", ANDROID_CHROME_UA, null);

        loginAsGroupOwner();
        ShortLinkStatsRespDTO response = shortLinkStatsService.groupShortLinkStats(buildGroupStatsReq());

        assertNotNull(response);
        assertEquals(4, response.getPv());
        assertEquals(3, response.getUv());
        assertEquals(3, response.getUip());

        String today = LocalDate.now().format(DATE_FORMATTER);
        ShortLinkStatsAccessDailyRespDTO todayStats = response.getDaily().stream()
                .filter(each -> today.equals(each.getDate()))
                .findFirst()
                .orElseThrow();
        assertEquals(4, todayStats.getPv());
        // 当前 daily 维度来自 t_link_access_stats 按短链累加，同一访客跨短链会重复计数。
        assertEquals(4, todayStats.getUv());
        assertEquals(4, todayStats.getUip());

        assertLocaleCount(response, "广东省", 2, 0.5D);
        assertLocaleCount(response, "北京", 1, 0.25D);
        assertLocaleCount(response, "四川省", 1, 0.25D);

        assertBrowserCount(response, "Chrome", 3, 0.75D);
        assertBrowserCount(response, "Safari", 1, 0.25D);
        assertOsCount(response, "Windows", 2, 0.5D);
        assertOsCount(response, "Mac OS", 1, 0.25D);
        assertOsCount(response, "Android", 1, 0.25D);
        assertDeviceCount(response, "PC", 3, 0.75D);
        assertDeviceCount(response, "Mobile", 1, 0.25D);

        assertEquals(4, response.getHourStats().stream().mapToInt(Integer::intValue).sum());
        assertEquals(4, response.getWeekdayStats().stream().mapToInt(Integer::intValue).sum());
        assertEquals(4, response.getNetworkStats().stream().mapToInt(each -> each.getCnt()).sum());
        assertNull(response.getUvTypeStats());
    }

    @Test
    @DisplayName("访问记录统计应返回倒序记录并区分新老访客")
    void shouldReturnAccessRecordsWithVisitorType() throws Exception {
        ShortLinkFixture first = createShortLink("r1.sl.cn", "rcd001", "https://example.com/record-1");
        ShortLinkFixture second = createShortLink("r2.sl.cn", "rcd002", "https://example.com/record-2");

        Cookie oldVisitorCookie = visitAndGetCookie(first, "113.87.168.12", WINDOWS_CHROME_UA, null);
        insertHistoricalAccessLog(first, oldVisitorCookie.getValue(), "113.87.168.12", "Chrome", "Windows");
        performRedirect(first, "125.71.229.12", IPHONE_SAFARI_UA, null);
        performRedirect(second, "220.181.38.148", MAC_SAFARI_UA, null);

        loginAsGroupOwner();

        PageResult<ShortLinkStatsAccessRecordRespDTO> shortLinkRecords = shortLinkStatsService.shortLinkStatsAccessRecord(buildShortLinkAccessRecordReq(first));
        assertEquals(2, shortLinkRecords.getRecords().size());
        assertTrue(shortLinkRecords.getRecords().get(0).getCreateTime().compareTo(shortLinkRecords.getRecords().get(1).getCreateTime()) >= 0);
        assertTrue(shortLinkRecords.getRecords().stream().anyMatch(each -> "老访客".equals(each.getUvType()) && oldVisitorCookie.getValue().equals(each.getUser())));
        assertTrue(shortLinkRecords.getRecords().stream().anyMatch(each -> "新访客".equals(each.getUvType()) && "Safari".equals(each.getBrowser()) && "iOS".equals(each.getOs()) && "125.71.229.12".equals(each.getIp())));
        assertTrue(shortLinkRecords.getRecords().stream().allMatch(each -> each.getNetwork() == null && each.getDevice() == null && each.getLocale() == null));

        PageResult<ShortLinkStatsAccessRecordRespDTO> groupRecords = shortLinkStatsService.groupShortLinkStatsAccessRecord(buildGroupAccessRecordReq());
        assertEquals(3, groupRecords.getRecords().size());
        assertTrue(groupRecords.getRecords().get(0).getCreateTime().compareTo(groupRecords.getRecords().get(1).getCreateTime()) >= 0);
        assertTrue(groupRecords.getRecords().stream().anyMatch(each -> "老访客".equals(each.getUvType()) && oldVisitorCookie.getValue().equals(each.getUser())));
        assertTrue(groupRecords.getRecords().stream().anyMatch(each -> "Safari".equals(each.getBrowser()) && "Mac OS".equals(each.getOs()) && "220.181.38.148".equals(each.getIp())));
        assertFalse(groupRecords.getRecords().isEmpty());
    }

    /**
     * 通过真实跳转入口生成统计数据，并返回首次访问生成的 UV 标识。
     *
     * @param fixture 短链夹具
     * @param ip      访问 IP
     * @param ua      User-Agent
     * @param cookie  已有访客标识
     * @return 本次访问后的访客标识
     */
    private Cookie visitAndGetCookie(ShortLinkFixture fixture, String ip, String ua, Cookie cookie) throws Exception {
        MvcResult result = performRedirect(fixture, ip, ua, cookie).andReturn();
        Cookie responseCookie = result.getResponse().getCookie("uvFlag");
        if (cookie != null) {
            return cookie;
        }
        assertNotNull(responseCookie);
        return responseCookie;
    }

    /**
     * 调用真实跳转接口，让项目自行完成统计写入。
     *
     * @param fixture 短链夹具
     * @param ip      访问 IP
     * @param ua      User-Agent
     * @param cookie  已有访客标识
     * @return MVC 执行结果
     */
    private ResultActions performRedirect(ShortLinkFixture fixture, String ip, String ua, Cookie cookie) throws Exception {
        when(shortUriCreateCachePenetrationBloomFilter.contains(fixture.fullShortUrl())).thenReturn(true);
        var request = get("/{shortUrl}", fixture.shortUri())
                .header("Host", fixture.domain())
                .header("X-Forwarded-For", ip)
                .header("User-Agent", ua)
                .with(currentRequest -> {
                    currentRequest.setServerName(fixture.domain());
                    return currentRequest;
                });
        if (cookie != null) {
            request.cookie(cookie);
        }
        return mockMvc.perform(request)
                .andExpect(status().isFound())
                .andExpect(header().string("Location", fixture.originUrl()));
    }

    /**
     * 创建短链测试数据，保证分组归属与跳转数据齐全。
     *
     * @param domain    短链域名
     * @param shortUri  短链后缀
     * @param originUrl 原始链接
     * @return 测试夹具
     */
    private ShortLinkFixture createShortLink(String domain, String shortUri, String originUrl) {
        String fullShortUrl = domain + "/" + shortUri;
        LocalDateTime now = LocalDateTime.now();
        assertEquals(1, linkMapper.insert(LinkDO.builder()
                .domain(domain)
                .shortUri(shortUri)
                .fullShortUrl(fullShortUrl)
                .originUrl(originUrl)
                .clickNum(0)
                .gid(gid)
                .enableStatus(0)
                .createdType(0)
                .validDateType(0)
                .createTime(now)
                .updateTime(now)
                .delFlag(0)
                .build()));
        assertEquals(1, linkGotoMapper.insert(LinkGotoDO.builder()
                .gid(gid)
                .fullShortUrl(fullShortUrl)
                .build()));
        return new ShortLinkFixture(domain, shortUri, fullShortUrl, originUrl);
    }

    /**
     * 插入历史访问日志，用来稳定制造老访客场景。
     *
     * @param fixture 短链夹具
     * @param user    访客标识
     * @param ip      IP
     * @param browser 浏览器
     * @param os      操作系统
     */
    private void insertHistoricalAccessLog(ShortLinkFixture fixture, String user, String ip, String browser, String os) {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
        assertEquals(1, linkAccessLogsMapper.insert(LinkAccessLogsDO.builder()
                .fullShortUrl(fixture.fullShortUrl())
                .gid(gid)
                .user(user)
                .browser(browser)
                .os(os)
                .ip(ip)
                .createTime(yesterday)
                .updateTime(yesterday)
                .delFlag(0)
                .build()));
    }

    private void loginAsGroupOwner() {
        UserContext.setUser(UserInfoDTO.builder()
                .id(1L)
                .username(username)
                .realName("统计测试用户")
                .build());
    }

    private ShortLinkStatsReqDTO buildShortLinkStatsReq(ShortLinkFixture fixture) {
        ShortLinkStatsReqDTO request = new ShortLinkStatsReqDTO();
        request.setFullShortUrl(fixture.fullShortUrl());
        request.setGid(gid);
        request.setEnableStatus(0);
        request.setStartDate(LocalDate.now().atStartOfDay().format(DATE_TIME_FORMATTER));
        request.setEndDate(LocalDate.now().atTime(23, 59, 59).format(DATE_TIME_FORMATTER));
        return request;
    }

    private ShortLinkGroupStatsReqDTO buildGroupStatsReq() {
        ShortLinkGroupStatsReqDTO request = new ShortLinkGroupStatsReqDTO();
        request.setGid(gid);
        request.setStartDate(LocalDate.now().atStartOfDay().format(DATE_TIME_FORMATTER));
        request.setEndDate(LocalDate.now().atTime(23, 59, 59).format(DATE_TIME_FORMATTER));
        return request;
    }

    private ShortLinkStatsAccessRecordReqDTO buildShortLinkAccessRecordReq(ShortLinkFixture fixture) {
        ShortLinkStatsAccessRecordReqDTO request = new ShortLinkStatsAccessRecordReqDTO();
        request.setCurrent(1L);
        request.setSize(10L);
        request.setFullShortUrl(fixture.fullShortUrl());
        request.setGid(gid);
        request.setEnableStatus(0);
        request.setStartDate(LocalDate.now().atStartOfDay().format(DATE_TIME_FORMATTER));
        request.setEndDate(LocalDate.now().atTime(23, 59, 59).format(DATE_TIME_FORMATTER));
        return request;
    }

    private ShortLinkGroupStatsAccessRecordReqDTO buildGroupAccessRecordReq() {
        ShortLinkGroupStatsAccessRecordReqDTO request = new ShortLinkGroupStatsAccessRecordReqDTO();
        request.setCurrent(1L);
        request.setSize(10L);
        request.setGid(gid);
        request.setStartDate(LocalDate.now().atStartOfDay().format(DATE_TIME_FORMATTER));
        request.setEndDate(LocalDate.now().atTime(23, 59, 59).format(DATE_TIME_FORMATTER));
        return request;
    }

    private void assertLocaleCount(ShortLinkStatsRespDTO response, String locale, int cnt, double ratio) {
        var target = response.getLocaleCnStats().stream()
                .filter(each -> locale.equals(each.getLocale()))
                .findFirst()
                .orElseThrow();
        assertEquals(cnt, target.getCnt());
        assertEquals(ratio, target.getRatio());
    }

    private void assertBrowserCount(ShortLinkStatsRespDTO response, String browser, int cnt, double ratio) {
        var target = response.getBrowserStats().stream()
                .filter(each -> browser.equals(each.getBrowser()))
                .findFirst()
                .orElseThrow();
        assertEquals(cnt, target.getCnt());
        assertEquals(ratio, target.getRatio());
    }

    private void assertOsCount(ShortLinkStatsRespDTO response, String os, int cnt, double ratio) {
        var target = response.getOsStats().stream()
                .filter(each -> os.equals(each.getOs()))
                .findFirst()
                .orElseThrow();
        assertEquals(cnt, target.getCnt());
        assertEquals(ratio, target.getRatio());
    }

    private void assertDeviceCount(ShortLinkStatsRespDTO response, String device, int cnt, double ratio) {
        var target = response.getDeviceStats().stream()
                .filter(each -> device.equals(each.getDevice()))
                .findFirst()
                .orElseThrow();
        assertEquals(cnt, target.getCnt());
        assertEquals(ratio, target.getRatio());
    }

    private void assertUvTypeCount(ShortLinkStatsRespDTO response, String uvType, int cnt, double ratio) {
        var target = response.getUvTypeStats().stream()
                .filter(each -> uvType.equals(each.getUvType()))
                .findFirst()
                .orElseThrow();
        assertEquals(cnt, target.getCnt());
        assertEquals(ratio, target.getRatio());
    }

    private record ShortLinkFixture(String domain, String shortUri, String fullShortUrl, String originUrl) {
    }
}
