package cn.luowb.shortlink.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 短链接不存在跳转控制器
 */
@Controller
public class LinkNotFoundController {
    /**
     * 短链接不存在跳转页面
     */
    @GetMapping("/page/notfound")
    public String notFound() {
        return "error/404";
    }
}
