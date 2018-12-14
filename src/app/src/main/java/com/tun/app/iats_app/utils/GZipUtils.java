package com.tun.app.iats_app.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 作者：TanTun
 * 时间：2017/2/22
 * 邮箱：32965926@qq.com
 * 描述：GZip工具类
 */

public class GZipUtils {
    /**
     * Gzip 压缩数据
     *
     * @param data 压缩前的数据
     * @return 压缩后的字符串
     */
    public static String compressForGzip(byte[] data) {

        if (data == null || data.length <= 0) {
            return null;
        }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            GZIPOutputStream gzip = new GZIPOutputStream(baos);
            gzip.write(data);
            gzip.close();
            byte[] encode = baos.toByteArray();
            baos.flush();
            baos.close();
            return Base64.encodeToString(encode, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gzip解压数据
     *
     * @param data 解压缩前的数据
     * @return 解压缩后的数据
     */
    public static byte[] decompressForGzip(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        byte[] t = Base64.decode(data, Base64.DEFAULT);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayInputStream in = new ByteArrayInputStream(t);
            GZIPInputStream gzip = new GZIPInputStream(in);
            byte[] buffer = new byte[1024];
            int n;
            while ((n = gzip.read(buffer, 0, buffer.length)) > 0) {
                out.write(buffer, 0, n);
            }
            gzip.close();
            in.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
