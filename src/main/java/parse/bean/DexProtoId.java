package parse.bean;

/**
 * Created by zsk
 * on 2024/11/19 01:22
 */
public class DexProtoId {
    //    struct DexProtoId {
    //        u4  shortyIdx;          /* index into stringIds for shorty descriptor */
    //        u4  returnTypeIdx;      /* index into typeIds list for return type */
    //        u4  parametersOff;      /* file offset to type_list for parameter types */
    //    };

    public int shortyIdx; // 指向 string_ids
    public int returnTypeIdx; // 指向 types_ids
    public int parametersOff;

    public DexProtoId(int shortyIdx, int returnTypeIdx, int parametersOff) {
        this.shortyIdx = shortyIdx;
        this.returnTypeIdx = returnTypeIdx;
        this.parametersOff = parametersOff;
    }

    @Override
    public String toString() {
        return "DexProtoId{" +
                "shortyIdx=" + shortyIdx +
                ", returnTypeIdx=" + returnTypeIdx +
                ", parametersOff=" + parametersOff +
                '}';
    }
}
