package com.tun.app.iats_app.service;

import android.location.Location;

import com.tun.app.iats_app.entities.Airplane;
import com.tun.app.iats_app.entities.Tracker;
import com.tun.app.iats_app.gps.GPSProviderStatus;

/**
 * 作者：TanTun
 * 时间：2018/7/15
 * 邮箱：32965926@qq.com
 * 描述：
 */

public interface TrackerAntennaServiceListener {

    /**
     * 方法描述：位置信息发生改变时被调用
     *
     * @param location 更新位置后的新的Location对象
     */
    void UpdateLocation(Location location);

    /**
     * 方法描述：provider定位源类型变化时被调用
     *
     * @param provider provider的类型
     * @param status   provider状态
     */
    void UpdateGpsStatus(String provider, int status);

    /**
     * 方法描述：GPS状态发生改变时被调用（GPS手动启动、手动关闭、GPS不在服务区、GPS占时不可用、GPS可用)
     *
     * @param gpsStatus 详见{@link GPSProviderStatus}
     */
    void UpdateGpsProviderStatus(int gpsStatus);

    /**
     * 方法描述：更新飞机数据
     *
     * @param plane 详见{@link Airplane}
     */
    void UpdatePlaneData(Airplane plane);

    /**
     * 方法描述：更新云台数据
     *
     * @param tracker 详见{@link Tracker}
     */
    void UpdateTrackerData(Tracker tracker);

    /**
     * 方法描述：更新服务状态
     *
     * @param status 详见{@link TrackerAntennaService.TrackerServiceStatus}
     */
    void UpdateServiceStatus(TrackerAntennaService.TrackerServiceStatus status);

    /**
     * 显示消息
     *
     * @param info 消息
     */
    void OnMessageShow(String info);
}
