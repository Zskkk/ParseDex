package utils;

import parse.DexParser;

import java.io.*;

/**
 * Created by zsk
 * on 2024/11/18 02:06
 */
public class Utils {
    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    public static byte[] copy(byte[] stringContent, int start, int length) {
        byte[] b = new byte[length];
        System.arraycopy(stringContent, start, b, 0, length);
        return b;
    }

    // 将 4 个字节解析为 int
    public static int fromBytes(byte b1, byte b2, byte b3, byte b4) {
        return b1 << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | (b4 & 0xFF);
    }

    // 将 2 个字节解析为 int（适用于 short）
    public static int fromBytes(byte b1, byte b2) {
        return b1 & 0xff | (b2 & 0xFF) << 8;
    }

    public static String byteArrayToHex(byte[] bytes) {
        return byteArrayToHex(bytes, true);
    }

    public static String byteArrayToHex(byte[] bytes, Boolean space) {
        StringBuilder r = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            r.append(hexCode[(b >> 4) & 0xF]); // 高位
            r.append(hexCode[b & 0xF]);       // 低位
            if (space && i < bytes.length - 1) { // 最后一个字节后不追加空格
                r.append(" ");
            }
        }
        return r.toString();
    }

    /**
     * 反序输出hex值，方面elf解析查看
    */
    public static String byteToHexReverse(byte[] bytes){
        StringBuilder r = new StringBuilder(bytes.length * 3);

        for (int i = bytes.length - 1; i >= 0; i--) {
            byte b = bytes[i];
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[b & 0xF]);
            if (i > 0) {
                r.append(" ");
            }
        }
        return r.toString();
    }

    public static int byte2Short(byte[] bytes) {
        return fromBytes(bytes[0], bytes[1]);
    }

    public static int byte2Int(byte[] bytes) {
        return fromBytes(bytes[3], bytes[2], bytes[1], bytes[0]);
    }

    public static byte[] readAll(File file) {
        try (FileInputStream in = new FileInputStream(file);
             BufferedInputStream bi = new BufferedInputStream(in);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] b = new byte[1024];
            int len;
            while ((len = bi.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int readUnsignedLeb128(byte[] src, int offset) {
        int result = 0;
        int count = 0;
        int cur;
        do {
            cur = src[offset] & 0xff;  // 将字节值转为0～255无符号整数
            result |= (cur & 0x7f) << count * 7;  // 提取当前字节的低7位数据 (cur & 0x7f)，并根据当前字节序号（count）将其左移 count * 7 位，拼接到 result 中。
            count++;
            offset++;
            DexParser.POSITION++;
        } while ((cur & 0x80) == 128 && count < 5);
        return result;
    }
}
