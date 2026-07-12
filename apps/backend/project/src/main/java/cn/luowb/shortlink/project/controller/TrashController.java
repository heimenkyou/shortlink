package cn.luowb.shortlink.project.controller;


import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.convention.result.Results;
import cn.luowb.shortlink.project.dto.req.TrashSaveReqDTO;
import cn.luowb.shortlink.project.service.TrashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回收站控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "回收站管理")
public class TrashController {
    private final TrashService trashService;

    /**
     * 将链接移动到回收站
     */
    @PostMapping("/api/short-link/v1/trash/save")
    @Operation(summary = "将链接移动到回收站")
    public Result<Void> save(@RequestBody TrashSaveReqDTO requestParam) {
        trashService.saveTrash(requestParam);
        return Results.success();
    }
}
