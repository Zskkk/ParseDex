package parse.bean;

/**
 * Created by zsk
 * on 2024/11/19 00:55
 */
public class DexTypeId {
    /*
    struct DexTypeId {
        u4  descriptorIdx;  // index into stringIds list for type descriptor
    };
    */
    public int descriptorIdx; // 指向 string_ids 中的内容
    public String stringData;

    public DexTypeId(int descriptorIdx, String stringData) {
        this.descriptorIdx = descriptorIdx;
        this.stringData = stringData;
    }

    @Override
    public String toString() {
        return "DexTypeId{" +
                "descriptorIdx=" + descriptorIdx +
                ", stringData='" + stringData + '\'' +
                '}';
    }
}
