package com.tun.app.iats_app.entities;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

/**
 * 作者：TanTun
 * 时间：2018/7/19
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class TrackerParameter {

    private final WeakReference<Context> mContext;

    private String bluetoothName;
    private int pid_p;
    private int pid_i;
    private int pid_d;
    private int pid_divider;
    private int pid_max_error;
    private int tilt_min;
    private int tilt_max;
    private int pan_center;
    private int pan_min_speed;
    private int compass_offset;
    private int compass_declination;
    private int start_tracking_distance;

    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public int getPid_p() {
        return pid_p;
    }

    public void setPid_p(int pid_p) {
        this.pid_p = pid_p;
    }

    public int getPid_i() {
        return pid_i;
    }

    public void setPid_i(int pid_i) {
        this.pid_i = pid_i;
    }

    public int getPid_d() {
        return pid_d;
    }

    public void setPid_d(int pid_d) {
        this.pid_d = pid_d;
    }

    public int getPid_divider() {
        return pid_divider;
    }

    public void setPid_divider(int pid_divider) {
        this.pid_divider = pid_divider;
    }

    public int getPid_max_error() {
        return pid_max_error;
    }

    public void setPid_max_error(int pid_max_error) {
        this.pid_max_error = pid_max_error;
    }

    public int getTilt_min() {
        return tilt_min;
    }

    public void setTilt_min(int tilt_min) {
        this.tilt_min = tilt_min;
    }

    public int getTilt_max() {
        return tilt_max;
    }

    public void setTilt_max(int tilt_max) {
        this.tilt_max = tilt_max;
    }

    public int getPan_center() {
        return pan_center;
    }

    public void setPan_center(int pan_center) {
        this.pan_center = pan_center;
    }

    public int getPan_min_speed() {
        return pan_min_speed;
    }

    public void setPan_min_speed(int pan_min_speed) {
        this.pan_min_speed = pan_min_speed;
    }

    public int getCompass_offset() {
        return compass_offset;
    }

    public void setCompass_offset(int compass_offset) {
        this.compass_offset = compass_offset;
    }

    public int getStart_tracking_distance() {
        return start_tracking_distance;
    }

    public void setStart_tracking_distance(int start_tracking_distance) {
        this.start_tracking_distance = start_tracking_distance;
    }

    public int getCompass_declination() {
        return compass_declination;
    }

    public void setCompass_declination(int compass_declination) {
        this.compass_declination = compass_declination;
    }

    public TrackerParameter(Context context) {
        mContext = new WeakReference<>(context);
        SharedPreferences sp = context.getSharedPreferences("att_parameter",
                Context.MODE_PRIVATE);

        bluetoothName = sp.getString("bluetoothName", "T_Auto_Track");
        pid_p = sp.getInt("pid_p", 4500);
        pid_i = sp.getInt("pid_i", 0);
        pid_d = sp.getInt("pid_d", 0);
        pid_divider = sp.getInt("pid_divider", 15);
        pid_max_error = sp.getInt("pid_max_error", 0);
        tilt_min = sp.getInt("tilt_min", 1500);
        tilt_max = sp.getInt("tilt_max", 1500);
        pan_center = sp.getInt("pan_center", 1500);
        pan_min_speed = sp.getInt("pan_min_speed", 50);
        compass_offset = sp.getInt("compass_offset", 0);
        compass_declination = sp.getInt("compass_declination", 0);
        start_tracking_distance = sp.getInt("start_tracking_distance", 10);
    }

    public void save() {
        SharedPreferences sp = mContext.get().getSharedPreferences("att_parameter",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("bluetoothName", bluetoothName);
        editor.putInt("pid_p", pid_p);
        editor.putInt("pid_i", pid_i);
        editor.putInt("pid_d", pid_d);
        editor.putInt("pid_divider", pid_divider);
        editor.putInt("pid_max_error", pid_max_error);
        editor.putInt("tilt_min", tilt_min);
        editor.putInt("tilt_max", tilt_max);
        editor.putInt("pan_center", pan_center);
        editor.putInt("pan_min_speed", pan_min_speed);
        editor.putInt("compass_offset", compass_offset);
        editor.putInt("compass_declination", compass_declination);
        editor.putInt("start_tracking_distance", start_tracking_distance);
        editor.apply();
    }
}
