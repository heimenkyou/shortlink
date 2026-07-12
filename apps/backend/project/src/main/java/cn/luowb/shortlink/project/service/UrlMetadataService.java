package cn.luowb.shortlink.project.service;


import cn.luowb.shortlink.project.dto.resp.WebsiteMetadataRespDTO;

public interface UrlMetadataService {
    /**
     * 从URL中提取网站元数据
     *
     * @param url 网站URL
     * @return 网站元数据DTO
     */
    WebsiteMetadataRespDTO fetchMetadata(String url);
}