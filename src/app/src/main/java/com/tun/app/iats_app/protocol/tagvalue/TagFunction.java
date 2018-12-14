package com.tun.app.iats_app.protocol.tagvalue;

import com.tun.app.iats_app.utils.ByteUtils;
import com.tun.app.iats_app.utils.DateUtils;
import com.tun.app.iats_app.utils.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.util.Date;

/**
 * 作者：TanTun
 * 时间：2017/10/20
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class TagFunction {

    public static int toByte(byte[] data) {
        return data[0];
    }

    public static byte[] toBytes(int len, byte[] data) {
        ByteBuffer buffer = ByteBuffer.allocate(len);
        for (int i = 0; i < data.length; i++) {
            buffer.put(data[i]);
            if (i >= len - 1) break;
        }
        return buffer.array();
    }

    public static int toUnsignedShort(byte[] data) {
        byte[] tmp = new byte[4];
        for (int i = data.length - 1; i >= 0; i--) {
            tmp[i] = data[i];
        }
        return ByteUtils.byteArrayToInt(tmp);
    }

    public static long toUnsignedInt(byte[] data) {
        byte[] tmp = new byte[8];
        for (int i = data.length - 1; i >= 0; i--) {
            tmp[i] = data[i];
        }
        return ByteUtils.byteArrayToLong(tmp);
    }

    public static BigDecimal toBigDecimal(byte[] data) {
        byte[] tmp = new byte[8];
        for (int i = data.length - 1; i >= 0; i--) {
            tmp[i] = data[i];
        }
        return new BigDecimal(ByteUtils.byteArrayToLong(tmp, 0, 3) / 100.00);
    }

    public static Date toShortDateTime(byte[] data) {
        try {
            return DateUtils.YYYYMMDD_SHORT.parse(ByteUtils
                    .byteArrayToBcdString(DateUtils.convertConsumeDateBytes(data, 0)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  new Date();
    }

    public static Date toLongDateTime(byte[] data) {
        try {
            return DateUtils.YYYYMMDDHHMMSS_SHORT.parse(ByteUtils
                    .byteArrayToBcdString(DateUtils.convertConsumeDateBytes(data, 0)) +
                    ByteUtils
                            .byteArrayToBcdString(DateUtils.convertConsumeDateBytes(data, 3)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  new Date();
    }

    public static String toHexString(byte[] data) {
        return ByteUtils.byteArrayToHexString(data);
    }

    public static String toASCII(byte[] data) {
        try {
            return new String(data, 0, data.length, "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return StringUtils.EMPTY;
    }

    public static String toBCD(byte[] data) {
            return ByteUtils.byteArrayToBcdString(data);
    }

    public static byte[] fromByte(byte data) {
        return new byte[] { data };
    }

    public static byte[] fromBytes(byte[] data) {
        return data;
    }

    public static byte[] fromShort(int data) {
        byte[] tmp = ByteUtils.intToByteArray(data);
        return new byte[] { tmp[3], tmp[2] };
    }

    public static byte[] fromUnsignedShort(int data) {
        byte[] tmp = ByteUtils.intToByteArray(data);
        return new byte[] { tmp[3], tmp[2] };
    }

    public static byte[] fromInt(int data) {
        byte[] tmp = ByteUtils.intToByteArray(data);
        return new byte[] { tmp[3], tmp[2], tmp[1], tmp[0] };
    }

    public static byte[] fromUnsignedInt(long data) {
        byte[] tmp = ByteUtils.longToByteArray(data);
        return new byte[] { tmp[7], tmp[6], tmp[5], tmp[4] };
    }

    public static byte[] fromBigDecimal(BigDecimal data) {

        byte[] tmp = ByteUtils.longToByteArray(data.multiply(new BigDecimal(100.00)).longValue());
        return new byte[] { tmp[0], tmp[1], tmp[2], tmp[3] };
    }

    public static byte[] fromShortDateTime(Date data) {
        return ByteUtils.bcdStringToByteArray(DateUtils.YYYYMMDD_SHORT.format(data).substring(2, 7));
    }

    public static byte[] fromLongDateTime(Date data) {
        return ByteUtils.bcdStringToByteArray(DateUtils.YYYYMMDDHHMMSS_SHORT.format(data).substring(2, 13));
    }

    public static byte[] fromHexString(String data) {
        return ByteUtils.hexStringToByteArray(data);
    }

    public static byte[] fromASCII(String data) {
        try {
            return data.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return new byte[data.length()];
    }

    public static byte[] fromBCD(String data) {
        return ByteUtils.bcdStringToByteArray(data);
    }
}
