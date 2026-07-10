package cn.luowb.shortlink.project.util;

import cn.hutool.core.lang.hash.MurmurHash;
import cn.hutool.core.util.RadixUtil;

/**
 * HASH 工具类
 */
public class HashUtil {

    // 62 位字符集字符串
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    /**
     * 将字符串进行 MurmurHash32 并转换为 62 进制短串
     */
    public static String hashToBase62(String str) {
        int i = MurmurHash.hash32(str);

        // 负数转正数
        long num = i < 0 ? Integer.MAX_VALUE - (long) i : i;

        // 十进制转 62 进制
        return RadixUtil.encode(BASE62_ALPHABET, num);
    }
}