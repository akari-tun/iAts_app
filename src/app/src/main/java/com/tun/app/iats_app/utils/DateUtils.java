package com.tun.app.iats_app.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 作者：TanTun
 * 时间：2017/2/27
 * 邮箱：32965926@qq.com
 * 描述：卡上数据转日期
 */

public class DateUtils {
    public static final SimpleDateFormat YYYYMMDD_SHORT
            = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
    public static final SimpleDateFormat YYYYMMDD_LONG
            = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

    public static final SimpleDateFormat YYYYMMDDHHMMSS_SHORT
            = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);
    public static final SimpleDateFormat YYYYMMDDHHMMSS_LONG
            = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    /**
     * 转换卡上的卡片有效期
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @return 日期字节数组（需要再转为HEX字符串）
     * @throws IllegalArgumentException
     */
    public static byte[] convertVailDateBytes(byte[] inArray, int offset) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");

        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");

        if (inArray.length < offset + 2)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于2个字节。");

        byte year = (byte) ((inArray[offset] & 0xF0) >> 4);
        byte month = (byte) (inArray[offset] & 0x0F);
        byte day = (byte) (inArray[offset + 1] & 0x1F);
        byte temp = (byte) (inArray[offset + 1] & 0xE0);
        year = (byte) (year | temp >> 1);

        byte[] outArray = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        outArray[0] = (byte) (0x20 + year / 100);
        outArray[1] = (byte) (year % 100 / 10 * 16 + year % 100 % 10);
        outArray[2] = (byte) (month / 10 * 16 + month % 10);
        outArray[3] = (byte) (day / 10 * 16 + day % 10);
        return outArray;
    }

    /**
     * 转换卡上的上次交易日期
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @return 日期字节数组（需要再转为HEX字符串）
     * @throws IllegalArgumentException
     */
    public static byte[] convertConsumeDateBytes(byte[] inArray, int offset) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");

        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");

        if (inArray.length < offset + 3)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于3个字节。");

        byte[] outArray = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00};
        outArray[0] = (byte) (0x20 + inArray[offset] / 100);
        outArray[1] = (byte) (inArray[offset] % 100 / 10 * 16 + inArray[offset] % 100 % 10);
        outArray[2] = (byte) (inArray[offset + 1] / 10 * 16 + inArray[offset + 1] % 10);
        outArray[3] = (byte) (inArray[offset + 2] / 10 * 16 + inArray[offset + 2] % 10);
        return outArray;
    }

    /**
     * 转换卡上的交易时间
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @return 日期字节数组（需要再转为HEX字符串）
     * @throws IllegalArgumentException
     */
    public static byte[] convertConsumeTimeBytes(byte[] inArray, int offset) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");

        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");

        if (inArray.length < offset + 2)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于2个字节。");

        byte[] outArray = new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x00};
        outArray[0] = (byte) (inArray[offset] / 10 * 16 + inArray[offset] % 10);
        outArray[1] = (byte) (inArray[offset + 1] / 10 * 16 + inArray[offset + 1] % 10);
        return outArray;
    }
}
