package com.tun.app.iats_app.fragment;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.tun.app.iats_app.R;

/**
 * 作者：TanTun
 * 时间：2018/7/17
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class SettingFragment extends DialogFragment {

    public final static String DEBUG_FRAGMENT = "DEBUG";
    public final static String TRACKER_FRAGMENT = "TRACKER";
    public final static String MAP_FRAGMENT = "MAP";

    Fragment mDebug_fragment = null;
    Fragment mTracker_fragment = null;
    Fragment mMap_fragment = null;

    RadioGroup mRadioGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Fragment_No_Dim);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        Window dialogWindow = getDialog().getWindow();
        assert dialogWindow != null;
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.alpha = 0.7f;
        dialogWindow.setAttributes(lp);

        mDebug_fragment = new SettingDebugFragment();
        mTracker_fragment = new SettingTrackerFragment();
        mMap_fragment = new SettingMapFragment();

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.right_fragment_layout, mDebug_fragment);
        transaction.commit();

        mRadioGroup = (RadioGroup) view.findViewById(R.id.radio_setting_menu);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment = null;
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch (checkedId) {
                    case R.id.radio_setting_menu_debug:
                        fragment = mDebug_fragment;
                        break;
                    case R.id.radio_setting_menu_tracker:
                        fragment = mTracker_fragment;
                        break;
                    case R.id.radio_setting_menu_map:
                        fragment = mMap_fragment;
                        break;
                }

                if (fragment != null) {
                    transaction.replace(R.id.right_fragment_layout, fragment);
                    transaction.commit();
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.remove(mDebug_fragment);
        transaction.remove(mTracker_fragment);
        transaction.remove(mMap_fragment);
        transaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            Window dialogWindow = getDialog().getWindow();
            assert dialogWindow != null;
            dialogWindow.setLayout(1400, 850);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
