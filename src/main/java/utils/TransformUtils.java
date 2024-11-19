package utils;

import java.nio.charset.Charset;

/**
 * Created by zsk
 * on 2024/11/17 22:52
 */
public class TransformUtils {

    public static String bytes2String(byte[] bytes){
        return new String(bytes, Charset.forName("utf-8"));
    }

    /**
     * little endian
     * byte[] 转 int
     * this is for little endian
     */
    public static int bytes2Int(byte[] bytes) {
        if (null == bytes || bytes.length == 0) return 0;
        return (bytes[3] & 0XFF) << 24
                | (bytes[2] & 0xFF) << 16
                | (bytes[1] & 0xFF) << 8
                | bytes[0] & 0xFF;
    }

    public static long bytes2UnsignedInt(byte[] bytes) {
        if (null == bytes || bytes.length == 0) return 0;
        return (long)(bytes[3] & 0XFF) << 24
                | (bytes[2] & 0xFF) << 16
                | (bytes[1] & 0xFF) << 8
                | bytes[0] & 0xFF;
    }

    /**
     * byte[] 转 unsigned short
     */
    public static int bytes2UnsignedShort(byte[] bytes) {
        if (null == bytes || bytes.length == 0) return 0;
        return ((bytes[0] & 0xff) |
                ((bytes[1] & 0xff)) << 8);
    }

    /**
     * byte[] 转 16进制字符串
     */
    public static String byte2HexStr(byte[] bytes) {
        if (null == bytes || bytes.length == 0) return "";
        StringBuilder sb = new StringBuilder("0x");
        for (byte b: bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 用 int 数组表示无符号 byte 数组
     */
    public static int[] bytes2Ints(byte[] b) {
        int[] ints = new int[b.length];
        for (int i = 0; i < b.length; i++) {
            ints[i] = b[i] & 0xFF;
        }
        return ints;
    }

    /**
     * 反转字符串
     */
    public static byte[] reverseBytes(byte[] bytes) {
        int length = bytes.length;
        byte[] result = new byte[length];
        for (int i = 0; i < length / 2; i++) {
            result[i] = bytes[length - i - 1];
            result[length - i - 1] = bytes[i];
        }
        return result;
    }
}
