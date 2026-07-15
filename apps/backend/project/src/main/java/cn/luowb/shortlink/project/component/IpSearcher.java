package cn.luowb.shortlink.project.component;

import cn.luowb.shortlink.common.convention.ServiceException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Slf4j
@Component
public class IpSearcher {

    private Searcher v4Searcher;
    private Searcher v6Searcher;

    @PostConstruct
    public void init() {
        // 1. 加载 IPv4 数据库和 IPv6 数据库
        this.v4Searcher = loadXdb(Version.IPv4, "ip2region/ip2region_v4.xdb");
        this.v6Searcher = loadXdb(Version.IPv6, "ip2region/ip2region_v6.xdb");
    }

    private Searcher loadXdb(Version version, String path) {
        ClassPathResource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            log.error("未找到 ip2region 数据库文件: {}", path);
            throw new ServiceException("系统初始化失败：未找到 IP 数据库文件 " + path);
        }

        try (InputStream in = resource.getInputStream()) {
            LongByteArray buffer = Searcher.loadContentFromInputStream(in);
            log.info("ip2region {} 离线数据库成功加载至内存", version);
            return Searcher.newWithBuffer(version, buffer);
        } catch (Exception e) {
            log.error("加载 ip2region 数据库 [{}] 失败", path, e);
            throw new ServiceException("系统初始化失败：加载 IP 数据库异常", e);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IpInfo {
        private String country = "";
        private String province = "";
        private String city = "";
        private String isp = "";
        private String adcode = ""; // 预留字段，当前默认返回空串 "" 确保索引不失效
    }

    /**
     * 解析 IP 并返回切分好的对象
     */
    public IpInfo searchInfo(String ip) {
        try {
            String region = this.search(ip);
            if (region == null || "未知".equals(region) || region.isEmpty()) {
                return new IpInfo();
            }
            String[] parts = region.split("\\|", -1);

            // 国家|省份|城市|ISP
            String country = parts.length > 0 ? parts[0] : "";
            String province = parts.length > 1 ? parts[1] : "";
            String city = parts.length > 2 ? parts[2] : "";
            String isp = parts.length > 3 ? parts[3] : "";

            String cleanCountry = "0".equals(country) ? "" : country;
            String cleanProvince = "0".equals(province) ? "" : province;
            String cleanCity = "0".equals(city) ? "" : city;
            String cleanIsp = "0".equals(isp) ? "" : isp;
            String adcodePlaceholder = "0".equals(city) ? "" : city;
            return new IpInfo(
                    cleanCountry,
                    cleanProvince,
                    cleanCity,
                    cleanIsp,
                    adcodePlaceholder // 用城市名称作为临时的 adcode 占位，
            );
        } catch (ServiceException se) {
            throw se;
        } catch (Exception e) {
            log.warn("IP 统计解析发生容错降级, ip: {}, reason: {}", ip, e.getMessage());
            return new IpInfo();
        }
    }

    /**
     * 解析 IP 地址（自动识别 IPv4 / IPv6）
     */
    public String search(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return "未知";
        }

        ip = ip.trim();
        try {
            // 简单的双栈路由逻辑
            if (ip.contains(":")) {
                // IPv6 地址
                if (v6Searcher == null) {
                    throw new ServiceException("IP 解析系统异常：IPv6 离线数据库未加载");
                }
                return v6Searcher.search(ip);
            } else {
                // IPv4 地址
                if (v4Searcher == null) {
                    throw new ServiceException("IP 解析系统异常：IPv4 离线数据库未加载");
                }
                return v4Searcher.search(ip);
            }
        } catch (Exception e) {
            log.warn("IP 解析失败: {}, err: {}", ip, e.getMessage());
            throw new ServiceException("IP 解析失败：" + e.getMessage(), e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (v4Searcher != null) {
                v4Searcher.close();
            }
            if (v6Searcher != null) {
                v6Searcher.close();
            }
            log.info("ip2region 资源成功释放");
        } catch (Exception e) {
            log.error("释放 ip2region 资源失败", e);
        }
    }
}