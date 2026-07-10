package cn.luowb.shortlink.project.dao.mapper;

import cn.luowb.shortlink.project.dao.entity.LinkDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接持久层
 */
@Mapper
public interface LinkMapper extends BaseMapper<LinkDO> {
}