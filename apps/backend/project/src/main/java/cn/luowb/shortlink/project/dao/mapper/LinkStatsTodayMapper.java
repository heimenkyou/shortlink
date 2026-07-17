package cn.luowb.shortlink.project.dao.mapper;

import cn.luowb.shortlink.project.dao.entity.LinkStatsTodayDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接访问统计持久层
 */
@Mapper
public interface LinkStatsTodayMapper extends BaseMapper<LinkStatsTodayDO> {
    /**
     * 记录今日统计监控数据
     */
    void recordStatus(LinkStatsTodayDO linkStatsTodayDO);
}