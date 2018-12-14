package com.tun.app.iats_app.protocol;

/**
 * 作者：TanTun
 * 时间：2017/11/5
 * 邮箱：32965926@qq.com
 * 描述：
 */

public enum DataState {
    IDLE,
    STATE_LEAD,
    STATE_START,
    STATE_CMD,
    STATE_INDEX,
    STATE_LEN,
    STATE_DATA
}
