package com.tun.app.iats_app.gps;

import android.content.Intent;

/**
 * 作者：TanTun
 * 时间：2018/2/25
 * 邮箱：32965926@qq.com
 * 描述：
 */

public interface GPSPermissionsListener {
    /**
     * 申请所需要的权限
     *
     * @param permissions 权限
     */
    void onRequestPermissions(String[] permissions, int id);
    /**
     * 打开GPS设置界面
     *
     * @param intent 意图
     */
    void onOpenGPS(Intent intent);
}