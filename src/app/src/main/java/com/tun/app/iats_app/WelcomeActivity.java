package com.tun.app.iats_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends Activity {

    //private Button btnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tun.app.iats_app.R.layout.activity_welcome);

        //获取并缩放图标
        Drawable drawable = ContextCompat.getDrawable(this, com.tun.app.iats_app.R.mipmap.bluetooth);
        //获取到控件
        ImageView iv = (ImageView) findViewById(com.tun.app.iats_app.R.id.image_bluetooth);
        iv.setImageDrawable(drawable);

        TextView tv = (TextView) findViewById(com.tun.app.iats_app.R.id.version);
        tv.setText(getVerName(this));

        Handler handler = new Handler();
        //当计时结束,跳转至主界面
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent main = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(main);
                WelcomeActivity.this.finish();
            }
        }, 2000);

        Log.d("WelcomeActivity-->", "onCreate");
    }

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            //Manifest.permission.BLUETOOTH_PRIVILEGED,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WAKE_LOCK
    };

    private static final int PERMISSON_REQUEST_CODE = 0;
    private static final int SETTING_REQUEST_CODE = 1;
    private static final int BLUETOOTH_REQUEST_CODER = 2;
    private static final int GPS_REQUEST_CODE = 10;

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @Override
    protected void onResume() {
        super.onResume();
        if (isNeedCheck) {
            checkPermissions(needPermissions);
            checkGPSIsOpen();
            checkBluetoothIsOpen();
        }
    }

    /**
     * @param permissions 权限列表
     * @since 2.5.0
     */
    private void checkPermissions(String... permissions) {
        List<String> list = findDeniedPermissions(permissions);
        if (null != list
                && list.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    list.toArray(
                            new String[list.size()]),
                    PERMISSON_REQUEST_CODE);
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions 权限列表
     * @return 权限
     */
    private List<String> findDeniedPermissions(String[] permissions) {
        List<String> list = new ArrayList<>();
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(this,
                    perm) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, perm)) {
                list.add(perm);
            }
        }
        return list;
    }

    /**
     * 检测是否说有的权限都已经授权
     *
     * @param grantResults 结果
     * @return 是否都已授权
     */
    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检测GPS是否打开
     */
    private void checkGPSIsOpen() {
        boolean isOpen;
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        isOpen = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);

        if (!isOpen) {
            showMissingPermissionDialog(com.tun.app.iats_app.R.string.notify_title,
                    com.tun.app.iats_app.R.string.msg_not_gps_permission,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //跳转GPS设置界面
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, GPS_REQUEST_CODE);
                        }
                    });
        }
    }

    /**
     * 检测蓝牙是否打开
     */
    private void checkBluetoothIsOpen() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        assert mBluetoothAdapter != null;

        if(!mBluetoothAdapter.isEnabled()) {
            //弹出对话框提示用户是后打开
            Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler, BLUETOOTH_REQUEST_CODER);

            if (mBluetoothAdapter.getScanMode() !=
                    BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                Intent discoverableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(
                        BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
                startActivity(discoverableIntent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
            @NonNull String[] permissions, @NonNull int[] paramArrayOfInt) {
        if (requestCode == PERMISSON_REQUEST_CODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog(com.tun.app.iats_app.R.string.notify_title,
                        com.tun.app.iats_app.R.string.msg_not_enough_permission,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //启动APP设置
                                Intent intent = new Intent(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivityForResult(intent, SETTING_REQUEST_CODE);
                            }
                        });
                isNeedCheck = false;
            }
        }
    }

    /**
     * 显示提示信息
     *
     * @since 2.5.0
     */
    private void showMissingPermissionDialog(int title, int msg,
                                             DialogInterface.OnClickListener settinglinstener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(msg);

        // 拒绝, 退出应用
        builder.setNegativeButton(com.tun.app.iats_app.R.string.button_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        builder.setPositiveButton(com.tun.app.iats_app.R.string.button_setting,
                settinglinstener);

        builder.setCancelable(false);

        builder.show();
    }

    /**
     * 启动应用的设置
     *
     * @since 2.5.0
     */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQUEST_CODE) {
            checkPermissions(needPermissions);
        }
        if (requestCode == GPS_REQUEST_CODE) {
            checkGPSIsOpen();
        }
        if (requestCode == BLUETOOTH_REQUEST_CODER) {
            checkBluetoothIsOpen();
        }
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return 版本号
     */
    public String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
