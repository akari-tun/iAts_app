package com.tun.app.iats_app.entities;

import com.tun.app.iats_app.service.TrackerAntennaService;

/**
 * 作者：TanTun
 * 时间：2017/12/8
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class Tracker {
    private double mLongitude;
    private double mLatitude;
    private float mAltitude;
    private float mHeading;
    private int mPitching;
    private float mVoltage;
    private int mMode;
    private int mGpsStatus;
    private TrackerAntennaService.TrackerServiceStatus mStatus;

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public float getAltitude() {
        return mAltitude;
    }

    public void setAltitude(float altitude) {
        this.mAltitude = altitude;
    }

    public float getHeading() {
        return mHeading;
    }

    public void setHeading(float heading) {
        this.mHeading = heading;
    }

    public int getPitching() {
        return mPitching;
    }

    public void setPitching(int pitching) {
        this.mPitching = pitching;
    }

    public float getVoltage() {
        return mVoltage;
    }

    public void setVoltage(float voltage) {
        this.mVoltage = voltage;
    }

    public int getMode() {
        return mMode;
    }

    public void setMode(int mode) {
        this.mMode = mode;
    }

    public int getGpsStatus() {
        return mGpsStatus;
    }

    public void setGpsStatus(int status) {
        this.mGpsStatus = status;
    }

    public TrackerAntennaService.TrackerServiceStatus getStatus() {
        return mStatus;
    }

    public void setStatus(TrackerAntennaService.TrackerServiceStatus status) {
        this.mStatus = status;
    }

}
