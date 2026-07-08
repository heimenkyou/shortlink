package cn.luowb.shortlink.admin.controller;

import cn.luowb.shortlink.admin.common.convention.result.Result;
import cn.luowb.shortlink.admin.common.convention.result.Results;
import cn.luowb.shortlink.admin.dto.req.UserLoginReqDTO;
import cn.luowb.shortlink.admin.dto.req.UserRegisterDTO;
import cn.luowb.shortlink.admin.dto.req.UserUpdateReqDTO;
import cn.luowb.shortlink.admin.dto.resp.UserLoginRespDTO;
import cn.luowb.shortlink.admin.dto.resp.UserRespDTO;
import cn.luowb.shortlink.admin.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理控制器
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户查询相关接口")
public class UserController {
    private final UserService userService;

    /**
     * 根据用户名查询用户
     */
    @Operation(summary = "根据用户名查询用户")
    @GetMapping("/api/short-link/v1/user/{username}")
    public Result<UserRespDTO> getUserByUsername(@PathVariable String username) {
        return Results.success(userService.getUserByUsername(username));
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册")
    @PostMapping("/api/short-link/v1/user")
    public Result<Void> register(@RequestBody UserRegisterDTO requestParam) {
        userService.register(requestParam);
        return Results.success();
    }

    /**
     * 检查用户名是否存在
     */
    @Operation(summary = "检查用户名是否存在")
    @Tag(name = "用户管理", description = "用户查询相关接口")
    @GetMapping("/api/short-link/v1/user/has-username")
    public Result<Boolean> hasUserName(@RequestParam String username) {
        return Results.success(userService.hasUserName(username));
    }

    /**
     * 修改用户信息
     */
    @Operation(summary = "修改用户信息")
    @PutMapping("/api/short-link/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO requestParam) {
        userService.update(requestParam);
        return Results.success();
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/api/short-link/v1/user/login")
    public Result<UserLoginRespDTO> login(@RequestBody UserLoginReqDTO requestParam) {
        return Results.success(userService.login(requestParam));
    }

    /**
     * 检查用户是否登录
     */
    @Operation(summary = "检查用户是否登录")
    @GetMapping("/api/short-link/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam String token) {
        return Results.success(userService.checkLogin(token));
    }

}