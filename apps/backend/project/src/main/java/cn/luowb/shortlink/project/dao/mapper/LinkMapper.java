package cn.luowb.shortlink.project.dao.mapper;

import cn.luowb.shortlink.project.dao.entity.LinkDO;
import cn.luowb.shortlink.project.dto.resp.GroupCountQueryRespDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 短链接持久层
 */
@Mapper
public interface LinkMapper extends BaseMapper<LinkDO> {
    /**
     * 查询分组下的短链接数量
     *
     * @param gidList 分组ID列表
     * @return 分组ID列表对应的 分组数量列表
     */
    List<GroupCountQueryRespDTO> selectCountByGid(@Param("gidList") List<String> gidList);

    /**
     * 短链接访问统计自增
     */
    void incrementStats(@Param("gid") String gid,
                        @Param("fullShortUrl") String fullShortUrl,
                        @Param("totalPv") Integer totalPv,
                        @Param("totalUv") Integer totalUv,
                        @Param("totalUip") Integer totalUip);
}