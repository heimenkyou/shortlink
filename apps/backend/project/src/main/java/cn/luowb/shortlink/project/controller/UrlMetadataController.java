package cn.luowb.shortlink.project.controller;

import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.convention.result.Results;
import cn.luowb.shortlink.project.dto.resp.WebsiteMetadataRespDTO;
import cn.luowb.shortlink.project.service.UrlMetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "URL元数据管理")
public class UrlMetadataController {
    private final UrlMetadataService urlMetadataService;

    /**
     * 从URL中提取网站元数据
     */
    @Operation(summary = "从URL中提取网站元数据")
    @GetMapping("/api/short-link/v1/metadata")
    public Result<WebsiteMetadataRespDTO> getWebsiteMetadata(@RequestParam String url) {
        return Results.success(urlMetadataService.fetchMetadata(url));
    }
}
