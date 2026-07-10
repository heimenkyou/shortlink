package cn.luowb.shortlink.admin.controller;

import cn.luowb.shortlink.common.convention.exception.result.Result;
import cn.luowb.shortlink.common.convention.exception.result.Results;
import cn.luowb.shortlink.admin.dto.req.GroupSaveReqDTO;
import cn.luowb.shortlink.admin.dto.req.GroupSortReqDTO;
import cn.luowb.shortlink.admin.dto.req.GroupUpdateReqDTO;
import cn.luowb.shortlink.admin.dto.resp.GroupRespDTO;
import cn.luowb.shortlink.admin.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 短链接分组管理控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "短链接分组管理")
public class GroupController {
    private final GroupService groupService;

    /**
     * 新增短链接分组
     */
    @Operation(summary = "新增短链接分组")
    @PostMapping("/api/short-link/admin/v1/group")
    public Result<Void> save(@RequestBody GroupSaveReqDTO requestParam) {
        groupService.save(requestParam.getName());
        return Results.success();
    }

    /**
     * 查询当前用户的所有短链接分组
     */
    @Operation(summary = "查询当前用户的所有短链接分组")
    @GetMapping("/api/short-link/admin/v1/group")
    public Result<List<GroupRespDTO>> listGroup() {
        return Results.success(groupService.listGroup());
    }

    /**
     * 修改短链接分组
     */
    @Operation(summary = "修改短链接分组")
    @PutMapping("/api/short-link/admin/v1/group")
    public Result<Void> update(@RequestBody GroupUpdateReqDTO requestParam) {
        groupService.update(requestParam);
        return Results.success();
    }

    /**
     * 删除短链接分组
     */
    @Operation(summary = "删除短链接分组")
    @DeleteMapping("/api/short-link/admin/v1/group/{gid}")
    public Result<Void> delete(@PathVariable String gid) {
        groupService.delete(gid);
        return Results.success();
    }

    /**
     * 短链接分组排序
     */
    @Operation(summary = "短链接分组排序")
    @PutMapping("/api/short-link/admin/v1/group/sort")
    public Result<Void> sort(@RequestBody List<GroupSortReqDTO> requestParam) {
        groupService.sort(requestParam);
        return Results.success();
    }
}