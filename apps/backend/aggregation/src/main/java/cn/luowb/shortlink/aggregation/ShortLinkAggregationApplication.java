package cn.luowb.shortlink.aggregation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

/**
 * 管理端与项目端的聚合服务入口。
 */
@SpringBootApplication(
        scanBasePackages = {"cn.luowb.shortlink.admin", "cn.luowb.shortlink.project"},
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class
)
public class ShortLinkAggregationApplication {

    /**
     * 启动聚合服务。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkAggregationApplication.class, args);
    }
}
