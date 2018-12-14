package com.tun.app.iats_app.fragment;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.tun.app.iats_app.protocol.tagvalue.Field;
import com.tun.app.iats_app.protocol.tagvalue.Tag;
import com.tun.app.iats_app.R;
import com.tun.app.iats_app.entities.Tracker;
import com.tun.app.iats_app.entities.TrackerParameter;
import com.tun.app.iats_app.service.TrackerAntennaService;
import com.tun.app.iats_app.utils.DigitsUtils;
import com.tun.app.iats_app.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * 作者：TanTun
 * 时间：2018/7/17
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class SettingDebugFragment extends Fragment {

    private static final String TAG = "SettingTrackerFragment";

    TrackerAntennaService.TrackerServiceBinder mTrackerServiceBinder;

    TrackerParameter mTrackerParameter;
    Tracker mTracker = null;

    Switch mSwitch_auto_guide;

    Button mButton_base_calibrate;
    Button mButton_base_heading;
    Button mButton_base_tilt;
    Button mButton_set_param_pid;
    Button mButton_set_param_servos;
    Button mButton_set_param_compass;

    EditText mTracker_tilt;
    EditText mTracker_heading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_debug, container, false);

        mSwitch_auto_guide = (Switch) view.findViewById(R.id.switch_debug_param_base_auto_guide);

        mButton_base_calibrate = (Button) view.findViewById(R.id.button_debug_param_base_calibrate);
        mButton_base_heading = (Button) view.findViewById(R.id.button_debug_param_base_heading);
        mButton_base_tilt = (Button) view.findViewById(R.id.button_debug_param_base_tilt);
        mButton_set_param_pid = (Button) view.findViewById(R.id.button_debug_param_set_param_pid);
        mButton_set_param_servos = (Button) view.findViewById(R.id.button_debug_param_set_param_servos);
        mButton_set_param_compass = (Button) view.findViewById(R.id.button_debug_param_set_param_compass);

        mTracker_tilt = (EditText) view.findViewById(R.id.edit_debug_param_base_tilt);
        mTracker_heading = (EditText) view.findViewById(R.id.edit_debug_param_base_heading);

        mSwitch_auto_guide.setOnCheckedChangeListener(new AutoGuide());
        mButton_base_calibrate.setOnClickListener(new Calibrate());
        mButton_base_heading.setOnClickListener(new SetHeading());
        mButton_base_tilt.setOnClickListener(new SetTilt());
        mButton_set_param_pid.setOnClickListener(new SetPidParameter());
        mButton_set_param_servos.setOnClickListener(new SetServosParameter());
        mButton_set_param_compass.setOnClickListener(new SetCompassParameter());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Intent bindIntent = new Intent(this.getContext(), TrackerAntennaService.class);
            this.getContext().bindService(bindIntent, connection, BIND_AUTO_CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            Log.d(TAG, "service connected = " + name.getClassName());

            mTrackerServiceBinder = (TrackerAntennaService.TrackerServiceBinder) service;
            mTrackerParameter = mTrackerServiceBinder.getTrackerParameter();
            mTracker = mTrackerServiceBinder.getTracker();

            mTracker_tilt.setText(DigitsUtils.FORMAT_ZERO_DIGITS.format(mTracker.getPitching()));
            mTracker_heading.setText(DigitsUtils.FORMAT_ZERO_DIGITS.format(mTracker.getHeading()));
        }
    };

    /**
     * 自动指北
     */
    private class AutoGuide implements Switch.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Integer value = 0x00;
            if (isChecked) value = 0x01;
            if (mTrackerServiceBinder.autoGuide(value)) {
                Log.d(TAG, "Auto_Guide() 自动指北：" + isChecked + "！");
            }
        }
    }

    /**
     * 较准
     */
    private class Calibrate implements  View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mTrackerServiceBinder.calibrate()) {
                Log.d(TAG, "Calibrate() 较准指令发送成功!");
            }
        }
    }

    /**
     * 指向
     */
    private class SetHeading implements  View.OnClickListener {

        @Override
        public void onClick(View v) {
            String value = mTracker_heading.getText().toString();
            if (!StringUtils.isNullOrEmpty((value))) {
                int heading = Integer.parseInt(value);

                if (heading > 359) heading = 359;
                if (heading < 0) heading = 0;

                if (mTrackerServiceBinder.SetHeading(heading * 10)) {
                    Log.d(TAG, "SetHeading() 指向指令发送成功!");
                }
            }
        }
    }

    /**
     * 俯仰
     */
    private class SetTilt implements  View.OnClickListener {

        @Override
        public void onClick(View v) {
            String value = mTracker_tilt.getText().toString();
            if (!StringUtils.isNullOrEmpty((value))) {
                int tilt = Integer.parseInt(value);

                if (tilt > 90) tilt = 90;
                if (tilt < 0) tilt = 0;

                if (mTrackerServiceBinder.SetTilt(tilt)) {
                    Log.d(TAG, "SetTilt() 俯仰指令发送成功!");
                }
            }
        }
    }

    /**
     * 设置PID参数
     */
    private class SetPidParameter implements  View.OnClickListener {

        @Override
        public void onClick(View v) {
            Map<Tag, Field> fields = new HashMap<>();
            fields.put(Tag.TAG_PARAM_PID_P,
                    Field.getInstance(Tag.TAG_PARAM_PID_P,  mTrackerParameter.getPid_p()));
            fields.put(Tag.TAG_PARAM_PID_I,
                    Field.getInstance(Tag.TAG_PARAM_PID_I,  mTrackerParameter.getPid_i()));
            fields.put(Tag.TAG_PARAM_PID_D,
                    Field.getInstance(Tag.TAG_PARAM_PID_D,  mTrackerParameter.getPid_d()));
            fields.put(Tag.TAG_PARAM_MAX_PID_ERROR,
                    Field.getInstance(Tag.TAG_PARAM_MAX_PID_ERROR,  mTrackerParameter.getPid_max_error()));

            if (mTrackerServiceBinder.setParameter(fields)) {
                Log.d(TAG, "SetPidParameter() 设置PID参数指令发送成功!");
            }
        }
    }

    /**
     * 设置舵机参数
     */
    private class SetServosParameter implements  View.OnClickListener {

        @Override
        public void onClick(View v) {
            Map<Tag, Field> fields = new HashMap<>();
            fields.put(Tag.TAG_PARAM_TILT_0,
                    Field.getInstance(Tag.TAG_PARAM_TILT_0,  mTrackerParameter.getTilt_min()));
            fields.put(Tag.TAG_PARAM_TILT_90,
                    Field.getInstance(Tag.TAG_PARAM_TILT_90,  mTrackerParameter.getTilt_max()));
            fields.put(Tag.TAG_PARAM_START_TRACKING_DISTANCE,
                    Field.getInstance(Tag.TAG_PARAM_START_TRACKING_DISTANCE,  mTrackerParameter.getStart_tracking_distance()));
            fields.put(Tag.TAG_PARAM_PAN_0,
                    Field.getInstance(Tag.TAG_PARAM_PAN_0,  mTrackerParameter.getPan_center()));
            fields.put(Tag.TAG_PARAM_MIN_PAN_SPEED,
                    Field.getInstance(Tag.TAG_PARAM_MIN_PAN_SPEED,  mTrackerParameter.getPan_min_speed()));

            if (mTrackerServiceBinder.setParameter(fields)) {
                Log.d(TAG, "SetServosParameter() 设置舵机参数指令发送成功!");
            }
        }
    }

    /**
     * 设置罗盘参数
     */
    private class SetCompassParameter implements  View.OnClickListener {

        @Override
        public void onClick(View v) {
            Map<Tag, Field> fields = new HashMap<>();
            fields.put(Tag.TAG_PARAM_OFFSET,
                    Field.getInstance(Tag.TAG_PARAM_OFFSET,  mTrackerParameter.getCompass_offset()));
            fields.put(Tag.TAG_PARAM_DECLINATION,
                    Field.getInstance(Tag.TAG_PARAM_DECLINATION,  mTrackerParameter.getCompass_declination()));

            if (mTrackerServiceBinder.setParameter(fields)) {
                Log.d(TAG, "Calibrate() 设置罗盘指令发送成功!");
            }
        }
    }
}
