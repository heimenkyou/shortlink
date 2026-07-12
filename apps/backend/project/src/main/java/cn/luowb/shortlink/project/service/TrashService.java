package cn.luowb.shortlink.project.service;

import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dto.req.TrashSaveReqDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface TrashService extends IService<LinkDO> {
    /**
     * 将链接移动到回收站
     * @param requestParam 请求参数
     */
    void saveTrash(TrashSaveReqDTO requestParam);
}
