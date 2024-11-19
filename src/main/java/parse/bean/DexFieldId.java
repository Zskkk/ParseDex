package parse.bean;

/**
 * Created by zsk
 * on 2024/11/19 01:48
 */
public class DexFieldId {
    /*
    struct DexFieldId {
        u2  classIdx;           // index into typeIds list for defining class
        u2  typeIdx;            // index into typeIds for field type
        u4  nameIdx;            // index into stringIds for field name
    };
    */

    public int classIdx; // 字段所属类，指向 typeIds
    public int typeIdx; // 字段的数据类型，指向 typeIds
    public int nameIdx; // 字段的名称，指向 stringIds

    public DexFieldId(int classIdx, int typeIdx, int nameIdx) {
        this.classIdx = classIdx;
        this.typeIdx = typeIdx;
        this.nameIdx = nameIdx;
    }

    @Override
    public String toString() {
        return "DexFieldId{" +
                "classIdx=" + classIdx +
                ", typeIdx=" + typeIdx +
                ", nameIdx=" + nameIdx +
                '}';
    }
}
