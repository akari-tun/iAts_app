package com.tun.app.iats_app.protocol;

import com.tun.app.iats_app.utils.ByteUtils;

/**
 * 作者：TanTun
 * 时间：2017/10/20
 * 邮箱：32965926@qq.com
 * 描述：
 */

public enum Cmd {
    CMD_U_HEARTBEAT("心跳", Defines.CMD_U_HEARTBEAT),
    CMD_U_AIRPLANE("上传飞机状态", Defines.CMD_U_AIRPLANE),
    CMD_U_TRACKER("上传设备状态", Defines.CMD_U_TRACKER),
    CMD_U_PARAM("上传参数", Defines.CMD_U_PARAM),
    CMD_U_ACK("应答结果", Defines.CMD_U_ACK),
    CMD_D_HEARTBEAT("心跳", Defines.CMD_D_HEARTBEAT),
    CMD_D_SET_HOME("设置家", Defines.CMD_D_SET_HOME),
    CMD_D_SET_PARA("设置参数", Defines.CMD_D_SET_PARA),
    CMD_D_QUERY_PARAM("请求参数", Defines.CMD_D_QUERY_PARAM),
    CMD_D_CONTROL("控制指令", Defines.CMD_D_CONTROL);

    private String name;
    private byte value;

    Cmd(String name, byte value) {
        this.name = name;
        this.value = value;
    }

    public static String getName(byte value) {
        for (Cmd c : Cmd.values()) {
            if (c.getValue() == value) {
                return c.name;
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
    public String toString(){

        return "[" + getName() + "] " +
                "[V]" + ByteUtils.byteToHexString(getValue()) + " ";
    }

    public static Cmd getCmd(byte value) {
        for (Cmd c : Cmd.values()) {
            if (c.getValue() == value) {
                return c;
            }
        }
        return null;
    }
}
