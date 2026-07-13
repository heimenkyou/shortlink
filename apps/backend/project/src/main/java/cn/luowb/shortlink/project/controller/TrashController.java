package cn.luowb.shortlink.project.controller;


import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.convention.result.Results;
import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.dto.req.TrashDeleteReqDTO;
import cn.luowb.shortlink.project.dto.req.TrashLinkPageReqDTO;
import cn.luowb.shortlink.project.dto.req.TrashRecoverReqDTO;
import cn.luowb.shortlink.project.dto.req.TrashSaveReqDTO;
import cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.project.service.TrashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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

    /**
     * 分页查询短链接
     */
    @Operation(summary = "分页查询回收站中的链接")
    @GetMapping("/api/short-link/v1/trash/page")
    public Result<PageResult<LinkPageRespDTO>> pageTrashLink(@Valid TrashLinkPageReqDTO requestParam) {
        return Results.success(trashService.pageTrashLink(requestParam));
    }

    /**
     * 从回收站中恢复链接
     *
     * @param requestParam 恢复链接的请求参数
     * @return 操作结果
     */
    @PostMapping("/api/short-link/v1/trash/recover")
    @Operation(summary = "从回收站中恢复链接")
    public Result<Void> recover(@RequestBody TrashRecoverReqDTO requestParam) {
        trashService.recoverTrash(requestParam);
        return Results.success();
    }

    /**
     * 从回收站删除链接
     */
    @PostMapping("/api/short-link/v1/trash/delete")
    @Operation(summary = "从回收站删除链接")
    public Result<Void> delete(@RequestBody TrashDeleteReqDTO requestParam) {
        trashService.deleteTrash(requestParam);
        return Results.success();
    }
}
