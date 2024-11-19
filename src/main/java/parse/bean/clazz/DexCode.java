package parse.bean.clazz;

import java.util.Arrays;

/**
 * Created by zsk
 * on 2024/11/20 01:20
 */
public class DexCode {
    public int registersSize;
    public int insSize;
    public int outsSize;
    public int triesSize;
    public int debugInfoOff;
    public int insnsSize;
    public String[] insns;

    public DexCode(int registersSize, int insSize, int outsSize, int triesSize, int debugInfoOff, int insnsSize, int[] insns) {
        this.registersSize = registersSize;
        this.insSize = insSize;
        this.outsSize = outsSize;
        this.triesSize = triesSize;
        this.debugInfoOff = debugInfoOff;
        this.insnsSize = insnsSize;
        this.insns = transform(insns);
    }

    private String[] transform(int[] insns) {
        String[] result = new String[insns.length];
        for (int i = 0; i < insns.length; i++) {
            result[i] = "0x"+Integer.toHexString(insns[i]);
        }
        return result;
    }

    public static int[] transformToInt(String[] hexStrings) {
        int[] result = new int[hexStrings.length];
        for (int i = 0; i < hexStrings.length; i++) {
            result[i] = Integer.parseInt(hexStrings[i].replace("0x", ""), 16);
        }
        return result;
    }

    @Override
    public String toString() {
        return "DexCode{" +
                "registersSize=" + registersSize +
                ", insSize=" + insSize +
                ", outsSize=" + outsSize +
                ", triesSize=" + triesSize +
                ", debugInfoOff=" + debugInfoOff +
                ", insnsSize=" + insnsSize +
                ", insns=" + Arrays.toString(insns) +
                '}';
    }
}
