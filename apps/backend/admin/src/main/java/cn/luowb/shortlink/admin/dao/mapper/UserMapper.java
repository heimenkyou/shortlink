package cn.luowb.shortlink.admin.dao.mapper;

import cn.luowb.shortlink.admin.dao.entity.UserDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户持久层
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDO> {
}
