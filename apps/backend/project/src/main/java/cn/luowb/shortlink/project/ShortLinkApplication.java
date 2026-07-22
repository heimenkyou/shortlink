package cn.luowb.shortlink.project;

import cn.luowb.shortlink.common.config.JSONConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@MapperScan("cn.luowb.shortlink.project.dao.mapper")
@Import(JSONConfiguration.class)
@SpringBootApplication
public class ShortLinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkApplication.class, args);
    }
}
