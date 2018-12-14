package com.tun.app.iats_app.entities;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.ref.WeakReference;

/**
 * 作者：TanTun
 * 时间：2018/7/21
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class MapParameter {

    private final WeakReference<Context> mContext;

    private int map_draw_line;
    private int map_type;
    private int map_location_mode;
    private int map_location_simulation;
    private double map_location_simulation_lon;
    private double map_location_simulation_lat;
    private int map_location_simulation_alt;

    public int getMap_draw_line() {
        return map_draw_line;
    }

    public void setMap_draw_line(int map_draw_line) {
        this.map_draw_line = map_draw_line;
    }

    public int getMap_type() {
        return map_type;
    }

    public void setMap_type(int map_type) {
        this.map_type = map_type;
    }

    public int getMap_location_mode() {
        return map_location_mode;
    }

    public void setMap_location_mode(int map_location_mode) {
        this.map_location_mode = map_location_mode;
    }

    public int getMap_location_simulation() {
        return map_location_simulation;
    }

    public void setMap_location_simulation(int map_location_simulation) {
        this.map_location_simulation = map_location_simulation;
    }

    public double getMap_location_simulation_lon() {
        return map_location_simulation_lon;
    }

    public void setMap_location_simulation_lon(double map_location_simulation_lon) {
        this.map_location_simulation_lon = map_location_simulation_lon;
    }

    public double getMap_location_simulation_lat() {
        return map_location_simulation_lat;
    }

    public void setMap_location_simulation_lat(double map_location_simulation_lat) {
        this.map_location_simulation_lat = map_location_simulation_lat;
    }

    public int getMap_location_simulation_alt() {
        return map_location_simulation_alt;
    }

    public void setMap_location_simulation_alt(int map_location_simulation_alt) {
        this.map_location_simulation_alt = map_location_simulation_alt;
    }

    public MapParameter(Context context) {
        mContext = new WeakReference<>(context);
        SharedPreferences sp = context.getSharedPreferences("map_parameter",
                Context.MODE_PRIVATE);

        map_draw_line = sp.getInt("map_draw_line", 0);
        map_type = sp.getInt("map_type", 1);
        map_location_mode = sp.getInt("map_location_mode", 0);
        map_location_simulation = sp.getInt("map_location_simulation", 0);
        map_location_simulation_lon = Double.parseDouble(sp.getString("map_location_simulation_lon", "114.054300"));
        map_location_simulation_lat = Double.parseDouble(sp.getString("map_location_simulation_lat", "22.555555"));
        map_location_simulation_alt = sp.getInt("map_location_simulation_alt", 0);
    }

    public void save() {
        SharedPreferences sp = mContext.get().getSharedPreferences("map_parameter",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt("map_draw_line", map_draw_line);
        editor.putInt("map_type", map_type);
        editor.putInt("map_location_mode", map_location_mode);
        editor.putInt("map_location_simulation", map_location_simulation);
        editor.putString("map_location_simulation_lon", Double.toString(map_location_simulation_lon));
        editor.putString("map_location_simulation_lat",  Double.toString(map_location_simulation_lat));
        editor.putInt("map_location_simulation_alt", map_location_simulation_alt);
        editor.apply();
    }
}
