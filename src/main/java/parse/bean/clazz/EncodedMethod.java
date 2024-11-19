package parse.bean.clazz;

/**
 * Created by zsk
 * on 2024/11/19 23:35
 */
public class EncodedMethod {
    public int fieldIdx;
    public int accessFlags;
    public int codeOff;

    public EncodedMethod(int fieldIdx, int accessFlags, int codeOff){
        this.fieldIdx = fieldIdx;
        this.accessFlags = accessFlags;
        this.codeOff = codeOff;
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

    public int getCodeOff() {
        return codeOff;
    }

    public void setCodeOff(int codeOff) {
        this.codeOff = codeOff;
    }
}
