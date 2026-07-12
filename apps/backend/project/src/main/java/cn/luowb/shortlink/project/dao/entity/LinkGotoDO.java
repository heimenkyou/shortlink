package cn.luowb.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("t_link_goto")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkGotoDO {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分组ID
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
