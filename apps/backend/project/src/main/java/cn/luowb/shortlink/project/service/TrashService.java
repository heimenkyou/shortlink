package cn.luowb.shortlink.project.service;

import cn.luowb.shortlink.common.dto.PageResult;
import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dto.req.TrashLinkPageReqDTO;
import cn.luowb.shortlink.project.dto.req.TrashSaveReqDTO;
import cn.luowb.shortlink.project.dto.resp.LinkPageRespDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TrashService extends IService<LinkDO> {
    /**
     * 将链接移动到回收站
     * @param requestParam 请求参数
     */
    void saveTrash(TrashSaveReqDTO requestParam);

    /**
     * 分页查询回收站中的链接
     * @param requestParam 请求参数
     * @return 分页结果
     */
    PageResult<LinkPageRespDTO> pageTrashLink(TrashLinkPageReqDTO requestParam);
}
