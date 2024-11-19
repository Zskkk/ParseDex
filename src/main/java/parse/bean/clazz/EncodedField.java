package parse.bean.clazz;

/**
 * Created by zsk
 * on 2024/11/19 23:35
 */
public class EncodedField {
    public int fieldIdx;
    public int accessFlags;

    public EncodedField(int fieldIdx, int accessFlags){
        this.fieldIdx = fieldIdx;
        this.accessFlags = accessFlags;
    }

    public int getFieldIdx() {
        return fieldIdx;
    }

    public void setFieldIdx(int fieldIdx) {
        this.fieldIdx = fieldIdx;
    }

    public int getAccessFlags() {
        return accessFlags;
    }

    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }
}
