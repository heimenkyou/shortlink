package cn.luowb.shortlink.admin.controller;

import cn.luowb.shortlink.admin.dto.req.OriginUrlWhitelistUpdateReqDTO;
import cn.luowb.shortlink.admin.dto.resp.OriginUrlWhitelistStatusRespDTO;
import cn.luowb.shortlink.admin.service.OriginUrlWhitelistManageService;
import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.convention.result.Results;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * 原始链接白名单开发者控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "原始链接白名单开发者管理")
public class OriginUrlWhitelistDevController {

    private static final String MANAGE_TOKEN_HEADER = "X-ShortLink-Dev-Token";

    private final OriginUrlWhitelistManageService originUrlWhitelistManageService;

    /**
     * 查询原始链接白名单状态
     */
    @Operation(summary = "查询原始链接白名单状态")
    @GetMapping("/internal/dev/v1/origin-url-whitelist")
    public Result<OriginUrlWhitelistStatusRespDTO> getStatus(
            @RequestHeader(MANAGE_TOKEN_HEADER) String manageToken) {
        originUrlWhitelistManageService.validateManageToken(manageToken);
        return Results.success(originUrlWhitelistManageService.getStatus());
    }

    /**
     * 更新原始链接白名单状态
     */
    @Operation(summary = "更新原始链接白名单状态")
    @PostMapping("/internal/dev/v1/origin-url-whitelist")
    public Result<Void> updateStatus(
            @RequestHeader(MANAGE_TOKEN_HEADER) String manageToken,
            @RequestBody OriginUrlWhitelistUpdateReqDTO requestParam) {
        originUrlWhitelistManageService.validateManageToken(manageToken);
        originUrlWhitelistManageService.updateStatus(requestParam.getEnabled());
        return Results.success();
    }
}
