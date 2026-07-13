package cn.luowb.shortlink.admin.service;

import cn.luowb.shortlink.admin.remote.dto.req.TrashLinkPageReqDTO;
import cn.luowb.shortlink.admin.remote.dto.resp.LinkPageRespDTO;
import cn.luowb.shortlink.common.convention.result.Result;
import cn.luowb.shortlink.common.dto.PageResult;
import jakarta.validation.Valid;

public interface TrashService {
    /**
     * 根据当前用户拥有的分组，分页查询回收站链接
     *
     * @param requestParam 分页查询请求参数
     * @return 分页查询结果
     */
    Result<PageResult<LinkPageRespDTO>> pageTrashLink(@Valid TrashLinkPageReqDTO requestParam);
}
