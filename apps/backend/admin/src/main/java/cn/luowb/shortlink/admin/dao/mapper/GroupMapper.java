package cn.luowb.shortlink.admin.dao.mapper;

import cn.luowb.shortlink.admin.dao.entity.GroupDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 短链接分组持久层
 */
@Mapper
public interface GroupMapper extends BaseMapper<GroupDO> {
}