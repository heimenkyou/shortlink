package cn.luowb.shortlink.project.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.luowb.shortlink.project.dto.resp.WebsiteMetadataRespDTO;
import cn.luowb.shortlink.project.service.UrlMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@Slf4j
public class UrlMetadataServiceImpl implements UrlMetadataService {
    /**
     * 从URL中提取网站元数据
     *
     * @param url 网站URL
     * @return 网站元数据DTO
     */
    public WebsiteMetadataRespDTO fetchMetadata(String url) {
        String title = "未知网站";
        String iconUrl = null;

        try {
            // 1. 发起请求并获取 HTML
            // 设置 User-Agent，防止部分网站返回 403 拒绝访问
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(4000)
                    .followRedirects(true)
                    .get();

            // 2. 解析标题
            if (!doc.title().isEmpty()) {
                title = doc.title();
            }

            // 3. 解析图标 - 匹配标准的 icon 或苹果专用的 icon
            Element iconEl = doc.selectFirst("link[rel~=(?i)^(shortcut )?icon], link[rel~=(?i)^apple-touch-icon]");
            if (iconEl != null) {
                // 将相对路径转为绝对路径
                iconUrl = iconEl.absUrl("href");
            }
        } catch (Exception e) {
            // 异常处理：允许抓取失败，失败时走兜底逻辑
            log.info("从URL {} 提取元数据失败，异常信息： {}", url, e.getMessage());
        }

        // 5. 兜底策略：没抓到 icon 时，直接使用域名根目录下的 /favicon.ico
        if (StrUtil.isBlank(iconUrl)) {
            try {
                URI uri = new URI(url);
                iconUrl = uri.getScheme() + "://" + uri.getHost() + "/favicon.ico";
            } catch (Exception ignored) {
                iconUrl = null;
            }
        }

        return WebsiteMetadataRespDTO.builder().title(title).iconUrl(iconUrl).build();
    }
}