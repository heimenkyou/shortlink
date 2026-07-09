package cn.luowb.shortlink.admin.dao.entity;

import cn.luowb.shortlink.admin.dao.entity.base.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 用户实体
 */
@Data
@TableName("t_user")
public class UserDO extends BaseDO {
    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码(加密)
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号(加密)
     */
    private String phone;

    /**
     * 邮箱(加密)
     */
    private String mail;

    /**
     * 注销时间戳
     */
    private Long deletionTime;
}
