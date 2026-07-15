package cn.luowb.shortlink.project.service;

import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.project.dto.req.LinkPageReqDTO;
import cn.luowb.shortlink.project.dto.req.LinkUpdateReqDTO;
import cn.luowb.shortlink.project.dto.resp.GroupCountQueryRespDTO;
import cn.luowb.shortlink.project.dto.resp.HighFrequencyIpRespDTO;
import cn.luowb.shortlink.project.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 短链接服务接口
 */
public interface LinkService extends IService<LinkDO> {

    /**
     * 创建短链接
     *
     * @param requestParam 创建短链接请求参数
     * @return 创建短链接响应参数
     */
    LinkCreateRespDTO createShortLink(LinkCreateReqDTO requestParam);

    /**
     * 分页查询短链接
     *
     * @param requestParam 分页查询短链接请求参数
     * @return 分页查询短链接结果集
     */
    PageResult<LinkPageRespDTO> pageShortLink(LinkPageReqDTO requestParam);

    /**
     * 查询短链接分组下的短链接数量
     *
     * @param gidList 分组ID列表
     * @return 分组ID列表对应的 分组数量列表
     */
    List<GroupCountQueryRespDTO> groupShortLinkCount(List<String> gidList);

    /**
     * 查询访问次数最高的 IP
     *
     * @return 高频访问 IP 列表
     */
    List<HighFrequencyIpRespDTO> listHighFrequencyIp();

    /**
     * 修改短链接
     * @param requestParam 修改短链接请求参数
     */
    void updateShortLink(LinkUpdateReqDTO requestParam);

    /**
     * 解析短链接
     *
     * @param shortUrl 短链接后缀
     * @param request  HTTP请求
     * @param response
     * @return 原始链接
     */
    String resolveShortUrl(String shortUrl, HttpServletRequest request, HttpServletResponse response);
}
