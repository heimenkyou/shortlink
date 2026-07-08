create table t_user
(
    id            bigint auto_increment primary key comment 'ID',
    username      varchar(256) null comment '用户名',
    password      varchar(512) null comment '密码(加密)',
    real_name     varchar(256) null comment '真实姓名',
    phone         varchar(128) null comment '手机号(加密)',
    mail          varchar(512) null comment '邮箱(加密)',
    deletion_time bigint       null comment '注销时间戳',
    create_time   datetime     null comment '创建时间',
    update_time   datetime     null comment '修改时间',
    del_flag      tinyint(1)   null comment '删除标识 0: 未删除 1: 已删除',
    constraint t_user_pk unique (username)
) comment '用户表';

