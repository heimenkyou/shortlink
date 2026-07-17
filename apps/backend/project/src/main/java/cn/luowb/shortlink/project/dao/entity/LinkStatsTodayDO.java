package cn.luowb.shortlink.project.dao.entity;

import cn.luowb.shortlink.common.database.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * 短链接今日统计表实体
 */
@Data
@TableName("t_link_stats_today")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LinkStatsTodayDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    @TableField("`date`")
    private LocalDate date;

    /**
     * 今日 PV
     */
    private Integer todayPv;

    /**
     * 今日 UV
     */
    private Integer todayUv;

    /**
     * 今日 IP 数
     */
    private Integer todayUip;
}