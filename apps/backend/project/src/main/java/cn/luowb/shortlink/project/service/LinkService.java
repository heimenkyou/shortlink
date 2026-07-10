package cn.luowb.shortlink.project.service;

import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dto.req.LinkCreateReqDTO;
import cn.luowb.shortlink.project.dto.req.LinkPageReqDTO;
import cn.luowb.shortlink.project.dto.resp.LinkCreateRespDTO;
import cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO;
import com.baomidou.mybatisplus.extension.service.IService;

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
}