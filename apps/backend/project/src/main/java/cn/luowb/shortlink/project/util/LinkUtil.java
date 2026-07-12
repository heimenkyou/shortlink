package cn.luowb.shortlink.project.util;

import cn.luowb.shortlink.project.common.constant.LinkConstant;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 短链接工具类
 */
public class LinkUtil {
    /**
     * 获取短链接缓存时间（毫秒）
     */
    public static long getCacheTime(LocalDateTime validDate) {
        if (validDate == null) {
            return LinkConstant.DEFAULT_CACHE_TIME;
        }
        long cacheTime = ChronoUnit.MILLIS.between(LocalDateTime.now(), validDate);
        return Math.max(0, cacheTime);
    }
}
