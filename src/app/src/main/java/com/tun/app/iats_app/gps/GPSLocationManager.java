package com.tun.app.iats_app.gps;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.tun.app.iats_app.entities.MapParameter;

import java.lang.ref.WeakReference;

/**
 * 作者：TanTun
 * 时间：2017/10/10
 * 邮箱：32965926@qq.com
 * 描述：GPS定位的管理类
 */
public class GPSLocationManager {
    private static final String GPS_LOCATION_NAME = android.location.LocationManager.GPS_PROVIDER;
    private static GPSLocationManager gpsLocationManager;
    private static final Object objLock = new Object();
    private static String mLocateType;
    private WeakReference<Context> mContext;
    private LocationManager locationManager;
    private GPSLocation mGPSLocation;
    private GPSPermissionsListener gpsPermissionsListener;
    private boolean isOpenGps;
    private long mMinTime;
    private float mMinDistance;
    private MapParameter mMapParameter;

    private GPSLocationManager(Context context) {
        initData(context);
    }

    private void initData(Context context) {
        this.mContext = new WeakReference<>(context);
        if (mContext.get() != null) {
            locationManager = (LocationManager) (mContext.get().getSystemService(Context.LOCATION_SERVICE));
        }

        //定位类型：GPS
        mLocateType = LocationManager.GPS_PROVIDER;
        //mLocateType = locationManager.getBestProvider(getCriteria(), true);
        //默认不强制打开GPS设置面板
        isOpenGps = true;
        //默认定位时间间隔为1000ms
        mMinTime = 1000;
        //默认位置可更新的最短距离为0m
        mMinDistance = 0;
    }

    public static GPSLocationManager getInstances(Context context) {
        if (gpsLocationManager == null) {
            synchronized (objLock) {
                if (gpsLocationManager == null) {
                    gpsLocationManager = new GPSLocationManager(context);
                }
            }
        }
        return gpsLocationManager;
    }

    /**
     * 设置权限相关监听
     *
     * @param onGPSPermissionsListener 监听方法
     */
//    public void setGPSPermissionsListener(GPSPermissionsListener onGPSPermissionsListener) {
//        gpsPermissionsListener = onGPSPermissionsListener;
//    }

    /**
     * 方法描述：设置发起定位请求的间隔时长
     *
     * @param minTime 定位间隔时长（单位ms）
     */
    public void setScanSpan(long minTime) {
        this.mMinTime = minTime;
    }

    /**
     * 方法描述：设置位置更新的最短距离
     *
     * @param minDistance 最短距离（单位m）
     */
    public void setMinDistance(float minDistance) {
        this.mMinDistance = minDistance;
    }

    /**
     * 方法描述：设置地图参数
     *
     * @param mapParameter 最短距离（单位m）
     */
    public void setMapParameter(MapParameter mapParameter) {
        this.mMapParameter = mapParameter;
    }

    /**
     * 方法描述：开启定位（默认情况下不会强制要求用户打开GPS设置面板）
     *
     * @param gpsLocationListener gps位置监听
     */
    public void start(GPSLocationListener gpsLocationListener) {
        this.start(gpsLocationListener, isOpenGps);
    }

    /**
     * 方法描述：开启定位
     *
     * @param gpsLocationListener gps位置监听
     * @param isOpenGps           当用户GPS未开启时是否强制用户开启GPS
     */
    private void start(GPSLocationListener gpsLocationListener, boolean isOpenGps) {
        this.isOpenGps = isOpenGps;
        if (mContext.get() == null) {
            return;
        }

        mGPSLocation = new GPSLocation(gpsLocationListener);

        if (!locationManager.isProviderEnabled(GPS_LOCATION_NAME) && isOpenGps) {
            openGPS();
            return;
        }

        if (ActivityCompat.checkSelfPermission(mContext.get(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (mContext.get(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(mContext.get(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (mContext.get(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(mLocateType);
        mGPSLocation.onLocationChanged(lastKnownLocation);
        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
        locationManager.requestLocationUpdates(mLocateType, mMinTime, mMinDistance, mGPSLocation);
    }

    /**
     * 方法描述：转到手机设置界面，用户设置GPS
     */
    private void openGPS() {
        Toast.makeText(mContext.get(), "请打开GPS设置", Toast.LENGTH_SHORT).show();
        if (Build.VERSION.SDK_INT > 23) {
            Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            //gpsPermissionsListener.onOpenGPS(intent);
            //mContext.get().startActivityForResult(intent, 0);
            mContext.get().startActivity(intent);
        }
    }

    /**
     * 方法描述：终止GPS定位,该方法最好在onPause()中调用
     */
    public void stop() {
        if (mContext.get() != null) {
            if (ActivityCompat.checkSelfPermission(mContext.get(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext.get(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(mGPSLocation);
        }
    }

    /**
     * 模拟GPS位置
     */
    public void startTestGpsLocation(GPSLocationListener gpsLocationListener) {

        mGPSLocation = new GPSLocation(gpsLocationListener);

        locationManager.addTestProvider(LocationManager.GPS_PROVIDER,
                "requiresNetwork" == "",
                "requiresSatellite" == "",
                "requiresCell" == "",
                "hasMonetaryCost" == "",
                "supportsAltitude" == "",
                "supportsSpeed" == "",
                "supportsBearing" == "",
                Criteria.NO_REQUIREMENT,
                Criteria.ACCURACY_COARSE);

        // 开启测试Provider
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

        locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER,
                LocationProvider.AVAILABLE,
                null,
                System.currentTimeMillis());

        if (ActivityCompat.checkSelfPermission(mContext.get(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (mContext.get(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(mContext.get(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                    (mContext.get(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000, 0f,
                mGPSLocation);

        //需要注册监听完成后再进行模拟定位点的设置，否则接受不到回调
        //可以用handler postDelay方法

        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (!isInterrupted()) {
                    try {

                        // 创建新的Location对象，并设定必要的属性值
                        Location newLocation = new Location(LocationManager.GPS_PROVIDER);
//                        newLocation.setLatitude(24.759725);
//                        newLocation.setLongitude(110.507655);
//                        newLocation.setAccuracy(500);
//                        newLocation.setAltitude(10);
                        newLocation.setLatitude(mMapParameter.getMap_location_simulation_lat());
                        newLocation.setLongitude(mMapParameter.getMap_location_simulation_lon());
                        newLocation.setAccuracy(500);
                        newLocation.setAltitude(mMapParameter.getMap_location_simulation_alt());
                        newLocation.setTime(System.currentTimeMillis());
                        // 这里一定要设置nonasecond单位的值，否则是没法持续收到监听的，原因见下文
                        newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

                        // 设置最新位置，一定要在requestLocationUpdate完成后进行，才能收到监听
                        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);

                        locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER,
                                LocationProvider.AVAILABLE,
                                null,
                                System.currentTimeMillis());

                        for (int i = 0; i < 2 * 100; i++) //每2秒调度一次
                        {
                            if (isInterrupted())
                                break;
                            Thread.sleep(10);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();

    }

    /**
     * 返回查询条件
     *
     * @return 查询条件
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        // 设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        // 设置是否需要方位信息
        criteria.setBearingRequired(false);
        // 设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }
}