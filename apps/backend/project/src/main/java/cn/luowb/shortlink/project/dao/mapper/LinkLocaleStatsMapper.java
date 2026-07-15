package cn.luowb.shortlink.project.dao.mapper;

import cn.luowb.shortlink.project.dao.entity.LinkLocaleStatsDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接区域统计持久层
 */
@Mapper
public interface LinkLocaleStatsMapper extends BaseMapper<LinkLocaleStatsDO> {

    void recordStatus(LinkLocaleStatsDO stats);
}