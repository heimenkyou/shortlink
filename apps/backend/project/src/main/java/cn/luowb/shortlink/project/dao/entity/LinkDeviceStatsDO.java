package cn.luowb.shortlink.project.dao.entity;

import cn.luowb.shortlink.common.database.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@TableName("t_link_device_stats")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LinkDeviceStatsDO extends BaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String fullShortUrl;

    private String gid;

    private LocalDate date;

    private Integer cnt;

    private String device;
}