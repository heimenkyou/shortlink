package cn.luowb.shortlink.project.mq.consumer;

import cn.hutool.core.util.StrUtil;
import cn.luowb.shortlink.common.constant.RedisCacheKeyEnum;
import cn.luowb.shortlink.project.component.IpSearcher;
import cn.luowb.shortlink.project.dao.entity.*;
import cn.luowb.shortlink.project.dao.mapper.*;
import cn.luowb.shortlink.project.mq.message.LinkStatsRecordMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.LINK_ACCESS_STATS_UIP_KEY;

/**
 * 短链接访问统计消息处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LinkStatsMessageHandler {

    private final StringRedisTemplate stringRedisTemplate;
    private final LinkGotoMapper linkGotoMapper;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final LinkMapper linkMapper;
    private final IpSearcher ipSearcher;

    /**
     * 处理访问统计消息
     *
     * @param message 访问统计消息
     */
    @Transactional(rollbackFor = Exception.class)
    public void handle(LinkStatsRecordMessage message) {
        String date = LocalDate.now().toString();
        String uvKey = RedisCacheKeyEnum.LINK_ACCESS_STATS_UV_KEY.getKey(message.getFullShortUrl(), date);
        Long uvAdded = stringRedisTemplate.opsForSet().add(uvKey, message.getUvFlag());
        boolean uvFirst = Objects.equals(uvAdded, 1L);
        if (uvFirst) {
            stringRedisTemplate.expire(uvKey, 1, TimeUnit.DAYS);
        }
        String uipKey = LINK_ACCESS_STATS_UIP_KEY.getKey(message.getFullShortUrl(), date);
        Long uipAdded = stringRedisTemplate.opsForSet().add(uipKey, message.getIp());
        boolean uipFirst = Objects.equals(uipAdded, 1L);
        if (uipFirst) {
            stringRedisTemplate.expire(uipKey, 1, TimeUnit.DAYS);
        }

        LocalDateTime occurredAt = Objects.requireNonNullElseGet(message.getOccurredAt(), LocalDateTime::now);
        LocalDate nowDate = occurredAt.toLocalDate();
        int hour = occurredAt.getHour();
        int weekday = occurredAt.getDayOfWeek().getValue();

        LinkGotoDO gotoDO = linkGotoMapper.selectOne(com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery(LinkGotoDO.class)
                .eq(LinkGotoDO::getFullShortUrl, message.getFullShortUrl()));
        if (gotoDO == null) {
            log.error("短链接不存在，无法消费访问统计消息，fullShortUrl={}，messageId={}", message.getFullShortUrl(), message.getMessageId());
            return;
        }
        String gid = gotoDO.getGid();

        LinkAccessStatsDO statsDO = LinkAccessStatsDO.builder()
                .date(nowDate)
                .hour(hour)
                .weekday(weekday)
                .pv(1)
                .uv(uvFirst ? 1 : 0)
                .uip(uipFirst ? 1 : 0)
                .fullShortUrl(message.getFullShortUrl())
                .gid(gid)
                .build();
        linkAccessStatsMapper.recordStatus(statsDO);

        IpSearcher.IpInfo ipInfo = ipSearcher.searchInfo(message.getIp());
        LinkLocaleStatsDO localeStats = LinkLocaleStatsDO.builder()
                .fullShortUrl(message.getFullShortUrl())
                .gid(gid)
                .date(nowDate)
                .cnt(1)
                .province(ipInfo.getProvince())
                .city(ipInfo.getCity())
                .adcode(ipInfo.getAdcode())
                .country(ipInfo.getCountry())
                .build();
        linkLocaleStatsMapper.recordStatus(localeStats);

        LinkOsStatsDO osStats = LinkOsStatsDO.builder()
                .fullShortUrl(message.getFullShortUrl())
                .gid(gid)
                .date(nowDate)
                .cnt(1)
                .os(message.getOs())
                .build();
        linkOsStatsMapper.recordStatus(osStats);

        LinkBrowserStatsDO browserStats = LinkBrowserStatsDO.builder()
                .fullShortUrl(message.getFullShortUrl())
                .gid(gid)
                .date(nowDate)
                .cnt(1)
                .browser(message.getBrowser())
                .build();
        linkBrowserStatsMapper.recordStatus(browserStats);

        LinkDeviceStatsDO deviceStats = LinkDeviceStatsDO.builder()
                .fullShortUrl(message.getFullShortUrl())
                .gid(gid)
                .date(nowDate)
                .cnt(1)
                .device(message.getDevice())
                .build();
        linkDeviceStatsMapper.recordStatus(deviceStats);

        LinkNetworkStatsDO networkStats = LinkNetworkStatsDO.builder()
                .fullShortUrl(message.getFullShortUrl())
                .gid(gid)
                .date(nowDate)
                .cnt(1)
                .network(message.getNetwork())
                .build();
        linkNetworkStatsMapper.recordStatus(networkStats);

        String locale = StrUtil.join("-", ipInfo.getCountry(), ipInfo.getProvince(), ipInfo.getCity());
        LinkAccessLogsDO accessLog = LinkAccessLogsDO.builder()
                .fullShortUrl(message.getFullShortUrl())
                .gid(gid)
                .user(message.getUvFlag())
                .browser(message.getBrowser())
                .os(message.getOs())
                .ip(message.getIp())
                .network(message.getNetwork())
                .device(message.getDevice())
                .locale(locale)
                .build();
        linkAccessLogsMapper.insert(accessLog);

        linkMapper.incrementStats(gid, message.getFullShortUrl(), 1, uvFirst ? 1 : 0, uipFirst ? 1 : 0);

        LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
                .todayPv(1)
                .todayUv(uvFirst ? 1 : 0)
                .todayUip(uipFirst ? 1 : 0)
                .fullShortUrl(message.getFullShortUrl())
                .gid(gid)
                .date(nowDate)
                .build();
        linkStatsTodayMapper.recordStatus(linkStatsTodayDO);
    }
}
