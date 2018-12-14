package com.tun.app.iats_app.entities;

/**
 * 作者：TanTun
 * 时间：2017/12/8
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class Airplane {
    private double mLongitude;
    private double mLatitude;
    private float mAltitude;
    private float mHeading;
    private long mDistance;
    private int mSpeed;
    private int mStarts;
    private int mFixType;
    private float mPitch;
    private float mRoll;

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

    public long getDistance() {
        return mDistance;
    }

    public void setDistance(long distance) {
        this.mDistance = distance;
    }

    public int getSpeed() {
        return mSpeed;
    }

    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    public int getStarts() {
        return mStarts;
    }

    public void setStarts(int starts) {
        this.mStarts = starts;
    }

    public int getFixType() {
        return mFixType;
    }

    public void setFixType(int fixType) {
        this.mFixType = fixType;
    }

    public float getPitch() {
        return mPitch;
    }

    public void setPitch(float pitch) {
        this.mPitch = mPitch;
    }

    public float getRoll() {
        return mRoll;
    }

    public void setRoll(float roll) {
        this.mRoll = mRoll;
    }
}
