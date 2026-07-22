package cn.luowb.shortlink.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关安全配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class Config {

    /**
     * 无需登录的请求路径
     */
    private List<String> whitePathList = new ArrayList<>();
}
