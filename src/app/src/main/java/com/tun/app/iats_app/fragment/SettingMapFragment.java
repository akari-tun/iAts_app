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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.tun.app.iats_app.Observer.ObservableFunctionTag;
import com.tun.app.iats_app.Observer.ObservableManager;
import com.tun.app.iats_app.R;
import com.tun.app.iats_app.entities.MapParameter;
import com.tun.app.iats_app.service.TrackerAntennaService;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * 作者：TanTun
 * 时间：2018/7/17
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class SettingMapFragment extends Fragment {

    private static final String TAG = "SettingMapFragment";

    TrackerAntennaService.TrackerServiceBinder mTrackerServiceBinder;

    MapParameter mMapParameter;

    RadioGroup mRadioMapType;
    RadioGroup mRadioLocationMode;
    Switch mSwitchLine;
    Switch mSwitchSimulation;
    EditText mSimulation_lat;
    EditText mSimulation_lon;
    EditText mSimulation_alt;

    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_setting_map, container, false);

        mRadioMapType = (RadioGroup) mView.findViewById(R.id.radio_map_param_map_type);
        mRadioMapType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) mView.findViewById(checkedId);
                ObservableManager.instance().notify(ObservableFunctionTag.MAP_TYPE_CHANGE,
                        Integer.valueOf(radioButton.getTag().toString()));

                mMapParameter.setMap_type(Integer.valueOf(radioButton.getTag().toString()));
            }
        });

        mRadioLocationMode = (RadioGroup) mView.findViewById(R.id.radio_map_param_map_mode);
        mRadioLocationMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) mView.findViewById(checkedId);
                ObservableManager.instance().notify(ObservableFunctionTag.MAP_LOCATION_MODE_CHANGE,
                        Integer.valueOf(radioButton.getTag().toString()));

                mMapParameter.setMap_location_mode(Integer.valueOf(radioButton.getTag().toString()));
            }
        });

        mSwitchLine = (Switch) mView.findViewById(R.id.switch_map_param_map_line);
        mSwitchLine.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mSwitchLine.isChecked()) {
                    ObservableManager.instance().notify(ObservableFunctionTag.MAP_DRAW_LINE_CHANGE,
                            1);
                    mMapParameter.setMap_draw_line(1);
                } else {
                    ObservableManager.instance().notify(ObservableFunctionTag.MAP_DRAW_LINE_CHANGE,
                            0);
                    mMapParameter.setMap_draw_line(0);
                }
            }
        });

        mSwitchSimulation = (Switch) mView.findViewById(R.id.switch_map_param_location_simulation);
        mSimulation_lat = (EditText) mView.findViewById(R.id.edit_map_param_location_simulation_lat);
        mSimulation_lon = (EditText) mView.findViewById(R.id.edit_map_param_location_simulation_lon);
        mSimulation_alt = (EditText) mView.findViewById(R.id.edit_map_param_location_simulation_alt);

        return mView;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mMapParameter != null) {
            if (mSwitchSimulation.isChecked()) {
                mMapParameter.setMap_location_simulation(1);
            } else {
                mMapParameter.setMap_location_simulation(0);
            }
            mMapParameter.setMap_location_simulation_alt(Integer.parseInt(mSimulation_alt.getText().toString()));
            mMapParameter.setMap_location_simulation_lat(Double.parseDouble(mSimulation_lat.getText().toString()));
            mMapParameter.setMap_location_simulation_lon(Double.parseDouble(mSimulation_lon.getText().toString()));
            mMapParameter.save();
        }

        if (connection != null) {
            this.getContext().unbindService(connection);
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
            mMapParameter = mTrackerServiceBinder.getMapParameter();

            mSwitchLine.setChecked(mMapParameter.getMap_draw_line() == 1);

            RadioButton radioButton = null;
            switch (mMapParameter.getMap_type()) {
                case 1:
                    radioButton = (RadioButton) mView.findViewById(R.id.radio_map_param_map_type_normal);
                    break;
                case 2:
                    radioButton = (RadioButton) mView.findViewById(R.id.radio_map_param_map_type_satellite);
                    break;
            }

            if (radioButton != null) radioButton.setChecked(true);
            radioButton = null;

            switch (mMapParameter.getMap_location_mode()) {
                case 0:
                    radioButton = (RadioButton) mView.findViewById(R.id.radio_map_param_map_mode_normal);
                    break;
                case 1:
                    radioButton = (RadioButton) mView.findViewById(R.id.radio_map_param_map_mode_following);
                    break;
                case 2:
                    radioButton = (RadioButton) mView.findViewById(R.id.radio_map_param_map_mode_compass);
                    break;
            }

            if (radioButton != null) radioButton.setChecked(true);

            mSwitchSimulation.setChecked(mMapParameter.getMap_location_simulation() == 1);
            mSimulation_lat.setText(String.valueOf(mMapParameter.getMap_location_simulation_lat()));
            mSimulation_lon.setText(String.valueOf(mMapParameter.getMap_location_simulation_lon()));
            mSimulation_alt.setText(String.valueOf(mMapParameter.getMap_location_simulation_alt()));
        }
    };
}