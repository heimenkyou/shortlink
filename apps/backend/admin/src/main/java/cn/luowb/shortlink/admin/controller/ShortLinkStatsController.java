package cn.luowb.shortlink.admin.controller;

import cn.luowb.shortlink.admin.remote.dto.LinkRemoteService;
import cn.luowb.shortlink.admin.remote.dto.req.ShortLinkGroupStatsAccessRecordReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.ShortLinkGroupStatsReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.ShortLinkStatsAccessRecordReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.ShortLinkStatsReqDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.dto.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接监控后管控制器
 */
@RestController
@Tag(name = "短链接监控后管管理")
public class ShortLinkStatsController {

    // TODO 以后再改成 feign 调用
    LinkRemoteService linkRemoteService = new LinkRemoteService() {
    };

    /**
     * 查询单个短链接监控数据
     */
    @Operation(summary = "查询单个短链接监控数据")
    @GetMapping("/api/short-link/admin/v1/stats")
    public Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return linkRemoteService.shortLinkStats(requestParam);
    }

    /**
     * 查询分组短链接监控数据
     */
    @Operation(summary = "查询分组短链接监控数据")
    @GetMapping("/api/short-link/admin/v1/stats/group")
    public Result<ShortLinkStatsRespDTO> groupShortLinkStats(ShortLinkGroupStatsReqDTO requestParam) {
        return linkRemoteService.groupShortLinkStats(requestParam);
    }

    /**
     * 查询单个短链接访问记录监控数据
     */
    @Operation(summary = "查询单个短链接访问记录监控数据")
    @GetMapping("/api/short-link/admin/v1/stats/access-record")
    public Result<PageResult<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return linkRemoteService.shortLinkStatsAccessRecord(requestParam);
    }

    /**
     * 查询分组短链接访问记录监控数据
     */
    @Operation(summary = "查询分组短链接访问记录监控数据")
    @GetMapping("/api/short-link/admin/v1/stats/access-record/group")
    public Result<PageResult<ShortLinkStatsAccessRecordRespDTO>> groupShortLinkStatsAccessRecord(ShortLinkGroupStatsAccessRecordReqDTO requestParam) {
        return linkRemoteService.groupShortLinkStatsAccessRecord(requestParam);
    }
}
