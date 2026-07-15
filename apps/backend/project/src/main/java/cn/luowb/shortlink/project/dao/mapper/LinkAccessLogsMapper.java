package cn.luowb.shortlink.project.dao.mapper;

import cn.luowb.shortlink.project.dao.entity.LinkAccessLogsDO;
import cn.luowb.shortlink.project.dto.resp.HighFrequencyIpRespDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 短链接访问统计持久层
 */
@Mapper
public interface LinkAccessLogsMapper extends BaseMapper<LinkAccessLogsDO> {

    /**
     * 查询访问次数最高的 IP
     *
     * @return 高频访问 IP 列表
     */
    @Select("""
            SELECT ip, COUNT(*) AS access_count
            FROM t_link_access_logs
            WHERE ip IS NOT NULL
              AND ip <> ''
              AND del_flag = 0
            GROUP BY ip
            ORDER BY access_count DESC, ip ASC
            LIMIT 10
            """)
    List<HighFrequencyIpRespDTO> listHighFrequencyIp();
}
