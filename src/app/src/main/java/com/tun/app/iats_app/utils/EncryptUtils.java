package com.tun.app.iats_app.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 作者：TanTun
 * 时间：2017/2/22
 * 邮箱：32965926@qq.com
 * 描述：加解密工具类
 */

public class EncryptUtils {

    /**
     * 作者：TanTun
     * 时间：2017/2/22
     * 邮箱：32965926@qq.com
     * 描述：SHA256与SHA512
     */
    public static class SHA {
        /**
         * SHA-256 加密
         *
         * @param strText 传入字符
         * @return 返回 SHA-256 串
         */
        public static String getSHA256 (final String strText)
        {
            return getSHA(strText, "SHA-256");
        }

        /**
         * SHA-512 加密
         *
         * @param strText 传入字符
         * @return 返回 SHA-512 串
         */

        public static String getSHA512(final String strText) {
            return getSHA(strText, "SHA-512");
        }

        /**
         * 字符串 SHA 加密
         *
         * @param strText 传入字符
         * @param strType 加密类型（SHA-256 | SHA-511）
         * @return SHA加密字符串
         */
        private static String getSHA(final String strText, final String strType) {
            // 返回值
            String strResult = null;

            // 是否是有效字符串
            if (strText != null && strText.length() > 0) {
                try {
                    // SHA 加密开始
                    // 创建加密对象 并傳入加密類型
                    MessageDigest messageDigest = MessageDigest.getInstance(strType);
                    // 传入要加密的字符串
                    messageDigest.update(strText.getBytes());
                    // 得到 byte 類型结果
                    byte byteBuffer[] = messageDigest.digest();

                    // 將 byte 轉換爲 string
                    StringBuilder strHexString = new StringBuilder();
                    // 遍歷 byte buffer
                    for (byte item : byteBuffer
                         ) {
                        String hex = Integer.toHexString(0xff & item);
                        if (hex.length() == 1) {
                            strHexString.append('0');
                        }
                        strHexString.append(hex);
                    }
                    // 得到返回結果
                    strResult = strHexString.toString();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }

            return strResult;
        }
    }

    /**
     * 作者：TanTun
     * 时间：2017/06/13
     * 邮箱：32965926@qq.com
     * 描述：AES加解密
     */
    public static class AES {

        private static final String CipherMode = "AES/CBC/PKCS5Padding";
        private static final byte[] Iv = { 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F };

        /**
         * AES 加密
         *
         * @param data 需要加密的数据
         * @param key 密钥（长度必须为16个字节）
         * @return 加密后的数据
         */
        public static byte[] encrypt(byte[] data, byte[] key) {
            try {
                if (key.length < 16) return null;

                SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
                Cipher cipher = Cipher.getInstance(CipherMode);
                cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(Iv));
                return cipher.doFinal(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * AES 解密
         *
         * @param data 需要解密的数据
         * @param key 密钥（长度必须为16个字节）
         * @return 解密后的数据
         */
        public static byte[] decrypt(byte[] data, byte[] key) {
            try {
                if (key.length < 16) return null;

                SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
                Cipher cipher = Cipher.getInstance(CipherMode);
                cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(Iv));
                return cipher.doFinal(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
