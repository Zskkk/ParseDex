package parse.bean;

/**
 * Created by zsk
 * on 2024/11/19 00:45
 */
public class DexStringId {
    /*
    struct DexStringId {
        u4 stringDataOff;
    };
    */

    public int stringDataOff;
    public String stringData;

    public DexStringId(int stringDataOff, String stringData) {
        this.stringDataOff = stringDataOff;
        this.stringData = stringData;
    }

    @Override
    public String toString() {
        return "DexStringId{" +
                "stringDataOff=" + stringDataOff +
                ", stringData=" + stringData +
                '}';
    }
}
