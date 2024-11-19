package utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import static utils.TransformUtils.reverseBytes;

/**
 * Created by zsk
 * on 2024/11/17 21:35
 */
public class Reader {
    private volatile InputStream in;
    private static boolean showLog = true;
    private boolean isLittleEndian = true;

    public Reader(InputStream in) {
        this(in, true);
    }

    public Reader(InputStream in, boolean isLittleEndian) {
        this.in = in;
        this.isLittleEndian = isLittleEndian;
    }

    /**
     * @param count 字节数
     * @return 返回 little endian 字节数组
     * @throws IOException
     */
    public byte[] read(int count) throws IOException {
        byte[] b = new byte[count];
        int read = in.read(b);
        if (read == -1) throw new EOFException();
        if (isLittleEndian) return b;
        else {
            return reverseBytes(b);
        }
    }

    public byte[] readOrigin(int count) throws IOException {
        byte[] b = new byte[count];
        int read = in.read(b);
        if (read == -1) throw new EOFException();
        else return b;
    }

    public int readUnsignedShort() throws IOException {
        return TransformUtils.bytes2UnsignedShort(read(2));
    }

    public int readInt() throws IOException {
        byte[] ints = read(4);
        return TransformUtils.bytes2Int(ints);
    }

    public long readUnsignedInt() throws IOException {
        byte[] ints = read(4);
        return TransformUtils.bytes2UnsignedInt(ints);
    }


    public static void log(String format, Object... params) {
        if (showLog) {
            System.out.printf(format, params);
        }
        System.out.println();
    }
}
