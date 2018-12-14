package com.tun.app.iats_app.action;

import com.tun.app.iats_app.bluetooth.BT_New;
import com.tun.app.iats_app.protocol.Frame;

import java.util.LinkedList;

/**
 * 作者：TanTun
 * 时间：2017/12/3
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class TrackerAction {

    private BT_New mBluetooth;
    private LinkedList<Frame> mQueue = new LinkedList<>();
    private boolean mIsRunning = false;
    private ActionHandleThread mThread = null;
    private int mIndex = 0;

    public void start(BT_New bluetooth) {
        mBluetooth = bluetooth;
        mIsRunning = true;
        mThread = new ActionHandleThread();
        mThread.start();
    }

    public void stop() {
        mIsRunning = false;

        if (mThread != null && mThread.isAlive()) {
            mThread.interrupt();
            mThread = null;
        }

        mBluetooth = null;
    }

    public void setFrame(Frame frame){

        if (mQueue.size() >= 10) {
            mQueue.pop();
        }

        mIndex++;
        frame.setIndex(mIndex);
        mQueue.push(frame);

        if (mIndex >= 255) mIndex = 0;
    }

    private class ActionHandleThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted() && mIsRunning) {
                try {
                    while (!mQueue.isEmpty()) {
                        Frame frame = mQueue.poll();
                        if (mBluetooth != null && frame != null) mBluetooth.Write(frame.toArray());
                    }

                    for (int i = 0; i < 30 * 100; i++) //每分钟调度一次
                    {
                        if (isInterrupted() || !mQueue.isEmpty() || !mIsRunning)
                            break;
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
