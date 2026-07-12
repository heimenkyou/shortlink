package cn.luowb.shortlink.admin.controller;


import cn.luowb.shortlink.admin.remote.dto.LinkRemoteService;
import cn.luowb.shortlink.admin.remote.dto.req.TrashSaveReqDTO;
import cn.luowb.shortlink.common.convention.result.Result;
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
@Tag(name = "后管回收站管理")
public class TrashController {
    // TODO 以后再改成 feign 调用
    LinkRemoteService linkRemoteService = new LinkRemoteService() {
    };

    /**
     * 将链接移动到回收站
     */
    @PostMapping("/api/short-link/admin/v1/trash/save")
    @Operation(summary = "将链接移动到回收站")
    public Result<Void> save(@RequestBody TrashSaveReqDTO requestParam) {
        return linkRemoteService.saveTrash(requestParam);
    }
}
