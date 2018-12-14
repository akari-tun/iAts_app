package com.tun.app.iats_app.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 作者：TanTun
 * 时间：2017/2/22
 * 邮箱：32965926@qq.com
 * 描述：HTTP工具类
 */
public class HttpUtils {
    public static String get(String urlPath, String data) {
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length()));
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setDoOutput(true); // 发送POST请求必须设置允许输出
            // 获取URLConnection对象对应的输出流
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
            int code = connection.getResponseCode();
            if (code == 200) {
                return changeInputStream(connection.getInputStream());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    public static String post(String urlPath, String data) {
        try {
            URL url = new URL(urlPath);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("accept", "application/json;");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Length", String.valueOf(data.length()));
            connection.setRequestProperty("DataType", "json");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            //connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setDoOutput(true); // 发送POST请求必须设置允许输出
            // 获取URLConnection对象对应的输出流
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            wr.close();
            int code = connection.getResponseCode();
            if (code == 200) {
                return changeInputStream(connection.getInputStream());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return "";
    }

    private static String changeInputStream(InputStream inputStream) {
        String JsonString = "";
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len = 0;
        byte[] data = new byte[1024];
        try {
            while ((len = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, len);
            }
            JsonString = new String(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonString;
    }
}
