package com.tun.app.iats_app.protocol;

/**
 * 作者：TanTun
 * 时间：2017/10/18
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class Defines {
    public static final byte TRACKER_MODE_MANUAL = (byte) 0x00;
    public static final byte TRACKER_MODE_AUTO = (byte) 0x01;
    public static final byte TRACKER_MODE_DEBUG = (byte) 0x02;

    public static final byte MAX_TAG_COUNT = (byte) 0x10;            //每帧数据最大TAG数
    public static final byte MAX_CMD_COUNT = (byte) 0x05;            //最大缓存等待发送的指令数

    public static final byte TP_PACKET_LEAD = (byte) 0x24;           //引导码 $
    public static final byte TP_PACKET_START = (byte) 0x54;          //协议头 T

    public static final byte TAG_COUNT = (byte) 0x23;                //TAG数量，定义了新的TAG需要增加这个值

    public static final byte TAG_BASE_ACK = (byte) 0x00;             //应答结果 L:1 V:0成功 非0失败
    public static final byte TAG_BASE_HEARTBEAT = (byte) 0x01;       //心跳数据 L:1
    public static final byte TAG_BASE_QUERY = (byte) 0x02;           //请求命令 L:1

    public static final byte TAG_PLANE_LONGITUDE = (byte) 0x10;      //飞机经度 L:4
    public static final byte TAG_PLANE_LATITUDE = (byte) 0x11;       //飞机纬度 L:4
    public static final byte TAG_PLANE_ALTITUDE = (byte) 0x12;       //飞机高度 L:2
    public static final byte TAG_PLANE_SPEED = (byte) 0x13;          //飞机速度 L:2
    public static final byte TAG_PLANE_DISTANCE = (byte) 0x14;       //飞机距离 L:4
    public static final byte TAG_PLANE_STAR = (byte) 0x15;           //定位星数 L:1
    public static final byte TAG_PLANE_FIX = (byte) 0x16;            //定位类型 L:1
    public static final byte TAG_PLANE_PITCH = (byte) 0x17;          //俯仰角度 L:2
    public static final byte TAG_PLANE_ROLL = (byte) 0x18;           //横滚角度 L:2
    public static final byte TAG_PLANE_HEADING = (byte) 0x19;        //飞机方向 L:2

    public static final byte TAG_HOME_LONGITUDE = (byte) 0x20;       //家的经度 L:4
    public static final byte TAG_HOME_LATITUDE = (byte) 0x21;        //家的纬度 L:4
    public static final byte TAG_HOME_ALTITUDE = (byte) 0x22;        //家的高度 L:2
    public static final byte TAG_HOME_HEADING = (byte) 0x23;         //家的朝向 L:2
    public static final byte TAG_HOME_PITCHING = (byte) 0x24;        //家的俯仰 L:1
    public static final byte TAG_HOME_VOLTAGE = (byte) 0x25;         //家的电压 L:2
    public static final byte TAG_HOME_MODE = (byte) 0x26;            //家的模式 L:1
    public static final byte TAG_HOME_DECLINATION = (byte) 0x27;     //磁偏角 L:1

    public static final byte TAG_PARAM_PID_P = (byte) 0x50;          //PID_P L:2
    public static final byte TAG_PARAM_PID_I = (byte) 0x51;          //PID_I L:2
    public static final byte TAG_PARAM_PID_D = (byte) 0x52;          //PID_D L:2
    public static final byte TAG_PARAM_TILT_0 = (byte) 0x53;         //俯仰零度 L:2
    public static final byte TAG_PARAM_TILT_90 = (byte) 0x54;        //俯仰90度 L:2
    public static final byte TAG_PARAM_PAN_0 = (byte) 0x55;          //水平中立点 L:2
    public static final byte TAG_PARAM_OFFSET = (byte) 0x56;         //罗盘偏移 L:1
    public static final byte TAG_PARAM_START_TRACKING_DISTANCE = (byte) 0x57;      //开始跟踪距离 L:1
    public static final byte TAG_PARAM_MAX_PID_ERROR = (byte) 0x58;  //跟踪偏移度数 L:1
    public static final byte TAG_PARAM_MIN_PAN_SPEED = (byte) 0x59;  //最小舵机速度 L:1
    public static final byte TAG_PARAM_DECLINATION = (byte) 0x5A;    //磁偏角 L:1

    public static final byte TAG_CTR_MODE = (byte) 0x60;             //模式 L:1 0：手动模式 1：自动跟踪： 2调试模式
    public static final byte TAG_CTR_AUTO_POINT_TO_NORTH = (byte) 0x61;  //自动指北 L:1 0：不启用 1：启用
    public static final byte TAG_CTR_CALIBRATE = (byte) 0x62;        //较准 L：1 >0：开始较准
    public static final byte TAG_CTR_HEADING = (byte) 0x63;        //较准 L：1 0~359
    public static final byte TAG_CTR_TILT = (byte) 0x64;        //较准 L：1 0~90

    public static final byte CMD_U_HEARTBEAT = (byte) 0x00;          //心跳
    public static final byte CMD_U_AIRPLANE = (byte) 0x30;           //上传飞机状态
    public static final byte CMD_U_TRACKER = (byte) 0x31;            //上传设备状态
    public static final byte CMD_U_PARAM = (byte) 0x32;              //上传参数
    public static final byte CMD_U_ACK = (byte) 0x33;                //应答结果

    public static final byte CMD_D_HEARTBEAT = (byte) 0x00;          //心跳
    public static final byte CMD_D_SET_HOME = (byte) 0x40;           //设置家
    public static final byte CMD_D_SET_PARA = (byte) 0x41;           //设置参数
    public static final byte CMD_D_QUERY_PARAM = (byte) 0x42;        //请求参数
    public static final byte CMD_D_CONTROL = (byte) 0x43;            //控制指令
}
