package cn.luowb.shortlink.admin;

public class TableShardingTest {

    static void printTables() {
        String SQL = """
                CREATE TABLE `t_link_goto_%d`
                (
                    `id`             bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                    `gid`            varchar(32)  DEFAULT 'default' COMMENT '分组标识',
                    `full_short_url` varchar(128) DEFAULT NULL COMMENT '完整短链接',
                    PRIMARY KEY (`id`)
                ) ENGINE = InnoDB
                  DEFAULT CHARSET = utf8mb4 COMMENT '链接跳转表_%d';
                """;
        for (int i = 0; i < 16; i++) {
            System.out.printf((SQL) + "%n", i, i);
        }
    }

    public static void main(String[] args) {
        printTables();
    }
}
