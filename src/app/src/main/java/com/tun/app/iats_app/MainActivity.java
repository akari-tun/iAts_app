package com.tun.app.iats_app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.tun.app.iats_app.Observer.Function;
import com.tun.app.iats_app.Observer.ObservableFunctionTag;
import com.tun.app.iats_app.Observer.ObservableManager;
import com.tun.app.iats_app.entities.Airplane;
import com.tun.app.iats_app.entities.MapParameter;
import com.tun.app.iats_app.entities.Tracker;
import com.tun.app.iats_app.entities.TrackerParameter;
import com.tun.app.iats_app.fragment.SettingFragment;
import com.tun.app.iats_app.protocol.Defines;
import com.tun.app.iats_app.service.TrackerAntennaService;
import com.tun.app.iats_app.service.TrackerAntennaServiceListener;
import com.tun.app.iats_app.utils.DigitsUtils;
import com.tun.app.iats_app.gps.GPSProviderStatus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE = 1;
    private static final String TAG = "MainActive";

    ProgressDialog mProgressDialog;

    TextView mTextView_Heading_Tracker;
    TextView mTextView_Pitching_Tracker;
    TextView mTextView_Voltage_Tracker;
    TextView mTextView_Longitude_Plane;
    TextView mTextView_Latitude_Plane;
    TextView mTextView_Altitude_Plane;
    TextView mTextView_Speed_Plane;
    TextView mTextView_Distance_Plane;
    TextView mTextView_Status_Tracker;

    RadioButton mRadioButton_Mode_Manual;
    RadioButton mRadioButton_Mode_Auto;
    RadioButton mRadioButton_Mode_Debug;

    View mTitleMenu;
    ImageView mImageLocalGps;
    ImageView mImageHomeGeolocation;
    ImageView mImagePlaneGeolocation;
    ImageView mImageMoreTitle;

    MapView mMapView = null;
    MyLocationData mPlaneStationData = null;
    BitmapDescriptor mPlaneMarkerIcon = null;
    BitmapDescriptor mTrackerMarkerIcon = null;
    Marker mTrackerMarker = null;
    CoordinateConverter mMyLatLngConverter = null;
    CoordinateConverter mPlaneLatLngConverter = null;
    Polyline mPolyline = null;

    Airplane mAirplane = null;
    Tracker mTracker = null;
    TrackerParameter mTrackerParameter = null;
    MapParameter mMapParameter = null;

    TrackerAntennaService.TrackerServiceBinder mTrackerServiceBinder;
    TrackerAntennaServiceListener mTrackerServiceListener;

    MapDrawLineChange mMapDrawLineChange;
    MapTypeChange mMapTypeChange;
    MapLocationModeChange mMapLocationModeChange;

    Bitmap mTrackerImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!TrackerAntennaService.isServiceRunning())
        {
            Intent main = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(main);
            final Intent intent = new Intent(this, TrackerAntennaService.class);
            intent.setAction("android.intent.action.RESPOND_VIA_MESSAGE");
            Log.d(TAG, "启动服务:" + startService(intent));
            MainActivity.this.finish();
            return;
        }

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(com.tun.app.iats_app.R.layout.activity_main);

        init();

        mImageLocalGps.setOnClickListener(new SetTrackerHome());
        mTextView_Status_Tracker.setOnClickListener(new SetTrackerHome());
        mImageHomeGeolocation.setOnClickListener(new ToMyLocation());
        mTextView_Distance_Plane.setOnClickListener(new ToMyLocation());
        mImagePlaneGeolocation.setOnClickListener(new ToPlaneLocation());
        mTextView_Altitude_Plane.setOnClickListener(new ToPlaneLocation());
        mImageMoreTitle.setOnClickListener(new MoreTitleClick());

        mTitleMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingFragment fragment = new SettingFragment();
                fragment.show(getFragmentManager(), "SettingFragment");
            }
        });

        Intent bindIntent = new Intent(this, TrackerAntennaService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        //强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //判断是不是启动蓝牙的结果
        if (requestCode == REQUEST_ENABLE) {
            if (resultCode == Activity.RESULT_OK) {
                //成功
                Toast.makeText(this, "蓝牙开启成功...", Toast.LENGTH_SHORT).show();
            } else {
                //失败
                Toast.makeText(this, "蓝牙开启失败...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mMapView != null) mMapView.onDestroy();

        if (mTrackerServiceBinder != null) {
            mTrackerServiceBinder.unlistener();
            unbindService(connection);
        }

        ObservableManager.instance().removeObserver(ObservableFunctionTag.MAP_DRAW_LINE_CHANGE, mMapDrawLineChange);
        ObservableManager.instance().removeObserver(ObservableFunctionTag.MAP_TYPE_CHANGE, mMapTypeChange);
        ObservableManager.instance().removeObserver(ObservableFunctionTag.MAP_LOCATION_MODE_CHANGE, mMapLocationModeChange);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        if (mMapView != null) mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        if (mMapView != null) mMapView.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
    }

    /**
     * 绑定到服务
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTrackerServiceBinder = (TrackerAntennaService.TrackerServiceBinder) service;
            mTrackerServiceBinder.setTrackerAntennaServiceListener(mTrackerServiceListener);
            mTracker = mTrackerServiceBinder.getTracker();
            mAirplane = mTrackerServiceBinder.getAirplane();
            mTrackerParameter = mTrackerServiceBinder.getTrackerParameter();
            mMapParameter = mTrackerServiceBinder.getMapParameter();

//            if (mMapView.getMap() != null) {
//                mMapView.getMap().setMapType(mMapParameter.getMap_type());
//            }

            Log.d(TAG, "service connected = " + name.getClassName());

            mTrackerServiceListener.UpdateServiceStatus(mTracker.getStatus());

            if (mMapParameter.getMap_location_simulation() == 1) {
                Toast.makeText(MainActivity.this, "已经开启模拟GPS位置，请确认相关权限已开启。", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * 判断服务是否开启
     *
     * @param mContext 对象
     * @return 是否已启动
     */
    public static boolean isServiceRunning(Context mContext) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

        if ((serviceList.size() > 0)) {
            for (ActivityManager.RunningServiceInfo info : serviceList) {
                if (info.service.getClassName().equals("TrackerAntennaService")) {
                    isRunning = true;
                    break;
                }
            }
        }
        return isRunning;
    }

    /**
     * 参数 无
     * 返回值 无
     * 异常 无
     * 描述：初始化窗体必须的对象
     */
    private void init() {
        mProgressDialog = new ProgressDialog(this);

        mTextView_Heading_Tracker = (TextView)findViewById(com.tun.app.iats_app.R.id.text_heading_tracker);
        mTextView_Pitching_Tracker = (TextView)findViewById(com.tun.app.iats_app.R.id.text_pitching_tracker);
        mTextView_Voltage_Tracker = (TextView)findViewById(com.tun.app.iats_app.R.id.text_voltage_tracker);
        mTextView_Longitude_Plane = (TextView)findViewById(com.tun.app.iats_app.R.id.text_longitude_plane);
        mTextView_Latitude_Plane = (TextView)findViewById(com.tun.app.iats_app.R.id.text_latitude_plane);
        mTextView_Altitude_Plane = (TextView)findViewById(com.tun.app.iats_app.R.id.text_altitude_plane);
        mTextView_Speed_Plane = (TextView)findViewById(com.tun.app.iats_app.R.id.text_speed_plane);
        mTextView_Distance_Plane = (TextView)findViewById(com.tun.app.iats_app.R.id.text_distance_plane);
        mTextView_Status_Tracker = (TextView)findViewById(com.tun.app.iats_app.R.id.text_status);

        mTextView_Heading_Tracker.setText(com.tun.app.iats_app.R.string.text_sample_zero_o);
        mTextView_Pitching_Tracker.setText(com.tun.app.iats_app.R.string.text_sample_zero_o);
        mTextView_Voltage_Tracker.setText(com.tun.app.iats_app.R.string.text_sample_zero_v);
        mTextView_Longitude_Plane.setText(com.tun.app.iats_app.R.string.text_sample_seven_digits);
        mTextView_Latitude_Plane.setText(com.tun.app.iats_app.R.string.text_sample_seven_digits);
        mTextView_Altitude_Plane.setText(com.tun.app.iats_app.R.string.text_sample_zero_m);
        mTextView_Speed_Plane.setText(com.tun.app.iats_app.R.string.text_sample_zero_speed);
        mTextView_Distance_Plane.setText(com.tun.app.iats_app.R.string.text_sample_zero_km);
        mTextView_Status_Tracker.setText(com.tun.app.iats_app.R.string.text_initializing);

        mTitleMenu = findViewById(com.tun.app.iats_app.R.id.title_menu);
        mImageLocalGps = (ImageView) findViewById(com.tun.app.iats_app.R.id.image_location_gps_status);
        mImageHomeGeolocation = (ImageView) findViewById(com.tun.app.iats_app.R.id.image_distance_plane);
        mImagePlaneGeolocation = (ImageView) findViewById(com.tun.app.iats_app.R.id.image_plane_geolocation);
        mImageMoreTitle = (ImageView) findViewById(com.tun.app.iats_app.R.id.image_more_title);

        /*---------------------------模式选择栏图片大小----------------------------------*/
        mRadioButton_Mode_Manual = (RadioButton) findViewById(com.tun.app.iats_app.R.id.radio_mode_manual);
        mRadioButton_Mode_Auto = (RadioButton) findViewById(com.tun.app.iats_app.R.id.radio_mode_auto);
        mRadioButton_Mode_Debug = (RadioButton) findViewById(com.tun.app.iats_app.R.id.radio_mode_debug);

        //定义底部标签图片大小
        Drawable drawable_Manual = ContextCompat.getDrawable(this, com.tun.app.iats_app.R.mipmap.manual);
        drawable_Manual.setBounds(0, 8, 45, 45);//第一0是距左右边距离，第二0是距上下边距离，第三69长度,第四宽度
        mRadioButton_Mode_Manual.setCompoundDrawables(null, drawable_Manual, null, null);//只放上面
        mRadioButton_Mode_Manual.setOnClickListener(new SetTrackerMode());

        Drawable drawable_Auto =  ContextCompat.getDrawable(this, com.tun.app.iats_app.R.mipmap.auto);
        drawable_Auto.setBounds(0, 8, 45, 45);
        mRadioButton_Mode_Auto.setCompoundDrawables(null, drawable_Auto, null, null);
        mRadioButton_Mode_Auto.setOnClickListener(new SetTrackerMode());

        Drawable drawable_Debug = ContextCompat.getDrawable(this, com.tun.app.iats_app.R.mipmap.debug);
        drawable_Debug.setBounds(0, 8, 45, 45);
        mRadioButton_Mode_Debug.setCompoundDrawables(null, drawable_Debug, null, null);
        mRadioButton_Mode_Debug.setOnClickListener(new SetTrackerMode());
        /*---------------------------------------------------------------------*/

        mAirplane = new Airplane();
        mTracker = new Tracker();

        /*---------------------------百度地图----------------------------------*/
        //获取地图控件引用
        mMapView = (MapView)findViewById(com.tun.app.iats_app.R.id.map_view);

        // 隐藏百度的LOGO
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView)) {
            child.setVisibility(View.INVISIBLE);
        }
        // 不显示地图上比例尺
        mMapView.showScaleControl(false);

        //绑定事件
        mMapView.getMap().setOnMapStatusChangeListener(new MapStatusChangeListener(this));

        int width;
        int height;
        Drawable drawable;
        Canvas canvas;
        Bitmap planeImage;

        drawable = ContextCompat.getDrawable(this, com.tun.app.iats_app.R.mipmap.airplane);
        width = (int) (drawable.getIntrinsicWidth() * 0.6);
        height = (int) (drawable.getIntrinsicHeight() * 0.6);
        planeImage = Bitmap.createBitmap(width, height,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        canvas = new Canvas(planeImage);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        drawable = ContextCompat.getDrawable(this, com.tun.app.iats_app.R.mipmap.tracker);
        width = (int) (drawable.getIntrinsicWidth() * 0.6);
        height = (int) (drawable.getIntrinsicHeight() * 0.6);
        mTrackerImage = Bitmap.createBitmap(width, height,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        canvas = new Canvas(mTrackerImage);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        mPlaneMarkerIcon = BitmapDescriptorFactory.fromBitmap(planeImage);
        mTrackerMarkerIcon = BitmapDescriptorFactory.fromBitmap(mTrackerImage);

        // 将GPS设备采集的原始GPS坐标转换成百度坐标
        mMyLatLngConverter = new CoordinateConverter();
        mMyLatLngConverter.from(CoordinateConverter.CoordType.GPS);
        mPlaneLatLngConverter = new CoordinateConverter();
        mPlaneLatLngConverter.from(CoordinateConverter.CoordType.GPS);
        /*---------------------------------------------------------------------*/

        mMapDrawLineChange = new MapDrawLineChange();
        mMapTypeChange = new MapTypeChange();
        mMapLocationModeChange = new MapLocationModeChange();

        ObservableManager.instance().registerObserver(ObservableFunctionTag.MAP_DRAW_LINE_CHANGE, mMapDrawLineChange);
        ObservableManager.instance().registerObserver(ObservableFunctionTag.MAP_TYPE_CHANGE, mMapTypeChange);
        ObservableManager.instance().registerObserver(ObservableFunctionTag.MAP_LOCATION_MODE_CHANGE, mMapLocationModeChange);

        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.screenBrightness = 100f * (1f / 255f);
        this.getWindow().setAttributes(lp);

        if (mTracker.getLongitude() != 0 && mTracker.getLatitude() != 0) {
            try {
                LatLng point = new LatLng(mTracker.getLatitude(), mTracker.getLongitude());
                mMyLatLngConverter.coord(point);

                if (mTrackerMarker == null) {
                    MarkerOptions mo = new MarkerOptions()
                            .position(mMyLatLngConverter.convert())
                            .icon(mTrackerMarkerIcon)
                            .draggable(false);
                    mTrackerMarker = (Marker)mMapView.getMap().addOverlay(mo);
                    //设定中心点坐标
                    setMapCenter(mMyLatLngConverter.convert());
                } else {
                    mTrackerMarker.setPosition(mMyLatLngConverter.convert());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mTrackerServiceListener = new TrackerServiceListener(this);
    }

    /**
     * 设置云台的家的地点
     */
    private class SetTrackerHome implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mTrackerMarker != null) {
                mTrackerServiceBinder.setTrackerHome();
            } else {
                Toast.makeText(MainActivity.this, "尚未定位成功...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 设置云台的模式
     */
    private class SetTrackerMode implements  View.OnClickListener {

        @Override
        public void onClick(View v) {

            boolean ret = false;

            switch (v.getId()) {
                case com.tun.app.iats_app.R.id.radio_mode_manual:
                    ret = mTrackerServiceBinder.setTrackerMode(Defines.TRACKER_MODE_MANUAL);
                    break;
                case com.tun.app.iats_app.R.id.radio_mode_auto:
                    ret = mTrackerServiceBinder.setTrackerMode(Defines.TRACKER_MODE_AUTO);
                    break;
                case com.tun.app.iats_app.R.id.radio_mode_debug:
                    ret = mTrackerServiceBinder.setTrackerMode(Defines.TRACKER_MODE_DEBUG);
                    break;
            }

            if (ret) {
                Log.d(TAG, "SetTrackerMode() 设置模式指令发送成功!");
            }
        }
    }


    /**
     * 定位我的位置为中心点
     */
    private class ToMyLocation implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mTrackerMarker != null) {
                setMapCenter(mTrackerMarker.getPosition());
            } else if (mTracker.getLatitude() != 0 && mTracker.getLongitude() != 0) {
                LatLng point = new LatLng(mTracker.getLatitude(), mTracker.getLongitude());
                mMyLatLngConverter.coord(point);

                MarkerOptions mo = new MarkerOptions()
                        .position(mMyLatLngConverter.convert())
                        .icon(mTrackerMarkerIcon)
                        .draggable(false);
                mTrackerMarker = (Marker)mMapView.getMap().addOverlay(mo);
                //设定中心点坐标
                setMapCenter(mMyLatLngConverter.convert());

                Toast.makeText(MainActivity.this,
                        "经度：" + mTracker.getLongitude() + "\n纬度：" + mTracker.getLatitude(),
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MainActivity.this, "尚未定位成功...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 定位飞机的位置为中心点
     */
    private class ToPlaneLocation implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mPlaneStationData != null) {
                setMapCenter(new LatLng(mPlaneStationData.latitude, mPlaneStationData.longitude));
            } else {
                Toast.makeText(MainActivity.this, "尚未接收到定位数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 标题中的More按钮点击
     */
    private class MoreTitleClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //创建弹出式菜单对象（最低版本11）
            PopupMenu popup = new PopupMenu(MainActivity.this, v);//第二个参数是绑定的那个view
            //获取菜单填充器
            MenuInflater inflater = popup.getMenuInflater();
            //填充菜单
            inflater.inflate(com.tun.app.iats_app.R.menu.more_title, popup.getMenu());
            //绑定菜单项的点击事件
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    switch (item.getItemId()) {
                        case com.tun.app.iats_app.R.id.menu_button_exit:
                            MainActivity.this.finish();
                            System.exit(0);
                            break;
                        default:
                            break;
                    }

                    return false;
                }
            });
            popup.show(); //这一行代码不要忘记了
        }
    }

    /**
     * 修改是否画线
     */
    private class MapDrawLineChange implements Function {

        @Override
        public void function(Object... data) {
            if (data.length <= 0) return;

            if ((int)data[0] == 0 && mPolyline != null) {
                mPolyline.remove();
                mPolyline = null;
            }
        }
    }

    /**
     * 修改地图类型
     */
    private class MapTypeChange implements Function {

        @Override
        public void function(Object... data) {
            if (data.length <= 0) return;

            int value = (int)data[0];

            switch (value) {
                case BaiduMap.MAP_TYPE_NORMAL:
                case BaiduMap.MAP_TYPE_SATELLITE:
                    mMapView.getMap().setMapType(value);
                    break;
            }
        }
    }

    /**
     * 修改定位模式
     */
    private class MapLocationModeChange implements Function {

        @Override
        public void function(Object... data) {
            if (data.length <= 0) return;

            MyLocationConfiguration config =
                    new MyLocationConfiguration(
                            MyLocationConfiguration.LocationMode.values()[(int)data[0]],
                            true, mPlaneMarkerIcon);

            mMapView.getMap().setMyLocationConfiguration(config);
        }
    }

    /**
     * 参数 中心点
     * 返回值 无
     * 异常 无
     * 描述：设置地图的中心点
     */
    private void setMapCenter(LatLng point) {
        //设定中心点坐标
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(18)
                .build();
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        mMapView.getMap().setMapStatus(mMapStatusUpdate);
    }

    /**
     * 云台跟踪服务事件监听
     */
    private class TrackerServiceListener implements TrackerAntennaServiceListener {

        private final WeakReference<MainActivity> mActivity;
        private int count = 0;

        int mGpsProviderStatus = GPSProviderStatus.GPS_DISABLED;

        private TrackerServiceListener(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        /**
         * 方法描述：位置信息发生改变时被调用
         *
         * @param location 更新位置后的新的Location对象
         */
        @Override
        public void UpdateLocation(Location location) {
            mActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        MainActivity activity = mActivity.get();

                        LatLng point = new LatLng(mTracker.getLatitude(), mTracker.getLongitude());
                        mMyLatLngConverter.coord(point);

                        if (mTrackerMarker == null) {
                            MarkerOptions mo = new MarkerOptions()
                                    .position(mMyLatLngConverter.convert())
                                    .icon(mTrackerMarkerIcon)
                                    .draggable(false);
                            mTrackerMarker = (Marker)mMapView.getMap().addOverlay(mo);
                            //设定中心点坐标
                            setMapCenter(mMyLatLngConverter.convert());

                            Toast.makeText(activity,
                                    "经度：" + mTracker.getLongitude() + "\n纬度：" + mTracker.getLatitude(),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            mTrackerMarker.setPosition(mMyLatLngConverter.convert());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * 方法描述：provider定位源类型变化时被调用
         *
         * @param provider provider的类型
         * @param status   provider状态
         */
        @Override
        public void UpdateGpsStatus(String provider, int status){
            if (provider.equals("gps")) {
                Log.d(TAG, "定位类型：" + provider);
                switch (status) {
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                }
            }
        }

        /**
         * 方法描述：GPS状态发生改变时被调用（GPS手动启动、手动关闭、GPS不在服务区、GPS占时不可用、GPS可用)
         *
         * @param gpsStatus 详见{@link GPSProviderStatus}
         */
        @Override
        public void UpdateGpsProviderStatus(int gpsStatus){
            mActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mTracker.getGpsStatus() != mGpsProviderStatus) {

                            MainActivity activity = mActivity.get();

                            switch (mTracker.getGpsStatus()) {
                                case GPSProviderStatus.GPS_ENABLED:
                                    mImageLocalGps.setImageResource(com.tun.app.iats_app.R.mipmap.gps_unavailable);
                                    Toast.makeText(activity, "GPS已开启", Toast.LENGTH_SHORT).show();
                                    break;
                                case GPSProviderStatus.GPS_DISABLED:
                                    mImageLocalGps.setImageResource(com.tun.app.iats_app.R.mipmap.gps_unavailable);
                                    Toast.makeText(activity, "GPS已关闭", Toast.LENGTH_SHORT).show();
                                    break;
                                case GPSProviderStatus.GPS_OUT_OF_SERVICE:
                                    mImageLocalGps.setImageResource(com.tun.app.iats_app.R.mipmap.gps_unavailable);
                                    Toast.makeText(activity, "GPS服务已停止", Toast.LENGTH_SHORT).show();
                                    break;
                                case GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE:
                                    mImageLocalGps.setImageResource(com.tun.app.iats_app.R.mipmap.gps_search);
                                    break;
                                case GPSProviderStatus.GPS_AVAILABLE:
                                    mImageLocalGps.setImageResource(com.tun.app.iats_app.R.mipmap.gps_available);
                                    Toast.makeText(activity, "本地GPS定位成功", Toast.LENGTH_SHORT).show();
                                    break;
                            }

                            mGpsProviderStatus = mTracker.getGpsStatus();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * 方法描述：更新飞机数据
         *
         * @param plane 详见{@link Airplane}
         */
        @Override
        public void UpdatePlaneData(Airplane plane){
            mActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mTextView_Longitude_Plane.setText(
                                DigitsUtils.FORMAT_SEVEN_DIGITS.format(mAirplane.getLongitude()));
                        mTextView_Latitude_Plane.setText(
                                DigitsUtils.FORMAT_SEVEN_DIGITS.format(mAirplane.getLatitude()));
                        mTextView_Altitude_Plane.setText(DigitsUtils.FORMAT_M_DIGITS.format(
                                mAirplane.getAltitude()));
                        mTextView_Speed_Plane.setText(DigitsUtils.FORMAT_KMH_DIGITS.format(
                                (mAirplane.getSpeed() * 3.6f)));
                        if (mAirplane.getDistance() < 1000) {
                            mTextView_Distance_Plane.setText(DigitsUtils.FORMAT_M_DIGITS.format(
                                    mAirplane.getDistance()));
                        } else {
                            mTextView_Distance_Plane.setText(DigitsUtils.FORMAT_KM_DIGITS.format(
                                    mAirplane.getDistance() / 1000.00f));
                        }

                        count++;
                        if (count < 2) return;
                        count = 0;

                        LatLng point = new LatLng(mAirplane.getLatitude(), mAirplane.getLongitude());
                        mPlaneLatLngConverter.coord(point);
                        LatLng newPoint = mPlaneLatLngConverter.convert();

                        if (mPlaneStationData == null) {
                            // 开启定位图层
                            mMapView.getMap().setMyLocationEnabled(true);
                            mPlaneStationData = new MyLocationData.Builder()
                                    .direction(mAirplane.getHeading())// 此处设置开发者获取到的方向信息，顺时针0-360
                                    .latitude(newPoint.latitude)
                                    .longitude(newPoint.longitude)
                                    .satellitesNum(mAirplane.getStarts())
                                    .build();

                            // 设置定位图层的配置（定位模式，是否允许方向信息，用户自定义定位图标）
                            MyLocationConfiguration config =
                                    new MyLocationConfiguration(
                                            MyLocationConfiguration.LocationMode.values()[mMapParameter.getMap_location_mode()],
                                            true, mPlaneMarkerIcon);

                            mMapView.getMap().setMyLocationConfiguration(config);
                        } else {
                            mPlaneStationData = new MyLocationData.Builder()
                                    .direction(mAirplane.getHeading())// 此处设置开发者获取到的方向信息，顺时针0-360
                                    .latitude(newPoint.latitude)
                                    .longitude(newPoint.longitude)
                                    .satellitesNum((mAirplane.getStarts()))
                                    .build();
                        }

                        //画线
                        if (mTrackerMarker != null && mMapParameter.getMap_draw_line() == 1) {
                            //构建折线点坐标
                            List<LatLng> points = new ArrayList<>();
                            points.add(newPoint);
                            points.add((mMyLatLngConverter.convert()));
                            //绘制折线
                            OverlayOptions ooPolyline = new PolylineOptions().width(3)
                                    .color(0xAAFF0000).points(points);

                            if (mPolyline == null) {
                                mPolyline = (Polyline) mMapView.getMap().addOverlay(ooPolyline);
                            } else {
                                mPolyline.setPoints(points);
                            }
                        }

                        mMapView.getMap().setMyLocationData(mPlaneStationData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * 方法描述：更新云台数据
         *
         * @param tracker 详见{@link Tracker}
         */
        @Override
        public void UpdateTrackerData(final Tracker tracker){

            mActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (mTrackerMarker != null) {
                            //旋转图标，指向飞机
                            float newRotate = mMapView.getMap().getMapStatus().rotate - mTracker.getHeading();
                            if (newRotate < 0) newRotate = 360 + newRotate;
                            mTrackerMarker.setRotate(newRotate);
                            //测试本地计算指向飞机的角度
//                            float heading = getAngle(new LatLng(mTracker.getLatitude(), mTracker.getLongitude()),
//                                    new LatLng(mAirplane.getLatitude(), mAirplane.getLongitude()));
//                            float newRotate = mMapView.getMap().getMapStatus().rotate - heading;
//                            if (newRotate < 0) newRotate = 360 + newRotate;
//                            mTrackerMarker.setRotate(newRotate);
                        }

                        mTextView_Voltage_Tracker.setText(DigitsUtils.FORMAT_V_DIGITS.format(mTracker.getVoltage()));
                        mTextView_Pitching_Tracker.setText(DigitsUtils.FORMAT_O_DIGITS.format(mTracker.getPitching()));
                        mTextView_Heading_Tracker.setText(DigitsUtils.FORMAT_O_DIGITS.format(mTracker.getHeading()));

                        switch (tracker.getMode()) {
                            case Defines.TRACKER_MODE_MANUAL :
                                mTextView_Status_Tracker.setText(com.tun.app.iats_app.R.string.text_mode_manual);
                                if (!mRadioButton_Mode_Manual.isChecked()) {
                                    mRadioButton_Mode_Manual.setChecked(true);
                                }
                                break;
                            case Defines.TRACKER_MODE_AUTO :
                                mTextView_Status_Tracker.setText(com.tun.app.iats_app.R.string.text_mode_auto);
                                if (!mRadioButton_Mode_Auto.isChecked()) {
                                    mRadioButton_Mode_Auto.setChecked(true);
                                }
                                break;
                            case Defines.TRACKER_MODE_DEBUG :
                                mTextView_Status_Tracker.setText(com.tun.app.iats_app.R.string.text_mode_debug);
                                if (!mRadioButton_Mode_Debug.isChecked()) {
                                    mRadioButton_Mode_Debug.setChecked(true);
                                }
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                /**
                * 计算两点之间的角度
                *
                * @param point1 本地位置
                * @param point2 飞机位置
                * @return angle
                */
//                public float getAngle(LatLng point1, LatLng point2) {
//                    double dlon = Math.toRadians(point2.longitude - point1.longitude);
//                    double dlat1 = Math.toRadians(point1.latitude);
//                    double dlat2 = Math.toRadians(point2.latitude);
//                    double a1 = Math.sin(dlon) * Math.cos(dlat2);
//                    double a2 = Math.sin(dlat1) * Math.cos(dlat2) * Math.cos(dlon);
//                    a2 = Math.cos(dlat1) * Math.sin(dlat2) - a2;
//                    a2 = Math.atan2(a1, a2);
//
//                    if (a2 < 0.0) a2 += (Math.PI * 2);
//
//                    return (float)Math.toDegrees(a2);
//                }
            });
        }

        /**
         * 方法描述：更新服务状态
         *
         * @param status 详见{@link TrackerAntennaService.TrackerServiceStatus}
         */
        @Override
        public void UpdateServiceStatus(TrackerAntennaService.TrackerServiceStatus status) {
            mActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (mTracker.getStatus()) {
                        case Initializing:
                            mTextView_Status_Tracker.setText(com.tun.app.iats_app.R.string.text_initializing);
                            break;
                        case Finding:
                            mTextView_Status_Tracker.setText(com.tun.app.iats_app.R.string.text_finding);
                            break;
                        case Connecting:
                            mTextView_Status_Tracker.setText(com.tun.app.iats_app.R.string.text_connecting);
                            break;
                        case Connected:
                            mTextView_Status_Tracker.setText(com.tun.app.iats_app.R.string.text_connected);
                            break;
                        case Tracking:
                            mTextView_Status_Tracker.setText(com.tun.app.iats_app.R.string.text_tracking);
                            break;
                        case Manual:
                            mTextView_Status_Tracker.setText(com.tun.app.iats_app.R.string.text_manual);
                            break;
                    }
                }
            });
        }

        /**
         * 显示消息
         *
         * @param info 消息
         */
        @Override
        public void OnMessageShow(String info) {
            Toast.makeText(MainActivity.this, info, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 地图状态改变事件监听
     */
    public class MapStatusChangeListener implements BaiduMap.OnMapStatusChangeListener {

        private final WeakReference<MainActivity> mActivity;
        MapStatus mMapStatus;

        private MapStatusChangeListener(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        /**
         * 手势操作地图，设置地图状态等操作导致地图状态开始改变。
         * @param status 地图状态改变开始时的地图状态
         */
        @Override
        public void onMapStatusChangeStart(MapStatus status){

        }

        /** 因某种操作导致地图状态开始改变。
         * @param status 地图状态改变开始时的地图状态
         * @param reason 表示地图状态改变的原因，取值有：
         * 1：用户手势触发导致的地图状态改变,比如双击、拖拽、滑动底图
         * 2：SDK导致的地图状态改变, 比如点击缩放控件、指南针图标
         * 3：开发者调用,导致的地图状态改变
         */
        @Override
        public void onMapStatusChangeStart(MapStatus status, int reason) {
            mMapStatus = status;
        }

        /**
         * 地图状态变化中
         * @param status 当前地图状态
         */
        @Override
        public void onMapStatusChange(MapStatus status) {


        }

        /**
         * 地图状态改变结束
         * @param status 地图状态改变结束后的地图状态
         */
        @Override
        public void onMapStatusChangeFinish(final MapStatus status) {
            if (status.rotate != mMapStatus.rotate) {
                mActivity.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mTrackerMarker != null) {
                                float newRotate = mMapView.getMap().getMapStatus().rotate - mTracker.getHeading();
                                if (newRotate < 0) newRotate = 360 + newRotate;
                                mTrackerMarker.setRotate(newRotate);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            mMapStatus = status;
        }
    }
}
