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
import android.widget.EditText;

import com.tun.app.iats_app.R;
import com.tun.app.iats_app.entities.TrackerParameter;
import com.tun.app.iats_app.service.TrackerAntennaService;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * 作者：TanTun
 * 时间：2018/7/20
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class SettingTrackerFragment extends Fragment {

    private static final String TAG = "SettingTrackerFragment";

    TrackerAntennaService.TrackerServiceBinder mTrackerServiceBinder;

    TrackerParameter mTrackerParameter;

    EditText mTracker_bluetooth;
    EditText mTracker_start_tracking_distance;
    EditText mPid_p;
    EditText mPid_i;
    EditText mPid_d;
    EditText mPid_divider;
    EditText mPid_max_error;
    EditText mServos_tilt_min;
    EditText mServos_tilt_max;
    EditText mServos_pan_center;
    EditText mServos_pan_speed;
    EditText mCompass_offset;
    EditText mCompass_declination;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_tracker, container, false);

        mTracker_bluetooth = (EditText) view.findViewById(R.id.edit_tracker_param_tracker_bluetooth);
        mTracker_start_tracking_distance = (EditText) view.findViewById(R.id.edit_tracker_param_tracker_start_tracking_distance);
        mPid_p = (EditText) view.findViewById(R.id.edit_tracker_param_pid_p);
        mPid_i = (EditText) view.findViewById(R.id.edit_tracker_param_pid_i);
        mPid_d = (EditText) view.findViewById(R.id.edit_tracker_param_pid_d);
        mPid_divider = (EditText) view.findViewById(R.id.edit_tracker_param_pid_divider);
        mPid_max_error = (EditText) view.findViewById(R.id.edit_tracker_param_pid_max_error);
        mServos_tilt_min = (EditText) view.findViewById(R.id.edit_tracker_param_servos_tilt_min);
        mServos_tilt_max = (EditText) view.findViewById(R.id.edit_tracker_param_servos_tilt_max);
        mServos_pan_center = (EditText) view.findViewById(R.id.edit_tracker_param_servos_pan_center);
        mServos_pan_speed = (EditText) view.findViewById(R.id.edit_tracker_param_servos_pan_speed);
        mCompass_offset = (EditText) view.findViewById(R.id.edit_tracker_param_compass_offset);
        mCompass_declination = (EditText) view.findViewById(R.id.edit_tracker_param_compass_declination);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mTrackerParameter != null) {
            mTrackerParameter.setBluetoothName(mTracker_bluetooth.getText().toString().trim());
            mTrackerParameter.setStart_tracking_distance(Integer.parseInt(mTracker_start_tracking_distance.getText().toString()));
            mTrackerParameter.setPid_p(Integer.parseInt(mPid_p.getText().toString()));
            mTrackerParameter.setPid_i(Integer.parseInt(mPid_i.getText().toString()));
            mTrackerParameter.setPid_d(Integer.parseInt(mPid_d.getText().toString()));
            mTrackerParameter.setPid_divider(Integer.parseInt(mPid_divider.getText().toString()));
            mTrackerParameter.setPid_max_error(Integer.parseInt(mPid_max_error.getText().toString()));
            mTrackerParameter.setTilt_min(Integer.parseInt(mServos_tilt_min.getText().toString()));
            mTrackerParameter.setTilt_max(Integer.parseInt(mServos_tilt_max.getText().toString()));
            mTrackerParameter.setPan_center(Integer.parseInt(mServos_pan_center.getText().toString()));
            mTrackerParameter.setPan_min_speed(Integer.parseInt(mServos_pan_speed.getText().toString()));
            mTrackerParameter.setCompass_offset(Integer.parseInt(mCompass_offset.getText().toString()));
            mTrackerParameter.setCompass_declination(Integer.parseInt(mCompass_declination.getText().toString()));

            mTrackerParameter.save();
        }

        if (connection != null) {
            this.getContext().unbindService(connection);
        }
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

            mTracker_bluetooth.setText(mTrackerParameter.getBluetoothName());
            mTracker_start_tracking_distance.setText(String.valueOf(mTrackerParameter.getStart_tracking_distance()));
            mPid_p.setText(String.valueOf(mTrackerParameter.getPid_p()));
            mPid_i.setText(String.valueOf(mTrackerParameter.getPid_i()));
            mPid_d.setText(String.valueOf(mTrackerParameter.getPid_d()));
            mPid_divider.setText(String.valueOf(mTrackerParameter.getPid_divider()));
            mPid_max_error.setText(String.valueOf(mTrackerParameter.getPid_max_error()));
            mServos_tilt_min.setText(String.valueOf(mTrackerParameter.getTilt_min()));
            mServos_tilt_max.setText(String.valueOf(mTrackerParameter.getTilt_max()));
            mServos_pan_center.setText(String.valueOf(mTrackerParameter.getPan_center()));
            mServos_pan_speed.setText(String.valueOf(mTrackerParameter.getPan_min_speed()));
            mCompass_offset.setText(String.valueOf(mTrackerParameter.getCompass_offset()));
            mCompass_declination.setText(String.valueOf(mTrackerParameter.getCompass_declination()));
        }
    };

}
