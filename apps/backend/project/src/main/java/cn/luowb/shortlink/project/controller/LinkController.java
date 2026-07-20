package cn.luowb.shortlink.project.controller;

import cn.luowb.shortlink.common.convention.exception.ClientException;
import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.convention.result.Results;
import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.project.dto.req.LinkPageReqDTO;
import cn.luowb.shortlink.project.dto.req.LinkUpdateReqDTO;
import cn.luowb.shortlink.project.dto.resp.GroupCountQueryRespDTO;
import cn.luowb.shortlink.project.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.project.handler.CustomBlockHandler;
import cn.luowb.shortlink.project.service.LinkService;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * 短链接控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "短链接管理")
@Slf4j
public class LinkController {
    private final LinkService linkService;

    /**
     * 短链接跳转
     */
    @Operation(summary = "短链接跳转")
    @GetMapping("/{shortUrl:[a-zA-Z0-9]{6}}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl, HttpServletRequest request, HttpServletResponse response) {
        String longUrl;
        try {
            longUrl = linkService.resolveShortUrl(shortUrl, request, response);
        } catch (ClientException e) {
            log.info("resolve short url failed, shortUrl: {}, msg: {}", shortUrl, e.getMessage());
            // 短链接不存在，跳转到404页面
            longUrl = "/page/notfound";
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(longUrl))
                .build();
    }

    /**
     * 创建短链接
     */
    @Operation(summary = "创建短链接")
    @PostMapping("/api/short-link/v1/create")
    @SentinelResource(
            value = "create_short-link",
            blockHandler = "createShortLinkBlockHandlerMethod",
            blockHandlerClass = CustomBlockHandler.class
    )
    public Result<LinkCreateRespDTO> createShortLink(@RequestBody LinkCreateReqDTO requestParam) {
        return Results.success(linkService.createShortLink(requestParam));
    }

    /**
     * 修改短链接
     */
    @Operation(summary = "修改短链接")
    @PostMapping("/api/short-link/v1/update")
    public Result<Void> updateShortLink(@RequestBody LinkUpdateReqDTO requestParam) {
        linkService.updateShortLink(requestParam);
        return Results.success();
    }

    /**
     * 分页查询短链接
     */
    @Operation(summary = "分页查询短链接")
    @GetMapping("/api/short-link/v1/page")
    public Result<PageResult<LinkPageRespDTO>> pageShortLink(@Valid LinkPageReqDTO requestParam) {
        return Results.success(linkService.pageShortLink(requestParam));
    }

    /**
     * 查询短链接分组下的短链接数量
     */
    @Operation(summary = "查询短链接分组下的短链接数量")
    @GetMapping("/api/short-link/admin/v1/group/count")
    public Result<List<GroupCountQueryRespDTO>> groupShortLinkCount(@RequestParam("gidList") List<String> gidList) {
        return Results.success(linkService.groupShortLinkCount(gidList));
    }
}
