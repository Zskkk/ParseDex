package parse.bean.clazz;

/**
 * Created by zsk
 * on 2024/11/19 23:28
 */
public class DexClassData {
    public int staticFieldsSize;
    public int instanceFieldsSize;
    public int directMethodsSize;
    public int virtualMethodsSize;

    public DexClassData(int staticFieldsSize, int instanceFieldsSize, int directMethodsSize, int virtualMethodsSize) {
        this.staticFieldsSize = staticFieldsSize;
        this.instanceFieldsSize = instanceFieldsSize;
        this.directMethodsSize = directMethodsSize;
        this.virtualMethodsSize = virtualMethodsSize;
    }

    @Override
    public String toString() {
        return "DexClassData{" +
                "staticFieldsSize=" + staticFieldsSize +
                ", instanceFieldsSize=" + instanceFieldsSize +
                ", directMethodsSize=" + directMethodsSize +
                ", virtualMethodsSize=" + virtualMethodsSize +
                '}';
    }
}
