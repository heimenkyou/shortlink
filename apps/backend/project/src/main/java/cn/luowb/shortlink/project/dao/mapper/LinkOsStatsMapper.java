package cn.luowb.shortlink.project.dao.mapper;

import cn.luowb.shortlink.project.dao.entity.LinkOsStatsDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接访问统计持久层
 */
@Mapper
public interface LinkOsStatsMapper extends BaseMapper<LinkOsStatsDO> {

    void recordStatus(LinkOsStatsDO statsDO);
}