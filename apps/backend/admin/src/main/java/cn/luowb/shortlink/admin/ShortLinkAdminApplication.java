package cn.luowb.shortlink.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@MapperScan(
        basePackages = "cn.luowb.shortlink.admin.dao.mapper",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
@EnableFeignClients("cn.luowb.shortlink.admin.remote")
@SpringBootApplication(nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class ShortLinkAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkAdminApplication.class, args);
    }
}
