package com.tun.app.iats_app.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 作者：TanTun
 * 时间：2018/7/15
 * 邮箱：32965926@qq.com
 * 描述：
 */

public class ImageUtils {

    public static Bitmap rotate(Bitmap image, int angle) {
        Matrix matrix = new Matrix(); //旋转图片 动作
        matrix.setRotate(angle);//旋转角度
        // 创建新的图片
        return Bitmap.createBitmap(image, 0, 0,
                image.getWidth(), image.getHeight(), matrix, true);
    }
}
