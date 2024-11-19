package parse.bean.clazz;

/**
 * Created by zsk
 * on 2024/11/19 22:12
 */

public class DexClass {
    //    struct DexClassDef {
    //        u4  classIdx;           /* index into typeIds for this class */
    //        u4  accessFlags;
    //        u4  superclassIdx;      /* index into typeIds for superclass */
    //        u4  interfacesOff;      /* file offset to DexTypeList */
    //        u4  sourceFileIdx;      /* index into stringIds for source file name */
    //        u4  annotationsOff;     /* file offset to annotations_directory_item */
    //        u4  classDataOff;       /* file offset to class_data_item */
    //        u4  staticValuesOff;    /* file offset to DexEncodedArray */
    //    };
    public int classIdx; // 指向 type_ids
    public int accessFlags;
    public int superclassIdx; // 指向 type_ids
    public int interfacesOff; // 指向 type_ids
    public int sourceFileIdx; // 指向 string_ids
    public int annotationsOff;
    public int classDataOff; // 指向 data 区
    public int staticValuesOff;

    public DexClass(int classIdx, int accessFlags, int superclassIdx, int interfacesOff, int sourceFileIdx, int annotationsOff, int classDataOff, int staticValuesOff) {
        this.classIdx = classIdx;
        this.accessFlags = accessFlags;
        this.superclassIdx = superclassIdx;
        this.interfacesOff = interfacesOff;
        this.sourceFileIdx = sourceFileIdx;
        this.annotationsOff = annotationsOff;
        this.classDataOff = classDataOff;
        this.staticValuesOff = staticValuesOff;
    }

    @Override
    public String toString() {
        return "DexClass{" +
                "classIdx=" + classIdx +
                ", accessFlags=" + accessFlags +
                ", superclassIdx=" + superclassIdx +
                ", interfacesOff=" + interfacesOff +
                ", sourceFileIdx=" + sourceFileIdx +
                ", annotationsOff=" + annotationsOff +
                ", classDataOff=" + classDataOff +
                ", staticValuesOff=" + staticValuesOff +
                '}';
    }
}
