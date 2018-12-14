package com.tun.app.iats_app.protocol;

import android.os.Handler;
import android.os.Message;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 作者：TanTun
 * 时间：2017/10/30
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class Parser {
    private Queue<byte[]> mFramesQueue = new LinkedList<>();
    private byte[] mBuffer = new byte[1024];
    private int mBufferIndex = 0;
    private int mFrameIndex = 0;
    private int mCheckByte = 0x00;
    private int mCammandIndex = 0;
    private int mSuccessCount = 0;
    private float mBluetoothQuality = 1;

    private Handler mHandlerOnFrame = null;

    private FrameHandleThread mFrameHandleThread = null;

    private DataState mState = DataState.IDLE;

    public Parser() {
        mFrameHandleThread = new FrameHandleThread();
        mFrameHandleThread.start();
    }

    public Handler getmHandlerOnFrame() {
        return mHandlerOnFrame;
    }

    public void setmHandlerOnFrame(Handler mHandlerOnFrame) {
        this.mHandlerOnFrame = mHandlerOnFrame;
    }

    public void analysis(byte[] data) {
        //逐字节解析数据
        for (byte aData : data) {
            //在没有读到帧头之前的数据全部丢弃
            if (mFrameIndex <= 0
                    && aData == Defines.TP_PACKET_LEAD
                    && mState == DataState.IDLE) {
                mBufferIndex = 0;
                mState = DataState.STATE_LEAD;
            } else if (aData == Defines.TP_PACKET_START
                    && mState == DataState.STATE_LEAD) {
                mState = DataState.STATE_START;
            } else if (mState == DataState.STATE_START) {
                mState = DataState.STATE_CMD;
            } else if (mState == DataState.STATE_CMD) {
                mFrameIndex = (aData & 0x0FF) + 1;
                mState = DataState.STATE_INDEX;
                mCammandIndex = aData;
            } else if (mState == DataState.STATE_INDEX) {
                mFrameIndex = (aData & 0x0FF) + 1;
                mState = DataState.STATE_LEN;
            } else if (mState == DataState.STATE_LEN) {
                mCheckByte = 0x00;
                mState = DataState.STATE_DATA;
            }

            if (mState != DataState.IDLE) {
                mBuffer[mBufferIndex] = aData;
                mBufferIndex++;
            }

            if (mState == DataState.STATE_DATA) {
                mFrameIndex--;
                if (mFrameIndex > 0) mCheckByte = (aData & 0x0FF) ^ mCheckByte;
                //当前帧中对应长度的数据已经接收完成，验证数据是否正确
                if (mFrameIndex <= 0) {
                    //首先验证校验位是否正确
                    if (mCheckByte == (aData & 0x0FF)) {
                        byte[] frame = new byte[mBufferIndex];
                        System.arraycopy(mBuffer, 0, frame, 0, mBufferIndex);
                        mFramesQueue.offer(frame);
                        mSuccessCount++;

                        if (mCammandIndex == 255 || mSuccessCount > mCammandIndex) {
                            mBluetoothQuality = mSuccessCount / 255;
                        }
                    }
                    mBufferIndex = 0;
                    mState = DataState.IDLE;
                }
            }
        }
    }

    private class FrameHandleThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                while (!mFramesQueue.isEmpty()) {
                    try {
                        byte[] frame = mFramesQueue.poll();

                        if (mHandlerOnFrame != null) {
                            Message msg = new Message();
                            msg.arg1 = 0;
                            msg.obj = Frame.parsing(frame);
                            mHandlerOnFrame.sendMessage(msg);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < 30 * 100; i++) //每30秒调度一次
                {
                    if (isInterrupted() || !mFramesQueue.isEmpty())
                        break;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
