package cn.luowb.shortlink.project.remote;

import cn.luowb.shortlink.admin.remote.dto.LinkRemoteService;
import cn.luowb.shortlink.admin.remote.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.LinkPageReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.LinkUpdateReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.ShortLinkGroupStatsReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.TrashDeleteReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.TrashLinkPageReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.TrashRecoverReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.TrashSaveReqDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.WebsiteMetadataRespDTO;
import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.config.JSONConfiguration;
import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.controller.LinkController;
import cn.luowb.shortlink.project.controller.ShortLinkStatsController;
import cn.luowb.shortlink.project.controller.TrashController;
import cn.luowb.shortlink.project.controller.UrlMetadataController;
import cn.luowb.shortlink.project.service.LinkService;
import cn.luowb.shortlink.project.service.ShortLinkStatsService;
import cn.luowb.shortlink.project.service.TrashService;
import cn.luowb.shortlink.project.service.UrlMetadataService;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = FeignNacosIntegrationTest.TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("dev")
class FeignNacosIntegrationTest {

    private static final String SERVICE_NAME = "shortlink-project-feign-test-" + UUID.randomUUID();

    @MockBean
    private LinkService linkService;

    @MockBean
    private ShortLinkStatsService shortLinkStatsService;

    @MockBean
    private TrashService trashService;

    @MockBean
    private UrlMetadataService urlMetadataService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @jakarta.annotation.Resource
    private LinkRemoteService linkRemoteService;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.application.name", () -> SERVICE_NAME);
        registry.add("spring.cloud.nacos.discovery.service", () -> SERVICE_NAME);
        registry.add("spring.cloud.nacos.discovery.server-addr", () -> "127.0.0.1:8848");
        registry.add("shortlink.project.service-name", () -> SERVICE_NAME);
    }

    @Test
    @DisplayName("OpenFeign 经 Nacos 调用 project 创建接口")
    void shouldSendCreateRequestToProject() {
        cn.luowb.shortlink.project.dto.resp.LinkCreateRespDTO response =
                new cn.luowb.shortlink.project.dto.resp.LinkCreateRespDTO();
        response.setGid("group-1");
        response.setFullShortUrl("http://s.l/abc123");
        when(linkService.createShortLink(any(cn.luowb.shortlink.project.dto.req.LinkCreateReqDTO.class)))
                .thenReturn(response);

        LinkCreateReqDTO request = new LinkCreateReqDTO();
        request.setGid("group-1");
        request.setOriginUrl("https://example.com");
        Result<cn.luowb.shortlink.admin.remote.dto.resp.LinkCreateRespDTO> result =
                linkRemoteService.createShortLink(request);

        assertTrue(result.isSuccess());
        assertEquals("http://s.l/abc123", result.getData().getFullShortUrl());
        verify(linkService).createShortLink(argThat(param ->
                "group-1".equals(param.getGid()) && "https://example.com".equals(param.getOriginUrl())));
    }

    @Test
    @DisplayName("OpenFeign 经 Nacos 调用 project 分页接口并解码日期")
    void shouldSendPageQueryToProjectAndDecodeResponse() {
        LocalDateTime createTime = LocalDateTime.of(2026, 7, 23, 0, 17, 2);
        cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO record =
                new cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO();
        record.setGid("898806");
        record.setFullShortUrl("http://s.l/abc123");
        record.setValidDate(createTime.plusDays(30));
        record.setCreateTime(createTime);
        when(linkService.pageShortLink(any(cn.luowb.shortlink.project.dto.req.LinkPageReqDTO.class)))
                .thenReturn(new PageResult<>(2L, 5L, 1L, List.of(record)));

        LinkPageReqDTO request = new LinkPageReqDTO();
        request.setGid("898806");
        request.setCurrent(2);
        request.setSize(5);
        Result<PageResult<LinkPageRespDTO>> result = linkRemoteService.pageShortLink(request);

        assertTrue(result.isSuccess());
        assertEquals(2L, result.getData().getCurrent());
        assertEquals(5L, result.getData().getSize());
        assertEquals(createTime, result.getData().getRecords().get(0).getCreateTime());
        verify(linkService).pageShortLink(argThat(param ->
                "898806".equals(param.getGid()) && param.getCurrent() == 2 && param.getSize() == 5));
    }

    @Test
    @DisplayName("OpenFeign 经 Nacos 调用 project 分组计数和更新接口")
    void shouldSendRemainingLinkRequestsToProject() {
        cn.luowb.shortlink.project.dto.resp.GroupCountQueryRespDTO count =
                new cn.luowb.shortlink.project.dto.resp.GroupCountQueryRespDTO();
        count.setGid("group-1");
        count.setShortLinkCount(3);
        when(linkService.groupShortLinkCount(List.of("group-1"))).thenReturn(List.of(count));

        Result<List<cn.luowb.shortlink.admin.remote.dto.resp.GroupCountQueryRespDTO>> countResult =
                linkRemoteService.groupShortLinkCount(List.of("group-1"));

        assertEquals(3, countResult.getData().get(0).getShortLinkCount());
        verify(linkService).groupShortLinkCount(List.of("group-1"));

        LocalDateTime validDate = LocalDateTime.of(2026, 8, 23, 12, 30);
        LinkUpdateReqDTO updateRequest = new LinkUpdateReqDTO();
        updateRequest.setOldGid("group-1");
        updateRequest.setNewGid("group-2");
        updateRequest.setFullShortUrl("http://s.l/abc123");
        updateRequest.setValidDate(validDate);

        assertTrue(linkRemoteService.updateShortLink(updateRequest).isSuccess());
        verify(linkService).updateShortLink(argThat(param ->
                "group-2".equals(param.getNewGid()) && validDate.equals(param.getValidDate())));
    }

    @Test
    @DisplayName("OpenFeign 经 Nacos 调用 project 元数据接口")
    void shouldSendMetadataRequestToProjectAndDecodeResponse() {
        String url = "https://wx.zsxq.com/group/51121244585524/topic/211458185851511";
        when(urlMetadataService.fetchMetadata(url)).thenReturn(
                cn.luowb.shortlink.project.dto.resp.WebsiteMetadataRespDTO.builder()
                        .title("知识星球")
                        .favicon("https://wx.zsxq.com/favicon.ico")
                        .build());

        Result<WebsiteMetadataRespDTO> result = linkRemoteService.fetchMetadata(url);

        assertTrue(result.isSuccess());
        assertEquals("知识星球", result.getData().getTitle());
        assertEquals("https://wx.zsxq.com/favicon.ico", result.getData().getFavicon());
        verify(urlMetadataService).fetchMetadata(url);
    }

    @Test
    @DisplayName("OpenFeign 经 Nacos 调用 project 统计接口")
    void shouldSendStatsQueryToProject() {
        when(shortLinkStatsService.oneShortLinkStats(any()))
                .thenReturn(new cn.luowb.shortlink.project.dto.resp.ShortLinkStatsRespDTO());

        ShortLinkStatsReqDTO request = new ShortLinkStatsReqDTO();
        request.setGid("group-1");
        request.setFullShortUrl("http://s.l/abc123");
        request.setStartDate("2026-07-01");
        request.setEndDate("2026-07-23");
        request.setEnableStatus(0);
        Result<?> result = linkRemoteService.shortLinkStats(request);

        assertTrue(result.isSuccess());
        verify(shortLinkStatsService).oneShortLinkStats(argThat(param ->
                "group-1".equals(param.getGid()) && param.getEnableStatus() == 0));
    }

    @Test
    @DisplayName("OpenFeign 经 Nacos 调用全部 project 分组和访问记录统计接口")
    void shouldSendRemainingStatsRequestsToProject() {
        when(shortLinkStatsService.groupShortLinkStats(any()))
                .thenReturn(new cn.luowb.shortlink.project.dto.resp.ShortLinkStatsRespDTO());
        ShortLinkGroupStatsReqDTO groupRequest = new ShortLinkGroupStatsReqDTO();
        groupRequest.setGid("group-1");
        groupRequest.setStartDate("2026-07-01");
        groupRequest.setEndDate("2026-07-23");

        assertTrue(linkRemoteService.groupShortLinkStats(groupRequest).isSuccess());
        verify(shortLinkStatsService).groupShortLinkStats(argThat(param -> "group-1".equals(param.getGid())));

        Date accessTime = new Date(1784737800000L);
        cn.luowb.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO record =
                new cn.luowb.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO();
        record.setIp("127.0.0.1");
        record.setCreateTime(accessTime);
        PageResult<cn.luowb.shortlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO> page =
                new PageResult<>(1L, 10L, 1L, List.of(record));
        when(shortLinkStatsService.shortLinkStatsAccessRecord(any())).thenReturn(page);
        when(shortLinkStatsService.groupShortLinkStatsAccessRecord(any())).thenReturn(page);

        ShortLinkStatsAccessRecordReqDTO accessRequest = new ShortLinkStatsAccessRecordReqDTO();
        accessRequest.setGid("group-1");
        accessRequest.setFullShortUrl("http://s.l/abc123");
        accessRequest.setStartDate("2026-07-01");
        accessRequest.setEndDate("2026-07-23");
        Result<PageResult<ShortLinkStatsAccessRecordRespDTO>> accessResult =
                linkRemoteService.shortLinkStatsAccessRecord(accessRequest);

        assertEquals("127.0.0.1", accessResult.getData().getRecords().get(0).getIp());
        assertEquals(accessTime, accessResult.getData().getRecords().get(0).getCreateTime());
        verify(shortLinkStatsService).shortLinkStatsAccessRecord(argThat(param ->
                "http://s.l/abc123".equals(param.getFullShortUrl())));

        ShortLinkGroupStatsAccessRecordReqDTO groupAccessRequest = new ShortLinkGroupStatsAccessRecordReqDTO();
        groupAccessRequest.setGid("group-1");
        groupAccessRequest.setStartDate("2026-07-01");
        groupAccessRequest.setEndDate("2026-07-23");

        assertTrue(linkRemoteService.groupShortLinkStatsAccessRecord(groupAccessRequest).isSuccess());
        verify(shortLinkStatsService).groupShortLinkStatsAccessRecord(argThat(param ->
                "group-1".equals(param.getGid())));
    }

    @Test
    @DisplayName("OpenFeign 经 Nacos 调用 project 回收站接口")
    void shouldSendTrashRequestToProject() {
        TrashSaveReqDTO request = new TrashSaveReqDTO();
        request.setGid("group-1");
        request.setFullShortUrl("http://s.l/abc123");
        Result<Void> result = linkRemoteService.saveTrash(request);

        assertTrue(result.isSuccess());
        verify(trashService).saveTrash(argThat(param ->
                "group-1".equals(param.getGid()) && "http://s.l/abc123".equals(param.getFullShortUrl())));
    }

    @Test
    @DisplayName("OpenFeign 经 Nacos 调用全部 project 回收站接口")
    void shouldSendRemainingTrashRequestsToProject() {
        when(trashService.pageTrashLink(any())).thenReturn(new PageResult<>(1L, 10L, 0L, List.of()));
        TrashLinkPageReqDTO pageRequest = new TrashLinkPageReqDTO();
        pageRequest.setGidList(List.of("group-1"));
        pageRequest.setCurrent(1);
        pageRequest.setSize(10);

        assertTrue(linkRemoteService.pageTrashLink(pageRequest).isSuccess());
        verify(trashService).pageTrashLink(argThat(param ->
                param.getGidList().equals(List.of("group-1")) && param.getSize() == 10));

        TrashRecoverReqDTO recoverRequest = new TrashRecoverReqDTO();
        recoverRequest.setGid("group-1");
        recoverRequest.setFullShortUrl("http://s.l/abc123");
        assertTrue(linkRemoteService.recoverTrash(recoverRequest).isSuccess());
        verify(trashService).recoverTrash(argThat(param -> "group-1".equals(param.getGid())));

        TrashDeleteReqDTO deleteRequest = new TrashDeleteReqDTO();
        deleteRequest.setGid("group-1");
        deleteRequest.setFullShortUrl("http://s.l/abc123");
        assertTrue(linkRemoteService.deleteTrash(deleteRequest).isSuccess());
        verify(trashService).deleteTrash(argThat(param -> "http://s.l/abc123".equals(param.getFullShortUrl())));
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            RedisAutoConfiguration.class,
            RedisRepositoriesAutoConfiguration.class,
            RedissonAutoConfiguration.class,
            RocketMQAutoConfiguration.class
    })
    @EnableDiscoveryClient
    @EnableFeignClients(clients = LinkRemoteService.class)
    @Import({
            JSONConfiguration.class,
            LinkController.class,
            ShortLinkStatsController.class,
            TrashController.class,
            UrlMetadataController.class
    })
    static class TestApplication {
    }
}
