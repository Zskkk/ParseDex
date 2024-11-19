package utils;

import java.io.*;

/**
 * Created by zsk
 * on 2024/11/18 02:06
 */
public class Utils {

    public static byte[] copy(byte[] stringContent, int start, int length) {
        byte[] b = new byte[length];
        System.arraycopy(stringContent, start, b, 0, length);
        return b;
    }

    public static byte[] readAll(File file) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            FileInputStream in = new FileInputStream(file);
            BufferedInputStream bi = new BufferedInputStream(in);
            byte[] b = new byte[1024];
            int len = 0;
            while ((len = bi.read(b)) != -1) {
                bos.write(b, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
