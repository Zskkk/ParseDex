package parse.bean;

/**
 * Created by zsk
 * on 2024/11/19 02:03
 */
public class DexMethodId {
    /*
    struct DexMethodId {
        u2  classIdx;           // index into typeIds list for defining class
        u2  protoIdx;           // index into protoIds for method prototype
        u4  nameIdx;            // index into stringIds for method name
    };
    */

    public int classIdx; // 指向 typeIds, 获取方法所属类的类型描述
    public int protoIdx; // 指向 protoIds, 获取方法的签名，包括返回类型和参数列表
    public int nameIdx; // 指向 stringIds, 获取方法名

    public DexMethodId(int classIdx, int protoIdx, int nameIdx) {
        this.classIdx = classIdx;
        this.protoIdx = protoIdx;
        this.nameIdx = nameIdx;
    }

    @Override
    public String toString() {
        return "DexMethodId{" +
                "classIdx=" + classIdx +
                ", protoIdx=" + protoIdx +
                ", nameIdx=" + nameIdx +
                '}';
    }

}
