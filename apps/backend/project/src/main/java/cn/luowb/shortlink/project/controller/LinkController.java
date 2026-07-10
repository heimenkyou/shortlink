package cn.luowb.shortlink.project.controller;

import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.convention.result.Results;
import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.project.dto.req.LinkPageReqDTO;
import cn.luowb.shortlink.project.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.project.service.LinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短链接控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "短链接管理")
public class LinkController {
    private final LinkService linkService;

    /**
     * 创建短链接
     */
    @Operation(summary = "创建短链接")
    @PostMapping("/api/short-link/v1/create")
    public Result<LinkCreateRespDTO> createShortLink(@RequestBody LinkCreateReqDTO requestParam) {
        return Results.success(linkService.createShortLink(requestParam));
    }

    /**
     * 分页查询短链接
     */
    @Operation(summary = "分页查询短链接")
    @PostMapping("/api/short-link/v1/page")
    public Result<PageResult<LinkPageRespDTO>> pageShortLink(@RequestBody LinkPageReqDTO requestParam) {
        return Results.success(linkService.pageShortLink(requestParam));
    }
}
