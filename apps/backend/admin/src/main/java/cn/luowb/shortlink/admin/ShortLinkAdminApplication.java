package cn.luowb.shortlink.admin;

import cn.luowb.shortlink.common.config.JSONConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@MapperScan("cn.luowb.shortlink.admin.dao.mapper")
@EnableFeignClients("cn.luowb.shortlink.admin.remote")
@Import(JSONConfiguration.class)
@SpringBootApplication
public class ShortLinkAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkAdminApplication.class, args);
    }
}
