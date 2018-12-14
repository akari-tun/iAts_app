package com.tun.app.iats_app.utils;

import java.text.DecimalFormat;

/**
 * 作者：TanTun
 * 时间：2018/7/19
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class DigitsUtils {

    public static final DecimalFormat FORMAT_SEVEN_DIGITS = new DecimalFormat("#.0000000");
    public static final DecimalFormat FORMAT_ONE_DIGITS = new DecimalFormat("#####.0");
    public static final DecimalFormat FORMAT_TWO_DIGITS = new DecimalFormat("#####.00");
    public static final DecimalFormat FORMAT_KMH_DIGITS = new DecimalFormat("#.00 KM/H");
    public static final DecimalFormat FORMAT_KM_DIGITS = new DecimalFormat("#.00 KM");
    public static final DecimalFormat FORMAT_M_DIGITS = new DecimalFormat("# M");
    public static final DecimalFormat FORMAT_O_DIGITS = new DecimalFormat("# °");
    public static final DecimalFormat FORMAT_V_DIGITS = new DecimalFormat("#.00 V");
    public static final DecimalFormat FORMAT_ZERO_DIGITS = new DecimalFormat("#####");
}
