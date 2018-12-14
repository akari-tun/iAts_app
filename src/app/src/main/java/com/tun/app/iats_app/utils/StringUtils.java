package com.tun.app.iats_app.utils;

/**
 * 作者：TanTun
 * 时间：2017/2/22
 * 邮箱：32965926@qq.com
 * 描述：STRING工具类
 */

public class StringUtils {
    public static final String EMPTY = "";

    /**
     * 判断字符串为Null或者Empty
     *
     * @param str 传入的字符串
     * @return 判断结果
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
