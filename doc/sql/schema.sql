-- 短链接访问统计表
CREATE TABLE `t_link_access_stats`
(
    `id`             bigint                                                        NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci  NULL DEFAULT NULL COMMENT '分组标识',
    `full_short_url` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '完整短链接',
    `date`           date                                                          NULL DEFAULT NULL COMMENT '日期',
    `pv`             int                                                           NULL DEFAULT NULL COMMENT '访问量',
    `uv`             int                                                           NULL DEFAULT NULL COMMENT '独立访问数',
    `uip`            int                                                           NULL DEFAULT NULL COMMENT '独立IP数',
    `hour`           int                                                           NULL DEFAULT NULL COMMENT '小时（-1 表示全天汇总，0-23 表示具体小时）',
    `weekday`        int                                                           NULL DEFAULT NULL COMMENT '星期（0=周一, 1=周二, ..., 6=周日）',
    `create_time`    datetime                                                      NULL DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime                                                      NULL DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1)                                                    NULL DEFAULT NULL COMMENT '删除标识：0 未删除 1 已删除',
    PRIMARY KEY (`id`) USING BTREE,
    -- 保证每天每小时只有一条记录
    UNIQUE INDEX `idx_unique_access_stats` (`full_short_url`, `gid`, `date`, `hour`) USING BTREE
) ENGINE = InnoDB
  CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  ROW_FORMAT = Dynamic COMMENT '短链接访问统计表';

-- 短链接区域统计表
CREATE TABLE `t_link_locale_stats`
(
    `id`             bigint   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '完整短链接',
    `gid`            varchar(32) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '分组标识',
    `date`           date                                    DEFAULT NULL COMMENT '日期',
    `cnt`            int                                     DEFAULT NULL COMMENT '访问量',
    `province`       varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '省份名称',
    `city`           varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '市名称',
    `adcode`         varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '城市编码',
    `country`        varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '国家标识',
    `create_time`    datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime NOT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1)                              DEFAULT NULL COMMENT '删除标识 0表示删除 1表示未删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_locale_stats` (`full_short_url`, `gid`, `date`, `adcode`, `province`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT '短链接区域统计表';

-- 短链接操作系统统计表
CREATE TABLE `t_link_os_stats`
(
    `id`             bigint   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '完整短链接',
    `gid`            varchar(32) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '分组标识',
    `date`           date                                    DEFAULT NULL COMMENT '日期',
    `cnt`            int                                     DEFAULT NULL COMMENT '访问量',
    `os`             varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '操作系统',
    `create_time`    datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime NOT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1)                              DEFAULT NULL COMMENT '删除标识 0表示删除 1表示未删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_locale_stats` (`full_short_url`, `gid`, `date`, `os`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT '短链接操作系统统计表';

-- 短链接浏览器统计表
CREATE TABLE `t_link_browser_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11)      DEFAULT NULL COMMENT '访问量',
    `browser`        varchar(64)  DEFAULT NULL COMMENT '浏览器',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_browser_stats` (`full_short_url`, `gid`, `date`, `browser`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接浏览器统计表';

-- 短链接访问日志表
CREATE TABLE `t_link_access_logs`
(
    `id`             bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '完整短链接',
    `gid`            varchar(32) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '分组标识',
    `user`           varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '用户信息',
    `browser`        varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '浏览器',
    `os`             varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT '操作系统',
    `ip`             varchar(64) COLLATE utf8mb4_general_ci  DEFAULT NULL COMMENT 'IP',
    `create_time`    datetime                                DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime                                DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1)                              DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT '短链接访问日志表'

-- 短链接访问设备统计表
CREATE TABLE `t_link_device_stats`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `date`           date         DEFAULT NULL COMMENT '日期',
    `cnt`            int(11)      DEFAULT NULL COMMENT '访问量',
    `device`         varchar(64)  DEFAULT NULL COMMENT '访问设备',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`       tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_browser_stats` (`full_short_url`, `gid`, `date`, `device`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8mb4 COMMENT '短链接访问设备统计表';

-- 短链接分组表
CREATE TABLE `t_group_0`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_1`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_2`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_3`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_4`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_5`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_6`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_7`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_8`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_9`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_10`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_11`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_12`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_13`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_14`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';

CREATE TABLE `t_group_15`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`         varchar(32)  DEFAULT NULL COMMENT '分组标识',
    `name`        varchar(64)  DEFAULT NULL COMMENT '分组名称',
    `username`    varchar(256) DEFAULT NULL COMMENT '创建分组用户名',
    `sort_order`  int(3)       DEFAULT NULL COMMENT '分组排序',
    `create_time` datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time` datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`    tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_gid` (`gid`, `username`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接分组表';


-- 短链接表
CREATE TABLE `t_link_0`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_1`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_2`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_3`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_4`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_5`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_6`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_7`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_8`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_9`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_10`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_11`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_12`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_13`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_14`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';

CREATE TABLE `t_link_15`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `domain`          varchar(128)                                           DEFAULT NULL COMMENT '域名',
    -- 指定字符集排序规则为 utf8mb4_bin 以区分大小写
    `short_uri`       varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin   DEFAULT NULL COMMENT '短链接',
    `full_short_url`  varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '完整短链接',
    `origin_url`      varchar(1024)                                          DEFAULT NULL COMMENT '原始链接',
    `click_num`       int(11)                                                DEFAULT 0 COMMENT '点击量',
    `gid`             varchar(32)                                            DEFAULT NULL COMMENT '分组标识',
    `favicon`         varchar(256)                                           DEFAULT NULL COMMENT '网站图标',
    `enable_status`   tinyint(1)                                             DEFAULT NULL COMMENT '启用标识 0：已启用 1：未启用',
    `created_type`    tinyint(1)                                             DEFAULT NULL COMMENT '创建类型 0：接口 1：控制台',
    `valid_date_type` tinyint(1)                                             DEFAULT NULL COMMENT '有效期类型 0：永久有效 1：用户自定义',
    `valid_date`      datetime                                               DEFAULT NULL COMMENT '有效期',
    `describe`        varchar(1024)                                          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime                                               DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                                               DEFAULT NULL COMMENT '修改时间',
    `del_flag`        tinyint(1)                                             DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_full_short_url` (`full_short_url`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='短链接表';


CREATE TABLE `t_link_goto_0`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_0';

CREATE TABLE `t_link_goto_1`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_1';

CREATE TABLE `t_link_goto_2`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_2';

CREATE TABLE `t_link_goto_3`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_3';

CREATE TABLE `t_link_goto_4`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_4';

CREATE TABLE `t_link_goto_5`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_5';

CREATE TABLE `t_link_goto_6`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_6';

CREATE TABLE `t_link_goto_7`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_7';

CREATE TABLE `t_link_goto_8`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_8';

CREATE TABLE `t_link_goto_9`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_9';

CREATE TABLE `t_link_goto_10`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_10';

CREATE TABLE `t_link_goto_11`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_11';

CREATE TABLE `t_link_goto_12`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_12';

CREATE TABLE `t_link_goto_13`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_13';

CREATE TABLE `t_link_goto_14`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_14';

CREATE TABLE `t_link_goto_15`
(
    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_15';


-- 用户表
CREATE TABLE `t_user_0`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_1`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_2`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_3`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_4`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_5`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_6`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_7`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_8`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_9`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_10`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_11`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_12`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_13`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_14`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';

CREATE TABLE `t_user_15`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码(加密)',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号(加密)',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱(加密)',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0: 未删除 1: 已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1715030926162935810
  DEFAULT CHARSET = utf8mb4 COMMENT ='用户表';
