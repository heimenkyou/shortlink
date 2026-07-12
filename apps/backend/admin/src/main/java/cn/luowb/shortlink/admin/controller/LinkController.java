package cn.luowb.shortlink.admin.controller;

import cn.luowb.shortlink.admin.remote.dto.LinkRemoteService;
import cn.luowb.shortlink.admin.remote.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.LinkPageReqDTO;
import cn.luowb.shortlink.admin.remote.dto.req.LinkUpdateReqDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.WebsiteMetadataRespDTO;
import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.convention.result.Results;
import cn.luowb.shortlink.common.dto.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

/**
 * 短链接后管管理控制器
 */
@RestController
@Tag(name = "短链接后管管理")
public class LinkController {
    // TODO 以后再改成 feign 调用
    LinkRemoteService linkRemoteService = new LinkRemoteService() {
    };

    /**
     * 创建短链接
     */
    @Operation(summary = "创建短链接")
    @PostMapping("/api/short-link/admin/v1/create")
    public Result<LinkCreateRespDTO> createShortLink(@RequestBody LinkCreateReqDTO requestParam) {
        return linkRemoteService.createShortLink(requestParam);
    }

    /**
     * 分页查询短链接
     */
    @Operation(summary = "分页查询短链接")
    @GetMapping("/api/short-link/admin/v1/page")
    public Result<PageResult<LinkPageRespDTO>> pageShortLink(LinkPageReqDTO requestParam) {
        return linkRemoteService.pageShortLink(requestParam);
    }

    /**
     * 修改短链接
     */
    @Operation(summary = "修改短链接")
    @PostMapping("/api/short-link/admin/v1/update")
    public Result<Void> updateShortLink(@RequestBody LinkUpdateReqDTO requestParam) {
        linkRemoteService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 从URL中提取网站元数据
     */
    @Operation(summary = "从URL中提取网站元数据")
    @GetMapping("/api/short-link/admin/v1/metadata")
    public Result<WebsiteMetadataRespDTO> getWebsiteMetadata(@RequestParam String url) {
        return linkRemoteService.fetchMetadata(url);
    }
}
