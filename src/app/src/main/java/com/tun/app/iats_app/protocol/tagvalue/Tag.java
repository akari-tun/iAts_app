package com.tun.app.iats_app.protocol.tagvalue;

import com.tun.app.iats_app.protocol.Defines;
import com.tun.app.iats_app.utils.ByteUtils;
import com.tun.app.iats_app.utils.StringUtils;

/**
 * 作者：TanTun
 * 时间：2017/10/18
 * 邮箱：32965926@qq.com
 * 描述：TAG枚举定义
 */

public enum Tag {
    TAG_BASE_ACK("应答结果", Defines.TAG_BASE_ACK, (byte)0x00),                 //应答结果 L:1 V:0 失败，非0对应应答的命令
    TAG_BASE_HEARTBEAT("心跳数据", Defines.TAG_BASE_HEARTBEAT, (byte)0x01),     //应答结果 L:1
    TAG_BASE_QUERY("请求命令", Defines.TAG_BASE_QUERY, (byte)0x02),             //请求命令 L:1

    TAG_PLANE_LONGITUDE("飞机经度", Defines.TAG_PLANE_LONGITUDE, (byte)0x04),   //飞机经度 L:4
    TAG_PLANE_LATITUDE("飞机纬度", Defines.TAG_PLANE_LATITUDE, (byte)0x04),     //飞机纬度 L:4
    TAG_PLANE_ALTITUDE("飞机高度", Defines.TAG_PLANE_ALTITUDE, (byte)0x02),     //飞机高度 L:2
    TAG_PLANE_SPEED("飞机速度", Defines.TAG_PLANE_SPEED, (byte)0x02),           //飞机速度 L:2
    TAG_PLANE_DISTANCE("飞机距离", Defines.TAG_PLANE_DISTANCE, (byte)0x04),     //飞机距离 L:4
    TAG_PLANE_STAR("定位星数", Defines.TAG_PLANE_STAR, (byte)0x01),             //定位星数 L:1
    TAG_PLANE_FIX("定位类型", Defines.TAG_PLANE_FIX, (byte)0x01),               //定位类型 L:1
    TAG_PLANE_PITCH("飞机速度", Defines.TAG_PLANE_PITCH, (byte)0x02),           //俯仰角度 L:2
    TAG_PLANE_ROLL("飞机速度", Defines.TAG_PLANE_ROLL, (byte)0x02),             //横滚角度 L:2
    TAG_PLANE_HEADING("飞机速度", Defines.TAG_PLANE_HEADING, (byte)0x02),       //飞机方向 L:2

    TAG_HOME_LONGITUDE("家的经度", Defines.TAG_HOME_LONGITUDE, (byte)0x04),     //家的经度 L:4
    TAG_HOME_LATITUDE("家的纬度", Defines.TAG_HOME_LATITUDE, (byte)0x04),       //家的纬度 L:4
    TAG_HOME_ALTITUDE("家的高度", Defines.TAG_HOME_ALTITUDE, (byte)0x02),       //家的高度 L:2
    TAG_HOME_HEADING("家的朝向", Defines.TAG_HOME_HEADING, (byte)0x02),         //家的朝向 L:2
    TAG_HOME_PITCHING("家的俯仰", Defines.TAG_HOME_PITCHING, (byte)0x01),       //家的俯仰 L:1
    TAG_HOME_VOLTAGE("家的电压", Defines.TAG_HOME_VOLTAGE, (byte)0x02),         //家的电压 L:2
    TAG_HOME_MODE("家的模式", Defines.TAG_HOME_MODE, (byte)0x01),               //家的模式 L:1
    TAG_HOME_DECLINATION("磁偏角", Defines.TAG_HOME_DECLINATION, (byte)0x01),   //家的模式 L:1

    TAG_PARAM_PID_P("PID_P", Defines.TAG_PARAM_PID_P, (byte)0x02),              //PID_P L:2
    TAG_PARAM_PID_I("PID_I", Defines.TAG_PARAM_PID_I, (byte)0x02),              //PID_I L:2
    TAG_PARAM_PID_D("PID_D", Defines.TAG_PARAM_PID_D, (byte)0x02),              //PID_D L:2
    TAG_PARAM_TILT_0("俯仰零度", Defines.TAG_PARAM_TILT_0, (byte)0x02),          //俯仰零度 L:2
    TAG_PARAM_TILT_90("俯仰90度", Defines.TAG_PARAM_TILT_90, (byte)0x02),        //俯仰90度 L:2
    TAG_PARAM_PAN_0("水平中立点", Defines.TAG_PARAM_PAN_0, (byte)0x02),           //水平中立点 L:2
    TAG_PARAM_OFFSET("罗盘偏移", Defines.TAG_PARAM_OFFSET, (byte)0x02),          //罗盘偏移 L:1
    TAG_PARAM_START_TRACKING_DISTANCE("开始跟踪距离", Defines.TAG_PARAM_START_TRACKING_DISTANCE, (byte)0x01),   //开始跟踪距离 L:1
    TAG_PARAM_MAX_PID_ERROR("跟踪偏移度数", Defines.TAG_PARAM_MAX_PID_ERROR, (byte)0x01),               //跟踪偏移度数 L:1
    TAG_PARAM_MIN_PAN_SPEED("最小舵机速度", Defines.TAG_PARAM_MIN_PAN_SPEED, (byte)0x01),               //最小舵机速度 L:1
    TAG_PARAM_DECLINATION("磁偏角", Defines.TAG_PARAM_DECLINATION, (byte)0x01),   //磁偏角 L:1

    TAG_CTR_MODE("设置模式", Defines.TAG_CTR_MODE, (byte)0x01),               //模式 L:1 0：手动模式 1：自动跟踪： 2调试模式
    TAG_CTR_AUTO_POINT_TO_NORTH("自动指北", Defines.TAG_CTR_AUTO_POINT_TO_NORTH, (byte)0x01),               //自动指北 L:1 0：不启用 1：启用
    TAG_CTR_CALIBRATE("较准", Defines.TAG_CTR_CALIBRATE, (byte)0x01),   //较准 L：1 >0：开始较准
    TAG_CTR_HEADING("指向", Defines.TAG_CTR_HEADING, (byte)0x02),   //指向 L：2 0~359
    TAG_CTR_TILT("俯仰", Defines.TAG_CTR_TILT, (byte)0x01);   //俯仰 L：1 0~90

    private String name;
    private byte value;
    private byte len;

    Tag(String name, byte value, byte len) {
        this.name = name;
        this.value = value;
        this.len = len;
    }

    public static String getName(byte value) {
        for (Tag c : Tag.values()) {
            if (c.getValue() == value) {
                return c.name;
            }
        }
        return null;
    }

    public static Tag getTag(byte value) {
        for (Tag c : Tag.values()) {
            if (c.getValue() == value) {
                return c;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public byte getValue() {
        return value;
    }
    public void setValue(byte index) {
        this.value = index;
    }
    public byte getLength() {
        return len;
    }

    public Object byteArrayToValue(byte[] data) {
        switch (this) {
            case TAG_BASE_ACK: return TagFunction.toByte(data);
            case TAG_BASE_HEARTBEAT: return TagFunction.toByte(data);
            case TAG_BASE_QUERY: return TagFunction.toByte(data);

            case TAG_PLANE_LONGITUDE: return TagFunction.toUnsignedInt(data);
            case TAG_PLANE_LATITUDE: return TagFunction.toUnsignedInt(data);
            case TAG_PLANE_ALTITUDE: return ByteUtils.byteArrayToInt(data);
            case TAG_PLANE_SPEED: return TagFunction.toUnsignedShort(data);
            case TAG_PLANE_DISTANCE: return TagFunction.toUnsignedInt(data);
            case TAG_PLANE_STAR: return TagFunction.toByte(data);
            case TAG_PLANE_FIX: return TagFunction.toByte(data);
            case TAG_PLANE_PITCH: return TagFunction.toUnsignedShort(data);
            case TAG_PLANE_ROLL: return TagFunction.toUnsignedShort(data);
            case TAG_PLANE_HEADING: return TagFunction.toUnsignedShort(data);

            case TAG_HOME_LONGITUDE: return TagFunction.toUnsignedInt(data);
            case TAG_HOME_LATITUDE: return TagFunction.toUnsignedInt(data);
            case TAG_HOME_ALTITUDE: return ByteUtils.byteArrayToInt(data);
            case TAG_HOME_HEADING: return TagFunction.toUnsignedShort(data);
            case TAG_HOME_PITCHING: return TagFunction.toUnsignedShort(data);
            case TAG_HOME_VOLTAGE: return TagFunction.toUnsignedInt(data);
            case TAG_HOME_MODE: return TagFunction.toByte(data);
            case TAG_HOME_DECLINATION: return TagFunction.toByte(data);

            case TAG_PARAM_PID_P: return TagFunction.toUnsignedShort(data);
            case TAG_PARAM_PID_I: return TagFunction.toUnsignedShort(data);
            case TAG_PARAM_PID_D: return TagFunction.toUnsignedShort(data);
            case TAG_PARAM_TILT_0: return TagFunction.toUnsignedShort(data);
            case TAG_PARAM_TILT_90: return TagFunction.toUnsignedShort(data);
            case TAG_PARAM_PAN_0: return TagFunction.toUnsignedShort(data);
            case TAG_PARAM_OFFSET: return ByteUtils.byteArrayToShort(data);
            case TAG_PARAM_START_TRACKING_DISTANCE: return TagFunction.toByte(data);
            case TAG_PARAM_MAX_PID_ERROR: return TagFunction.toByte(data);
            case TAG_PARAM_MIN_PAN_SPEED: return TagFunction.toByte(data);
            case TAG_PARAM_DECLINATION: return TagFunction.toByte(data);

            case TAG_CTR_MODE: return TagFunction.toByte(data);
            case TAG_CTR_AUTO_POINT_TO_NORTH: return TagFunction.toByte(data);
            case TAG_CTR_CALIBRATE: return TagFunction.toByte(data);
            case TAG_CTR_HEADING: return TagFunction.toUnsignedShort(data);
            case TAG_CTR_TILT: return TagFunction.toByte(data);
        }

        return null;
    }
    public byte[] stringToValue(String data) {
        switch (this) {
            case TAG_BASE_ACK: return TagFunction.fromByte(Byte.valueOf(data));
            case TAG_BASE_HEARTBEAT: return TagFunction.fromByte(Byte.valueOf(data));
            case TAG_BASE_QUERY: return TagFunction.fromByte(Byte.valueOf(data));

            case TAG_PLANE_LONGITUDE: return TagFunction.fromUnsignedInt(Long.valueOf(data));
            case TAG_PLANE_LATITUDE: return TagFunction.fromUnsignedInt(Long.valueOf(data));
            case TAG_PLANE_ALTITUDE: return TagFunction.fromInt(Integer.valueOf(data));
            case TAG_PLANE_SPEED: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_PLANE_DISTANCE: return TagFunction.fromUnsignedInt(Long.valueOf(data));
            case TAG_PLANE_STAR: return TagFunction.fromByte(Byte.valueOf(data));
            case TAG_PLANE_FIX: return TagFunction.fromByte(Byte.valueOf(data));
            case TAG_PLANE_PITCH: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_PLANE_ROLL: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_PLANE_HEADING: return TagFunction.fromUnsignedShort(Integer.valueOf(data));

            case TAG_HOME_LONGITUDE: return TagFunction.fromUnsignedInt(Long.valueOf(data));
            case TAG_HOME_LATITUDE: return TagFunction.fromUnsignedInt(Long.valueOf(data));
            case TAG_HOME_ALTITUDE: return TagFunction.fromInt(Integer.valueOf(data));
            case TAG_HOME_HEADING: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_HOME_PITCHING: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_HOME_VOLTAGE: return TagFunction.fromUnsignedInt(Long.valueOf(data));
            case TAG_HOME_MODE: return TagFunction.fromByte(Byte.valueOf(data));
            case TAG_HOME_DECLINATION: return TagFunction.fromByte(Byte.valueOf(data));

            case TAG_PARAM_PID_P: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_PARAM_PID_I: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_PARAM_PID_D: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_PARAM_TILT_0: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_PARAM_TILT_90: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_PARAM_PAN_0: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_PARAM_OFFSET: return ByteUtils.shortToByteArray(Short.valueOf(data));
            case TAG_PARAM_START_TRACKING_DISTANCE: return TagFunction.fromByte(Byte.valueOf(data));
            case TAG_PARAM_MAX_PID_ERROR: return TagFunction.fromByte(Byte.valueOf(data));
            case TAG_PARAM_MIN_PAN_SPEED: return TagFunction.fromByte(Byte.valueOf(data));
            case TAG_PARAM_DECLINATION: return TagFunction.fromByte(Byte.valueOf(data));

            case TAG_CTR_MODE: return TagFunction.fromByte(Byte.valueOf(data));
            case TAG_CTR_AUTO_POINT_TO_NORTH: return TagFunction.fromByte(Byte.valueOf(data));
            case TAG_CTR_CALIBRATE: return TagFunction.fromByte(Byte.valueOf(data));
            case TAG_CTR_HEADING: return TagFunction.fromUnsignedShort(Integer.valueOf(data));
            case TAG_CTR_TILT: return TagFunction.fromByte(Byte.valueOf(data));
        }

        return null;
    }

    public byte[] valueToByteArray(Object value) {
        switch (this) {
            case TAG_BASE_ACK: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
            case TAG_BASE_HEARTBEAT: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
            case TAG_BASE_QUERY: return TagFunction.fromByte((byte)((Integer)value & 0xFF));

            case TAG_PLANE_LONGITUDE: return TagFunction.fromUnsignedInt((Long)value);
            case TAG_PLANE_LATITUDE: return TagFunction.fromUnsignedInt((Long)value);
            case TAG_PLANE_ALTITUDE: return TagFunction.fromInt((int)value);
            case TAG_PLANE_SPEED: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_PLANE_DISTANCE: return TagFunction.fromUnsignedInt((Long)value);
            case TAG_PLANE_STAR: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
            case TAG_PLANE_FIX: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
            case TAG_PLANE_PITCH: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_PLANE_ROLL: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_PLANE_HEADING: return TagFunction.fromUnsignedShort((Integer)value);

            case TAG_HOME_LONGITUDE: return TagFunction.fromUnsignedInt((Long)value);
            case TAG_HOME_LATITUDE: return TagFunction.fromUnsignedInt((Long)value);
            case TAG_HOME_ALTITUDE: return TagFunction.fromInt((Integer)value);
            case TAG_HOME_HEADING: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_HOME_PITCHING: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_HOME_VOLTAGE: return TagFunction.fromUnsignedInt((Long)value);
            case TAG_HOME_MODE: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
            case TAG_HOME_DECLINATION: return TagFunction.fromByte((byte)((Integer)value & 0xFF));

            case TAG_PARAM_PID_P: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_PARAM_PID_I: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_PARAM_PID_D: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_PARAM_TILT_0: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_PARAM_TILT_90: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_PARAM_PAN_0: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_PARAM_OFFSET: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_PARAM_START_TRACKING_DISTANCE: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
            case TAG_PARAM_MAX_PID_ERROR: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
            case TAG_PARAM_MIN_PAN_SPEED: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
            case TAG_PARAM_DECLINATION: return TagFunction.fromByte((byte)((Integer)value & 0xFF));

            case TAG_CTR_MODE: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
            case TAG_CTR_AUTO_POINT_TO_NORTH: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
            case TAG_CTR_CALIBRATE: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
            case TAG_CTR_HEADING: return TagFunction.fromUnsignedShort((Integer)value);
            case TAG_CTR_TILT: return TagFunction.fromByte((byte)((Integer)value & 0xFF));
        }

        return null;
    }
    public String format(Object value) {
        switch (this) {
            case TAG_BASE_ACK: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
            case TAG_BASE_HEARTBEAT: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
            case TAG_BASE_QUERY: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));

            case TAG_PLANE_LONGITUDE: return Long.toString((Long)value);
            case TAG_PLANE_LATITUDE: return Long.toString((Long)value);
            case TAG_PLANE_ALTITUDE: return Integer.toString((int)value);
            case TAG_PLANE_SPEED: return Integer.toString((Integer)value);
            case TAG_PLANE_DISTANCE: return Long.toString((Long)value);
            case TAG_PLANE_STAR: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
            case TAG_PLANE_FIX: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
            case TAG_PLANE_PITCH: return Integer.toString((Integer)value);
            case TAG_PLANE_ROLL: return Integer.toString((Integer)value);
            case TAG_PLANE_HEADING: return Integer.toString((Integer)value);

            case TAG_HOME_LONGITUDE: return Long.toString((Long)value);
            case TAG_HOME_LATITUDE: return Long.toString((Long)value);
            case TAG_HOME_ALTITUDE: return Integer.toString((Integer)value);
            case TAG_HOME_HEADING: return Integer.toString((Integer)value);
            case TAG_HOME_PITCHING: return Long.toString((Integer)value);
            case TAG_HOME_VOLTAGE: return Long.toString((Long)value);
            case TAG_HOME_MODE: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
            case TAG_HOME_DECLINATION: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));

            case TAG_PARAM_PID_P: return Integer.toString((Integer)value);
            case TAG_PARAM_PID_I: return Integer.toString((Integer)value);
            case TAG_PARAM_PID_D: return Integer.toString((Integer)value);
            case TAG_PARAM_TILT_0: return Integer.toString((Integer)value);
            case TAG_PARAM_TILT_90: return Integer.toString((Integer)value);
            case TAG_PARAM_PAN_0: return Integer.toString((Integer)value);
            case TAG_PARAM_OFFSET: return Integer.toString((Integer)value);
            case TAG_PARAM_START_TRACKING_DISTANCE: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
            case TAG_PARAM_MAX_PID_ERROR: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
            case TAG_PARAM_MIN_PAN_SPEED: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
            case TAG_PARAM_DECLINATION: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));

            case TAG_CTR_MODE: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
            case TAG_CTR_AUTO_POINT_TO_NORTH: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
            case TAG_CTR_CALIBRATE: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
            case TAG_CTR_HEADING: return Integer.toString((Integer)value);
            case TAG_CTR_TILT: return Integer.toString(ByteUtils.byteToUnsignedByte((byte)((Integer)value & 0xFF)));
        }

        return StringUtils.EMPTY;
    }
}
