package com.tun.app.iats_app.utils;

import java.nio.ByteBuffer;

/**
 * 作者：TanTun
 * 时间：2017/2/26
 * 邮箱：32965926@qq.com
 * 描述：Byte数据类型转换
 */

public class ByteUtils {

    private static char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F'};

    /**
     * 作者：TanTun
     * 时间：2017/2/26
     * 邮箱：32965926@qq.com
     * 描述：大小端模式枚举
     */
    public enum Endian {
        Big,
        Little
    }

    /**
     * 将data字节型数据转换为0~255 (0xFF 即BYTE)
     *
     * @param data 有符号byte数据
     * @return 有符号byte的无符号数的int类型的值
     */
    public static int byteToUnsignedByte(byte data) {
        return data & 0x0FF; // 部分编译器会把最高位当做符号位，因此写成0x0FF.
    }

    /**
     * 将字节转换为HEX字符串
     *
     * @param inByte 需要转换的字节
     * @return HEX字符串
     */
    public static String byteToHexString(byte inByte) {
        int i, j, in;
        String out = "";

        for (j = 0; j < 1; ++j) {
            in = inByte & 0xFF;
            i = (in >> 4) & 0x0F;
            out += hex[i];
            i = in & 0x0F;
            out += hex[i];
        }
        return out;
    }

    /**
     * 将HEX字符串转换为字节数组
     *
     * @param str HEX字符串
     * @return 字节数组
     */
    public static byte[] hexStringToByteArray(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    /**
     * 将字节数组转换为HEX字符串
     *
     * @param inArray 需要转换的字节数组
     * @return HEX字符串
     */
    public static String byteArrayToHexString(byte[] inArray) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");

        int i, j, in;

        StringBuffer sb = new StringBuffer(inArray.length * 2);

        for (j = 0; j < inArray.length; ++j) {
            in = inArray[j] & 0xFF;
            i = (in >> 4) & 0x0F;
            sb.append(hex[i]);
            i = in & 0x0F;
            sb.append(hex[i]);
        }
        return sb.toString();
    }

    /**
     * 将字节数组转换为HEX字符串
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @param length  转换个数
     * @return HEX字符串
     */
    public static String byteArrayToHexString(byte[] inArray, int offset, int length) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");
        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");
        if (inArray.length < offset + length)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于转换个数。");

        int i, j, in;
        StringBuffer sb = new StringBuffer(inArray.length * 2);

        for (j = offset; j < offset + length; ++j) {
            in = inArray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            sb.append(hex[i]);
            i = in & 0x0f;
            sb.append(hex[i]);
        }
        return sb.toString();
    }


    /**
     * 将BCD字符串转换为字节数组
     *
     * @param bcd 需要转换的BCD字符串
     * @return 字节数组
     */
    public static byte[] bcdStringToByteArray(String bcd) {
        int len = bcd.length();
        int mod = len % 2;
        if (mod != 0) {
            bcd = "0" + bcd;
            len = bcd.length();
        }
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        byte[] abt = bcd.getBytes();
        int j, k;
        for (int p = 0; p < bcd.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }

    /**
     * 将字节数组转换为BCD字符串
     *
     * @param inArray 需要转换的字节数组
     * @return BCD字符串
     */
    public static String byteArrayToBcdString(byte[] inArray) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");

        StringBuilder sb = new StringBuilder(inArray.length * 2);
        for (byte item : inArray
                ) {
            int h = ((item & 0xff) >> 4) + 48;
            sb.append((char) h);
            int l = (item & 0x0f) + 48;
            sb.append((char) l);
        }
        return sb.toString();
    }

    /**
     * 将字节数组转换为BCD字符串
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @param length  转换个数
     * @return HEX字符串
     */
    public static String byteArrayToBcdString(byte[] inArray, int offset, int length) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");
        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");
        if (inArray.length < offset + length)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于转换个数。");

        StringBuilder sb = new StringBuilder(inArray.length * 2);
        for (int i = offset; i < offset + length; i++) {
            int h = ((inArray[i] & 0xff) >> 4) + 48;
            sb.append((char) h);
            int l = (inArray[i] & 0x0f) + 48;
            sb.append((char) l);
        }
        return sb.toString();
    }

    /**
     * short类型转换为byte[]
     *
     * @param value 需要转换的数据
     * @return ByteArray
     */
    public static byte[] shortToByteArray(short value) {
        return new byte[] {
                (byte) (value & 0xFF),
                (byte) (value  >> 8 & 0xFF)
        };
    }

    /**
     * int类型转换为byte[]
     *
     * @param value 需要转换的数据
     * @return ByteArray
     */
    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte) ((value >> 24) & 0xFF),
                (byte) ((value >> 16) & 0xFF),
                (byte) ((value >> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    /**
     * long类型转换为byte[]
     *
     * @param value 需要转换的数据
     * @return ByteArray
     */
    public static byte[] longToByteArray(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, value);
        return buffer.array();
    }

    /**
     * 将2个byte转换为short类型的数据
     *
     * @param inArray 需要转换的字节数组（必须为2个字节）
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static short byteArrayToShort(byte[] inArray) {
        if (inArray.length != 2)
            throw new IllegalArgumentException("传入数据的长度不等于2个字节。");

        return byteArrayToShort(inArray, Endian.Little);
    }

    /**
     * 从指定位置将指定个数的byte转换为short类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @param length  转换个数（不能大于4）
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static short byteArrayToShort(byte[] inArray, int offset, int length) {
        //默认小端模式
        return byteArrayToShort(inArray, offset, length, Endian.Little);
    }

    /**
     * 将2个byte转换为short类型的数据
     *
     * @param inArray 需要转换的字节数组（必须为2个字节）
     * @param mode    大小端模式
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static short byteArrayToShort(byte[] inArray, Endian mode) {
        if (inArray.length != 2)
            throw new IllegalArgumentException("传入数据的长度不等于2个字节。");

        if (mode == Endian.Big)
            return (short)(inArray[0] & 0xFF | (inArray[1] & 0xFF) << 8);
        else
            return (short)(inArray[1] & 0xFF | (inArray[0] & 0xFF) << 8);
    }

    /**
     * 从指定位置将2个byte转换为Short类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @param mode    大小端模式
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static short byteArrayToShort(byte[] inArray, int offset, Endian mode) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");
        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");
        if (inArray.length < offset + 2)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于2个字节。");

        if (mode == Endian.Big)
            return (short)(inArray[offset] & 0xFF | (inArray[offset + 1] & 0xFF) << 8);
        else
            return (short)(inArray[offset + 1] & 0xFF | (inArray[offset] & 0xFF) << 8);
    }

    /**
     * 从指定位置将指定个数的byte转换为short类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @param length  转换个数（不能大于4）
     * @param mode    大小端模式
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static short byteArrayToShort(byte[] inArray, int offset, int length, Endian mode) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");
        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");
        if (length <= 0)
            throw new IllegalArgumentException("转换的byte个数不能小于1个。");
        if (length > 2)
            throw new IllegalArgumentException("转换的byte个数不能大于2个字节。");
        if (inArray.length < offset + length)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于转换个数。");

        int value = 0;
        int oft = 0;
        if (mode == Endian.Big) {
            for (int i = offset; i < offset + length; i++) {
                value = value | (inArray[i] & 0xFF) << oft;
                oft = oft + 8;
            }
        } else {
            //数组下标从0开始，所以偏移位加数据长度必须减1开始往前循环
            for (int i = offset + length - 1; i >= offset; i--) {
                value = value | (inArray[i] & 0xFF) << oft;
                oft = oft + 8;
            }
        }
        return (short)value;
    }

    /**
     * 将4个byte转换为int类型的数据
     *
     * @param inArray 需要转换的字节数组（必须为4个字节）
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static int byteArrayToInt(byte[] inArray) {
        //默认小端模式
        return byteArrayToInt(inArray, Endian.Little);
    }

    /**
     * 从指定位置将4个byte转换为int类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static int byteArrayToInt(byte[] inArray, int offset) {
        //默认小端模式
        return byteArrayToInt(inArray, offset, Endian.Little);
    }

    /**
     * 从指定位置将指定个数的byte转换为int类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @param length  转换个数（不能大于4）
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static int byteArrayToInt(byte[] inArray, int offset, int length) {
        //默认小端模式
        return byteArrayToInt(inArray, offset, length, Endian.Little);
    }

    /**
     * 将4个byte转换为int类型的数据
     *
     * @param inArray 需要转换的字节数组（必须为4个字节）
     * @param mode    大小端模式
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static int byteArrayToInt(byte[] inArray, Endian mode) {
        if (inArray.length != 4)
            throw new IllegalArgumentException("传入数据的长度不等于4个字节。");

        if (mode == Endian.Big)
            return inArray[3] & 0xFF | (inArray[2] & 0xFF) << 8 |
                    (inArray[1] & 0xFF) << 16 | (inArray[0] & 0xFF) << 24;
        else
            return inArray[0] & 0xFF | (inArray[1] & 0xFF) << 8 |
                    (inArray[2] & 0xFF) << 16 | (inArray[3] & 0xFF) << 24;
    }

    /**
     * 从指定位置将4个byte转换为int类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @param mode    大小端模式
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static int byteArrayToInt(byte[] inArray, int offset, Endian mode) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");
        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");
        if (inArray.length < offset + 4)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于4个字节。");

        if (mode == Endian.Big)
            return inArray[offset + 3] & 0xFF | (inArray[offset + 2] & 0xFF) << 8 |
                    (inArray[offset + 1] & 0xFF) << 16 | (inArray[offset] & 0xFF) << 24;
        else
            return inArray[offset] & 0xFF | (inArray[offset + 1] & 0xFF) << 8 |
                    (inArray[offset + 2] & 0xFF) << 16 | (inArray[offset + 3] & 0xFF) << 24;

    }

    /**
     * 从指定位置将指定个数的byte转换为int类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @param length  转换个数（不能大于4）
     * @param mode    大小端模式
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static int byteArrayToInt(byte[] inArray, int offset, int length, Endian mode) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");
        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");
        if (length <= 0)
            throw new IllegalArgumentException("转换的byte个数不能小于1个。");
        if (length > 4)
            throw new IllegalArgumentException("转换的byte个数不能大于4个字节。");
        if (inArray.length < offset + length)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于转换个数。");

        int value = 0;
        int oft = 0;
        if (mode == Endian.Big) {
            //数组下标从0开始，所以偏移位加数据长度必须减1开始往前循环
            for (int i = offset + length - 1; i >= offset; i--) {
                value = value | (inArray[i] & 0xFF) << oft;
                oft = oft + 8;
            }
        } else {
            for (int i = offset; i < offset + length; i++) {
                value = value | (inArray[i] & 0xFF) << oft;
                oft = oft + 8;
            }
        }
        return value;
    }

    /**
     * 将8个byte转换为long类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @return 长整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static long byteArrayToLong(byte[] inArray) {
        //默认小端模式
        return byteArrayToLong(inArray, Endian.Little);
    }

    /**
     * 从指定位置将8个byte转换为long类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static long byteArrayToLong(byte[] inArray, int offset) {
        //默认小端模式
        return byteArrayToLong(inArray, offset, Endian.Little);
    }

    /**
     * 从指定位置将指定个数的byte转换为long类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @param length  转换个数（不能大于4）
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static long byteArrayToLong(byte[] inArray, int offset, int length) {
        //默认小端模式
        return byteArrayToLong(inArray, offset, length, Endian.Little);
    }

    /**
     * 将8个byte转换为long类型的数据
     *
     * @param inArray 需要转换的字节数组（必须为8个字节）
     * @param mode    大小端模式
     * @return 长整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static long byteArrayToLong(byte[] inArray, Endian mode) {
        if (inArray.length != 8)
            throw new IllegalArgumentException("传入数据的长度不等于8个字节。");

        ByteBuffer buffer = ByteBuffer.allocate(8);

        if (mode == Endian.Big) {
            buffer.put(inArray, 0, inArray.length);
            buffer.flip();
        } else {
            for (int i = inArray.length - 1; i >= 0; i--)
                buffer.put(inArray[i]);
            buffer.flip();
        }

        return buffer.getLong();
    }

    /**
     * 从指定位置将8个byte转换为long类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @param mode    大小端模式
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static long byteArrayToLong(byte[] inArray, int offset, Endian mode) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");
        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");
        if (inArray.length < offset + 8)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于8个字节。");

        ByteBuffer buffer = ByteBuffer.allocate(8);

        if (mode == Endian.Big) {
            buffer.put(inArray, 0, 8);
            buffer.flip();
        } else {
            //数组下标从0开始，所以第8个字节下标是7，从7开始往前循环
            for (int i = offset + 7; i >= offset; i--)
                buffer.put(inArray[i]);
            buffer.flip();
        }

        return buffer.getLong();
    }

    /**
     * 从指定位置将指定个数的byte转换为int类型的数据
     *
     * @param inArray 需要转换的字节数组
     * @param offset  偏移位置
     * @param length  转换个数（不能大于4）
     * @param mode    大小端模式
     * @return 整型数据
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static long byteArrayToLong(byte[] inArray, int offset, int length, Endian mode) {
        if (inArray == null || inArray.length == 0)
            throw new IllegalArgumentException("不能传入空数据。");
        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");
        if (length <= 0)
            throw new IllegalArgumentException("转换的byte个数不能小于1个。");
        if (length > 8)
            throw new IllegalArgumentException("转换的byte个数不能大于8个字节。");
        if (inArray.length < offset + length)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于转换个数。");

        ByteBuffer buffer = ByteBuffer.allocate(8);

        if (mode == Endian.Big) {
            for (int i = offset + length - 8; i < offset + length; i++) {
                if (i < offset) buffer.put((byte) 0x00);
                else buffer.put(inArray[i]);
            }
            buffer.flip();
        } else {
            //数组下标从0开始，所以从偏移位加7开始往前循环
            for (int i = offset + 7; i >= offset; i--) {
                if (i > offset + length - 1) buffer.put((byte) 0x00);
                else buffer.put(inArray[i]);
            }
            buffer.flip();
        }

        return buffer.getLong();
    }

    /**
     * 计算循环校验和（CRC）
     *
     * @param inArray 需要计算校验和的数据
     * @param offset  偏移位置
     * @param length  计算的数据长度
     * @return 计算结果
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static byte calculationSumCrc(byte[] inArray, int offset, int length) {
        if (offset < 0)
            throw new IllegalArgumentException("偏移位置必须大于等于0。");
        if (length <= 0)
            throw new IllegalArgumentException("转换的byte个数不能小于1个。");
        if (inArray.length < offset + length)
            throw new IllegalArgumentException("传入数据从偏移位之后的长度小于计算数据的长度。");

        int sum = 0;

        for (int i = offset; i < offset + length; i++) {
            sum = sum + inArray[i];
        }

        return (byte)(sum);
    }

    /**
     * 计算校验和（CRC）
     *
     * @param inArray 需要计算校验和的数据
     * @param offset  偏移位置
     * @param length  计算的数据长度
     * @return 计算结果
     * @throws IllegalArgumentException 传入的数据不合法
     */
    public static int calculationCRC(byte[] inArray, int offset, int length) {
        int crc = 0;
        for (int i = offset; i < offset + length; i++) {
            crc ^= ByteUtils.byteToUnsignedByte(inArray[i]);
        }
        crc = ~crc;
        return ByteUtils.byteToUnsignedByte((byte)crc);
    }
}
