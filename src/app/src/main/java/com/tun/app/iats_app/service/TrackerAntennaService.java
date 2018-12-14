package com.tun.app.iats_app.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tun.app.iats_app.bluetooth.BT_New;
import com.tun.app.iats_app.entities.Airplane;
import com.tun.app.iats_app.entities.MapParameter;
import com.tun.app.iats_app.entities.Tracker;
import com.tun.app.iats_app.entities.TrackerParameter;
import com.tun.app.iats_app.protocol.Cmd;
import com.tun.app.iats_app.protocol.Defines;
import com.tun.app.iats_app.protocol.Frame;
import com.tun.app.iats_app.protocol.Parser;
import com.tun.app.iats_app.protocol.tagvalue.Field;
import com.tun.app.iats_app.protocol.tagvalue.Tag;
import com.tun.app.iats_app.MainActivity;
import com.tun.app.iats_app.R;
import com.tun.app.iats_app.action.TrackerAction;
import com.tun.app.iats_app.gps.GPSLocationListener;
import com.tun.app.iats_app.gps.GPSLocationManager;
import com.tun.app.iats_app.utils.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 作者：TanTun
 * 时间：2018/1/14
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class TrackerAntennaService extends Service {

    public static final String TAG = "TrackerAntennaService";

    private volatile static TrackerAntennaService mService;

    BluetoothAdapter mBluetoothAdapter;

    BT_New mBluetooth;

    TrackerAction mAction = null;

    GPSLocationManager mGPSLocationManager;
    GPSListener mGpsListener;
    Parser mParser;

    Airplane mAirplane = null;
    Tracker mTracker = null;
    TrackerParameter mTrackerParameter = null;
    MapParameter mMapParameter = null;

    BluetoothThread mBluetoothThread = null;
    BluetoothHandler mBluetoothHandler = null;

    FrameHandler mFrameHandler = null;

    TrackerServiceBinder mBinder = null;

    TrackerAntennaServiceListener mTrackerServiceListener;

    List<CtrWaiter> mCtrWaiters = null;

    public class CtrWaiter {
        public com.tun.app.iats_app.protocol.Cmd Cmd;
        public Date StartTime;
        public int WaitMillis;
        List<Tag> Tags;

        CtrWaiter() {
            Tags = new ArrayList<>();
        }
    }

    /**
     * 描述：服务状态枚举
     */
    public enum TrackerServiceStatus {
        Initializing,
        Finding,
        Connecting,
        Connected,
        Tracking,
        Manual
    }

    /**
     * 首次创建服务时，系统将调用此方法来执行一次性设置程序（在调用 onStartCommand() 或 onBind() 之前）。
     * 如果服务已在运行，则不会调用此方法。该方法只被调用一次
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mGPSLocationManager = GPSLocationManager.getInstances(TrackerAntennaService.this);
        mGPSLocationManager.setScanSpan(5000);
        mGPSLocationManager.setMinDistance(10);
        mGpsListener = new GPSListener();
        mFrameHandler = new FrameHandler(this);
        mBluetoothHandler = new BluetoothHandler(this);
        mAction = new TrackerAction();
        mAirplane = new Airplane();
        mTracker = new Tracker();
        mTrackerParameter = new TrackerParameter(TrackerAntennaService.this);
        mMapParameter = new MapParameter(TrackerAntennaService.this);
        mParser = new Parser();

        mCtrWaiters = new ArrayList<>();

        mBinder = new TrackerServiceBinder();

        mGPSLocationManager.setMapParameter(mMapParameter);

        setTrackerStatus(TrackerServiceStatus.Initializing);
    }

    /**
     * 每次通过startService()方法启动Service时都会被回调。
     * @param intent 意图
     * @param flags 请求时是否有额外数据
     * @param startId 服务的唯一ID
     * @return 不知道
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");

        if (mMapParameter.getMap_location_simulation() == 1) {
            mGPSLocationManager.startTestGpsLocation(mGpsListener);
        } else {
            mGPSLocationManager.start(mGpsListener);
        }

        mParser.setmHandlerOnFrame(mFrameHandler);

        mBluetoothThread = new BluetoothThread(this);
        mBluetoothThread.start();

        mService = this;

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 服务销毁时的回调
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");

        mService = null;
    }

    /**
     * 绑定服务时才会调用
     * 必须要实现的方法
     * @param intent 意图
     * @return IBinder
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    /**
     * 服务是否存活
     *
     * @return true: 存活
     */
    public static boolean isServiceRunning() {
        return mService != null;
    }

    public class TrackerServiceBinder extends Binder {

        public void setTrackerAntennaServiceListener(TrackerAntennaServiceListener listener) {
            Log.d(TAG, "setTrackerAntennaServiceListener() executed");
            mTrackerServiceListener = listener;
        }

        public void unlistener() {
            Log.d(TAG, "setTrackerAntennaServiceListener() executed");
            mTrackerServiceListener = null;
        }

        public Airplane getAirplane() {
            return mAirplane;
        }

        public Tracker getTracker() {
            return mTracker;
        }

        public TrackerParameter getTrackerParameter() {
            return mTrackerParameter;
        }
        public MapParameter getMapParameter() {
            return mMapParameter;
        }

        /**
         *  设置家
         */
        public void setTrackerHome() {
            Log.d(TAG, "setTrackerHome() executed");

            if (mAction != null) {
                Map<Tag, Field> fields = new HashMap<>();
                fields.put(Tag.TAG_HOME_LATITUDE,
                        Field.getInstance(Tag.TAG_HOME_LATITUDE,
                                Math.round(mTracker.getLatitude() * 10000000L)));
                fields.put(Tag.TAG_HOME_LONGITUDE,
                        Field.getInstance(Tag.TAG_HOME_LONGITUDE,
                                Math.round(mTracker.getLongitude() * 10000000L)));
                fields.put(Tag.TAG_HOME_ALTITUDE,
                        Field.getInstance(Tag.TAG_HOME_ALTITUDE,
                                (int)(mTracker.getAltitude() * 100L)));
                mAction.setFrame(new Frame(Cmd.CMD_D_SET_HOME, fields));
            }
        }

        /**
         *  设置模式
         *  @param mode 模式 0：手动 1：自动 2：调试
         *  @return 是否成功
         */
        public boolean setTrackerMode(int mode) {
            Log.d(TAG, "setTrackerMode() executed");

            boolean ret = false;

            if (mAction != null) {
                for (CtrWaiter cmd : mCtrWaiters
                     ) {
                    if (cmd.Cmd == Cmd.CMD_D_CONTROL) {
                        String info = "指令 [" + cmd.Cmd.getName() + "] 正在执行，请勿重复操作!";
                        mTrackerServiceListener.OnMessageShow(info);
                        return false;
                    }
                }

                Map<Tag, Field> fields = new HashMap<>();
                fields.put(Tag.TAG_CTR_MODE,
                        Field.getInstance(Tag.TAG_CTR_MODE, mode));
                mAction.setFrame(new Frame(Cmd.CMD_D_CONTROL, fields));

                CtrWaiter waiter = new CtrWaiter();
                waiter.Cmd = Cmd.CMD_D_CONTROL;
                waiter.Tags.add(Tag.TAG_CTR_MODE);
                waiter.StartTime = new Date(System.currentTimeMillis());
                waiter.WaitMillis = 2000;

                mCtrWaiters.add(waiter);

                //当计时结束，清理超时指令
                new Handler().postDelayed(new WaiterRunnable(waiter), waiter.WaitMillis);

                ret = true;
            }

            return ret;
        }

        /**
         *  设置自动指北
         *  @param value 模式 0：关闭 1：开启
         *  @return 是否成功
         */
        public boolean autoGuide(Integer value) {
            Log.d(TAG, "setAutoGuide() executed");

            boolean ret = false;

            if (mAction != null) {
                for (CtrWaiter cmd : mCtrWaiters
                        ) {
                    if (cmd.Cmd == Cmd.CMD_D_CONTROL) {
                        String info = "指令 [" + cmd.Cmd.getName() + "] 正在执行，请勿重复操作!";
                        mTrackerServiceListener.OnMessageShow(info);
                        return false;
                    }
                }

                Map<Tag, Field> fields = new HashMap<>();
                fields.put(Tag.TAG_CTR_AUTO_POINT_TO_NORTH,
                        Field.getInstance(Tag.TAG_CTR_AUTO_POINT_TO_NORTH, value));
                mAction.setFrame(new Frame(Cmd.CMD_D_CONTROL, fields));

                CtrWaiter waiter = new CtrWaiter();
                waiter.Cmd = Cmd.CMD_D_CONTROL;
                waiter.Tags.add(Tag.TAG_CTR_AUTO_POINT_TO_NORTH);
                waiter.StartTime = new Date(System.currentTimeMillis());
                waiter.WaitMillis = 2000;

                mCtrWaiters.add(waiter);

                //当计时结束，清理超时指令
                new Handler().postDelayed(new WaiterRunnable(waiter), waiter.WaitMillis);

                ret = true;
            }

            return ret;
        }

        /**
         *  较准
         *  @return 是否成功
         */
        public boolean calibrate() {
            Log.d(TAG, "calibrate() executed");

            boolean ret = false;

            if (mAction != null) {
                for (CtrWaiter cmd : mCtrWaiters
                        ) {
                    if (cmd.Cmd == Cmd.CMD_D_CONTROL) {
                        String info = "指令 [" + cmd.Cmd.getName() + "] 正在执行，请勿重复操作!";
                        mTrackerServiceListener.OnMessageShow(info);
                        return false;
                    }
                }

                Map<Tag, Field> fields = new HashMap<>();
                fields.put(Tag.TAG_CTR_CALIBRATE,
                        Field.getInstance(Tag.TAG_CTR_CALIBRATE, 0x01));
                mAction.setFrame(new Frame(Cmd.CMD_D_CONTROL, fields));

                CtrWaiter waiter = new CtrWaiter();
                waiter.Cmd = Cmd.CMD_D_CONTROL;
                waiter.Tags.add(Tag.TAG_CTR_CALIBRATE);
                waiter.StartTime = new Date(System.currentTimeMillis());
                waiter.WaitMillis = 15000;

                mCtrWaiters.add(waiter);

                //当计时结束，清理超时指令
                new Handler().postDelayed(new WaiterRunnable(waiter), waiter.WaitMillis);

                ret = true;
            }

            return ret;
        }

        /**
         *  设置指向
         *  @return 是否成功
         */
        public boolean SetHeading(int heading) {
            Log.d(TAG, "SetHeading() executed");

            boolean ret = false;

            if (mAction != null) {
                for (CtrWaiter cmd : mCtrWaiters
                        ) {
                    if (cmd.Cmd == Cmd.CMD_D_CONTROL) {
                        String info = "指令 [" + cmd.Cmd.getName() + "] 正在执行，请勿重复操作!";
                        mTrackerServiceListener.OnMessageShow(info);
                        return false;
                    }
                }

                Map<Tag, Field> fields = new HashMap<>();
                fields.put(Tag.TAG_CTR_HEADING,
                        Field.getInstance(Tag.TAG_CTR_HEADING, heading));
                mAction.setFrame(new Frame(Cmd.CMD_D_CONTROL, fields));

                CtrWaiter waiter = new CtrWaiter();
                waiter.Cmd = Cmd.CMD_D_CONTROL;
                waiter.Tags.add(Tag.TAG_CTR_HEADING);
                waiter.StartTime = new Date(System.currentTimeMillis());
                waiter.WaitMillis = 1500;

                mCtrWaiters.add(waiter);

                //当计时结束，清理超时指令
                new Handler().postDelayed(new WaiterRunnable(waiter), waiter.WaitMillis);

                ret = true;
            }

            return ret;
        }

        /**
         *  设置俯仰
         *  @return 是否成功
         */
        public boolean SetTilt(int heading) {
            Log.d(TAG, "SetTilt() executed");

            boolean ret = false;

            if (mAction != null) {
                for (CtrWaiter cmd : mCtrWaiters
                        ) {
                    if (cmd.Cmd == Cmd.CMD_D_CONTROL) {
                        String info = "指令 [" + cmd.Cmd.getName() + "] 正在执行，请勿重复操作!";
                        mTrackerServiceListener.OnMessageShow(info);
                        return false;
                    }
                }

                Map<Tag, Field> fields = new HashMap<>();
                fields.put(Tag.TAG_CTR_TILT,
                        Field.getInstance(Tag.TAG_CTR_TILT, heading));
                mAction.setFrame(new Frame(Cmd.CMD_D_CONTROL, fields));

                CtrWaiter waiter = new CtrWaiter();
                waiter.Cmd = Cmd.CMD_D_CONTROL;
                waiter.Tags.add(Tag.TAG_CTR_TILT);
                waiter.StartTime = new Date(System.currentTimeMillis());
                waiter.WaitMillis = 1500;

                mCtrWaiters.add(waiter);

                //当计时结束，清理超时指令
                new Handler().postDelayed(new WaiterRunnable(waiter), waiter.WaitMillis);

                ret = true;
            }

            return ret;
        }

        /**
         * 设置参数
         * @param fields 参数
         * @return 是否成功
         */
        public boolean setParameter(Map<Tag, Field> fields) {

            boolean ret = false;

            if (mAction != null) {

                for (CtrWaiter cmd : mCtrWaiters
                        ) {
                    if (cmd.Cmd == Cmd.CMD_D_SET_PARA) {
                        String info = "指令 [" + cmd.Cmd.getName() + "] 正在执行，请勿重复操作!";
                        mTrackerServiceListener.OnMessageShow(info);
                        return false;
                    }
                }

                mAction.setFrame(new Frame(Cmd.CMD_D_SET_PARA, fields));

                CtrWaiter waiter = new CtrWaiter();
                waiter.Cmd = Cmd.CMD_D_SET_PARA;
                waiter.StartTime = new Date(System.currentTimeMillis());
                waiter.WaitMillis = 2000;

                mCtrWaiters.add(waiter);

                //当计时结束，清理超时指令
                new Handler().postDelayed(new WaiterRunnable(waiter), waiter.WaitMillis);

                ret = true;
            }

            return ret;

        }

        /**
         *  等待命令超时清理指令
         */
        private class WaiterRunnable implements Runnable {

            CtrWaiter mWaiter;

            WaiterRunnable (CtrWaiter waiter) {
                mWaiter = waiter;
            }

            @Override
            public void run() {
                if (mCtrWaiters.contains(mWaiter)) {

                    String info = "指令 [" + mWaiter.Cmd.getName() + "] 超时!";

                    Log.d(TAG, "WaiterRunnable() " + info);
                    mCtrWaiters.remove(mWaiter);
                    if (mTrackerServiceListener != null){
                        mTrackerServiceListener.OnMessageShow(info);
                    }
                }
            }
        }
    }

    /**
     * 设置通知信息
     */
    private void setNotifyContent(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        // 必需的通知内容
        builder.setContentTitle("自动跟踪云台")
                .setContentText(content)
                .setSmallIcon(R.mipmap.ic_launcher);

        Intent notifyIntent = new Intent(this, MainActivity.class);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(notifyPendingIntent);
        Notification notification = builder.build();

        this.startForeground(1, notification);
    }

    /**
     * 设置通知信息
     */
    private void setTrackerStatus(TrackerServiceStatus status) {
        if (mTracker.getStatus() != status) {
            mTracker.setStatus(status);

            switch (status) {
                case Initializing:
                    setNotifyContent("正在初始化。。。");
                    break;
                case Finding:
                    setNotifyContent("正在寻找云台。。。");
                    break;
                case Connecting:
                    setNotifyContent("正在连接云台。。。");
                    break;
                case Connected:
                    setNotifyContent("成功连接云台");
                    break;
                case Tracking:
                    setNotifyContent("正在跟踪。。。");
                    break;
                case Manual:
                    setNotifyContent("手动操作");
                    break;
            }
        }

        if (mTrackerServiceListener != null) {
            mTrackerServiceListener.UpdateServiceStatus(status);
        }
    }

    /**
     * 自动连接蓝牙
     */
    private static class BluetoothThread extends Thread {

        private final WeakReference<TrackerAntennaService> mService;

        private BluetoothThread(TrackerAntennaService service) {
            mService = new WeakReference<>(service);
        }

        private void setIsRunning(boolean value) {
            mIsRunning = value;
        }

        private boolean getIsRunning() {
            return mIsRunning;
        }

        private boolean mIsRunning = true;

        @Override
        public void run() {
            super.run();
            while (!isInterrupted() && mIsRunning) {
                try {

                    TrackerAntennaService service = mService.get();

                    if (service.mBluetooth == null || service.mBluetooth.getState() == 0) {

                        service.setTrackerStatus(TrackerServiceStatus.Finding);

                        //获取本机蓝牙名称与地址
                        Log.d(TAG, "bluetooth name = " + service.mBluetoothAdapter.getName());
                        //获取已配对蓝牙设备
                        Set<BluetoothDevice> devices = service.mBluetoothAdapter.getBondedDevices();
                        Log.d(TAG, "bonded device size = " + devices.size());

                        for (BluetoothDevice device : devices) {
                            Log.d(TAG, "bonded device name = " + device.getName() + " address" + device.getAddress());
                            String name = device.getName();
                            if (name != null && name.equals(service.mTrackerParameter.getBluetoothName())) {

                                service.mBluetoothAdapter.cancelDiscovery();

                                if (service.mBluetooth == null) {
                                    service.mBluetooth = new BT_New(service);
                                    service.mBluetooth.setHandler(service.mBluetoothHandler);
                                }

                                service.mBluetooth.connect(device);
                            }
                        }
                    } else if (service.mBluetooth.getState() == 3) {
                        service.mBluetooth.stop();
                        service.mBluetooth.Close();
                    }

                    for (int i = 0; i < 2 * 100; i++) //每2秒调度一次
                    {
                        if (isInterrupted() || !mIsRunning)
                            break;
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理蓝牙连接相关
     */
    private static class BluetoothHandler extends Handler {

        private final WeakReference<TrackerAntennaService> mService;

        private BluetoothHandler(TrackerAntennaService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            TrackerAntennaService service = mService.get();

            switch (msg.what) {
                case BT_New.MESSAGE_STATE_CHANGE:
                    if (msg.arg1 == BT_New.STATE_CONNECTED) {
                        service.mBluetoothThread.setIsRunning(false);
                        if (service.mBluetoothThread != null && service.mBluetoothThread.isAlive()) {
                            service.mBluetoothThread.interrupt();
                            service.mBluetoothThread = null;
                        }
                        service.mAction.start(service.mBluetooth);
                        service.setTrackerStatus(TrackerServiceStatus.Connected);
                    } else if (msg.arg1 == BT_New.STATE_CONNECTING) {
                        service.setTrackerStatus(TrackerServiceStatus.Connecting);
                    } else if (msg.arg1 == BT_New.STATE_NONE) {
                        if (service.mBluetoothThread == null || !service.mBluetoothThread.getIsRunning()) {
                            service.mAction.stop();
                            service.mBluetoothThread = new BluetoothThread(service);
                            service.mBluetoothThread.setIsRunning(true);
                            service.mBluetoothThread.start();
                        }
                    }
                    break;
                case BT_New.MESSAGE_READ:
                    if (msg.arg1 > 0) {
                        service.mParser.analysis((byte[]) msg.obj);
                    }
                    break;
            }
        }
    }

    /**
     * GPS事件
     */
    private class GPSListener implements GPSLocationListener {

        @Override
        public void UpdateLocation(Location location) {
            if (location != null) {
                Log.d(TAG, "经度：" + location.getLongitude() + "\n纬度：" + location.getLatitude());

                mTracker.setLongitude(location.getLongitude());
                mTracker.setLatitude(location.getLatitude());
                if ((float)location.getAltitude() < 0) {
                    mTracker.setAltitude(0.00f);
                } else {
                    mTracker.setAltitude((float)location.getAltitude());
                }

                if (mTrackerServiceListener != null) {
                    mTrackerServiceListener.UpdateLocation(location);
                }
            }
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {
            if (provider.equals("gps")) {
                if (mTrackerServiceListener != null) {
                    mTrackerServiceListener.UpdateGpsStatus(provider, status);
                }
            }
        }

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {

            mTracker.setGpsStatus(gpsStatus);

            if (mTrackerServiceListener != null) {
                mTrackerServiceListener.UpdateGpsProviderStatus(gpsStatus);
            }
        }
    }

    /**
     * 处理云台发送的数据
     */
    private static class FrameHandler extends Handler {

        private final WeakReference<TrackerAntennaService> mService;

        private FrameHandler(TrackerAntennaService service) {
            mService = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            try {

                TrackerAntennaService service = mService.get();
                Frame frame = (Frame)msg.obj;

                Log.d(TAG, frame.toString());

                switch (frame.getCmd()) {
                    case CMD_U_ACK:
                        if (frame.hasField(Tag.TAG_BASE_ACK)) {
                            Cmd cmd = Cmd.getCmd((byte)(int)frame.getField(Tag.TAG_BASE_ACK).getValue());
                            if (cmd != null) {

                                CtrWaiter waiter = null;

                                for (CtrWaiter item : service.mCtrWaiters
                                        ) {
                                    if (item.Cmd.getValue() == cmd.getValue()) {
                                        waiter = item;
                                        break;
                                    }
                                }

                                if (waiter != null) service.mCtrWaiters.remove(waiter);

                                String info = StringUtils.EMPTY;

                                switch (cmd) {
                                    case CMD_D_CONTROL:
                                        if (frame.hasField(Tag.TAG_CTR_MODE)) {
                                            switch ((int)frame.getField(Tag.TAG_CTR_MODE).getValue()) {
                                                case Defines.TRACKER_MODE_MANUAL:
                                                    info = "指令 [" + cmd.getName() + "] [" + frame.getField(Tag.TAG_CTR_MODE).getTag().getName() +
                                                            "：手动] 执行成功！";
                                                    break;
                                                case Defines.TRACKER_MODE_AUTO:
                                                    info = "指令 [" + cmd.getName() + "] [" + frame.getField(Tag.TAG_CTR_MODE).getTag().getName() +
                                                            "：自动] 执行成功！";
                                                    break;
                                                case Defines.TRACKER_MODE_DEBUG:
                                                    info = "指令 [" + cmd.getName() + "] [" + frame.getField(Tag.TAG_CTR_MODE).getTag().getName() +
                                                            "：调试] 执行成功！";
                                                    break;
                                            }
                                            if (!StringUtils.isNullOrEmpty(info))  {
                                                Log.d(TAG, info);
                                                service.mTrackerServiceListener.OnMessageShow((info));
                                            }
                                        }
                                        if (frame.hasField(Tag.TAG_CTR_AUTO_POINT_TO_NORTH)) {
                                            String enable = "开启";
                                            if (((int)frame.getField(Tag.TAG_CTR_AUTO_POINT_TO_NORTH).getValue()) == 1) {
                                                enable = "关闭";
                                            }
                                            info = "指令 [" + cmd.getName() + "] [" + frame.getField(Tag.TAG_CTR_AUTO_POINT_TO_NORTH).getTag().getName() +
                                                    "：" + enable + "] 执行成功！";
                                            Log.d(TAG, info);
                                            service.mTrackerServiceListener.OnMessageShow((info));
                                        }
                                        if (frame.hasField(Tag.TAG_CTR_CALIBRATE)) {
                                            info = "指令 [" + cmd.getName() + "] [" + frame.getField(Tag.TAG_CTR_MODE).getTag().getName() +
                                                    "] 执行成功！";
                                            Log.d(TAG, info);
                                            service.mTrackerServiceListener.OnMessageShow((info));
                                        }

                                        info = StringUtils.EMPTY;
                                        break;
                                    case CMD_D_SET_HOME:
                                        info = "指令 [" + cmd.getName() + "] 执行成功！";
                                        break;
                                }

                                if (!StringUtils.isNullOrEmpty(info)) service.mTrackerServiceListener.OnMessageShow((info));
                            }
                        }
                        break;
                    case CMD_U_HEARTBEAT:
                        if (frame.hasField(Tag.TAG_BASE_HEARTBEAT)) {
                            if ((int)frame.getField(Tag.TAG_BASE_HEARTBEAT).getValue() == 1) {
                                Map<Tag, Field> fields = new HashMap<>();
                                fields.put(Tag.TAG_BASE_ACK,
                                        Field.getInstance(Tag.TAG_BASE_ACK, 0x01));
                                service.mAction.setFrame(new Frame(Cmd.CMD_D_HEARTBEAT, fields));
                            }
                        }
                        if (frame.hasField(Tag.TAG_BASE_QUERY)) {
                            Cmd cmd = Cmd.getCmd((byte)(int)frame.getField(Tag.TAG_BASE_QUERY).getValue());
                            assert cmd != null;
                            switch (cmd) {
                                case CMD_D_SET_HOME:
                                    service.mBinder.setTrackerHome();
                                    break;
                            }
                        }
                        break;
                    case CMD_U_AIRPLANE:
                        if (frame.hasField(Tag.TAG_PLANE_LONGITUDE)) {
                            service.mAirplane.setLongitude((Long)frame.getField(Tag.TAG_PLANE_LONGITUDE).getValue() /
                                    10000000.00d);
                        }
                        if (frame.hasField(Tag.TAG_PLANE_LATITUDE)) {

                            service.mAirplane.setLatitude((Long)frame.getField(Tag.TAG_PLANE_LATITUDE).getValue() /
                                    10000000.00d);
                        }
                        if (frame.hasField(Tag.TAG_PLANE_ALTITUDE)) {
                            service.mAirplane.setAltitude((int)frame.getField(Tag.TAG_PLANE_ALTITUDE).getValue() / 100.00f);
                        }
                        if (frame.hasField(Tag.TAG_PLANE_STAR)) {
                            service.mAirplane.setStarts((int)frame.getField(Tag.TAG_PLANE_STAR).getValue());
                        }
                        if (frame.hasField(Tag.TAG_PLANE_SPEED)) {
                            service.mAirplane.setSpeed((int)frame.getField(Tag.TAG_PLANE_SPEED).getValue());
                        }
                        if (frame.hasField(Tag.TAG_PLANE_DISTANCE)) {
                            service.mAirplane.setDistance((long)frame.getField(Tag.TAG_PLANE_DISTANCE).getValue());
                        }
                        if (frame.hasField(Tag.TAG_PLANE_HEADING)) {
                            service.mAirplane.setHeading((int)frame.getField(Tag.TAG_PLANE_HEADING).getValue());
                        }

                        if (service.mTrackerServiceListener != null){
                            service.mTrackerServiceListener.UpdatePlaneData(service.mAirplane);
                        }
                        break;
                    case CMD_U_TRACKER:
                        if (frame.hasField(Tag.TAG_HOME_HEADING)) {
                            service.mTracker.setHeading((int)frame.getField(Tag.TAG_HOME_HEADING).getValue() /
                                    10.0f);
                        }
                        if (frame.hasField(Tag.TAG_HOME_PITCHING)) {
                            service.mTracker.setPitching((int)frame.getField(Tag.TAG_HOME_PITCHING).getValue());
                        }
                        if (frame.hasField(Tag.TAG_HOME_VOLTAGE)) {
                            service.mTracker.setVoltage((long)frame.getField(Tag.TAG_HOME_VOLTAGE).getValue() / 100.00f);
                        }
                        if (frame.hasField(Tag.TAG_HOME_MODE)) {
                            service.mTracker.setMode((int)frame.getField(Tag.TAG_HOME_MODE).getValue());
                        }

                        if (service.mTrackerServiceListener != null){
                            service.mTrackerServiceListener.UpdateTrackerData(service.mTracker);
                        }
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
