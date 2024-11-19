package parse;

import parse.bean.*;
import utils.Reader;
import utils.TransformUtils;
import utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static utils.Reader.log;

// http://androidxref.com/9.0.0_r3/xref/dalvik/libdex/DexFile.h

public class DexParser {
    private Dex dex;
    private Reader reader;
    private byte[] dexData;
    private List<DexStringId> dexStringIds = new ArrayList<>();
    private List<DexTypeId> dexTypeIds = new ArrayList<>();
    private List<DexProtoId> dexProtos = new ArrayList<>();
    private List<DexFieldId> dexFieldIds = new ArrayList<>();
    private List<DexMethodId> dexMethodIds = new ArrayList<>();

    public DexParser(InputStream in, byte[] dexData) {
        this.dexData = dexData;
        reader = new Reader(in);
        dex = new Dex();
    }

    public void parse() {
        parseHeader();
        parseDexString();
        parseDexType();
        parseDexProto();
        parseDexField();
        parseDexMethod();
        parseDexClass();
    }

    private void parseHeader() {
        DexHeader dexHeader = new DexHeader(reader);
        dexHeader.parse();
        dex.setDexHeader(dexHeader);
    }

    /*
    struct DexStringId {
        u4 stringDataOff;
    };
    */
    private void parseDexString() {
        log("\nparse DexStringId");
        int stringIdsSize = dex.getDexHeader().stringIdsSize;
        for (int i = 0; i < stringIdsSize; i++) {
            try {
                int stringDataOff = reader.readInt();
                byte size = dexData[stringDataOff]; // 第一个字节表示该字符串的长度，之后是字符串内容
                String stringData = new String(Utils.copy(dexData, stringDataOff + 1, size));
                DexStringId dexString = new DexStringId(stringDataOff, stringData);
                dexStringIds.add(dexString);
                log("string[%d] data: %s", i, dexString.stringData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    struct DexTypeId {
        u4  descriptorIdx;
    };
    */
    private void parseDexType() {
        log("\nparse DexTypeId");
        try {
            int typeIdsSize = dex.getDexHeader().typeIdsSize;
            for (int i = 0; i < typeIdsSize; i++) {
                int descriptorIdx = reader.readInt();
                DexTypeId dexTypeId = new DexTypeId(descriptorIdx, dexStringIds.get(descriptorIdx).stringData);
                dexTypeIds.add(dexTypeId);
                log("type[%d] data: %s", i, dexTypeId.stringData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*
    struct DexProtoId {
        u4  shortyIdx;          // 指向 string_ids ，表示方法声明的字符串
        u4  returnTypeIdx;      // 指向 type_ids ，表示方法的返回类型
        u4  parametersOff;      // 方法参数列表的偏移量，参数类型的type_list文件偏移量
    };
    struct DexTypeList {
        u4  size;
        DexTypeItem list[1];
    };
    struct DexTypeItem {
        u2  typeIdx;            // index into typeIds
    };
    */
    private void parseDexProto() {
        log("\nparse DexProto");
        try {
            int protoIdsSize = dex.getDexHeader().protoIdsSize;
            for (int i = 0; i < protoIdsSize; i++) {
                int shortyIdx = reader.readInt();
                int returnTypeIdx = reader.readInt();
                int parametersOff = reader.readInt();

                DexProtoId dexProtoId = new DexProtoId(shortyIdx, returnTypeIdx, parametersOff);
                String signature = "";
                if (parametersOff > 0) {
                    signature = parseDexProtoParameters(parametersOff);
                }
                log("proto[%d]: %s %s %s", i, dexStringIds.get(shortyIdx).stringData,
                        dexTypeIds.get(returnTypeIdx).stringData, signature);
                dexProtos.add(dexProtoId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parseDexProtoParameters(int parametersOff) {
        StringBuilder signature = new StringBuilder();
        int paramSize = TransformUtils.bytes2Int(Utils.copy(dexData, parametersOff, 4)); // 参数长度
        for (int i = 0; i < paramSize; i++) {
            int typeIdx = TransformUtils.bytes2UnsignedShort(Utils.copy(dexData, parametersOff + i * 2 + 4, 2));  // 跳过 size 的 4 字节, 每个参数的类型索引是 u2（2 字节）
            //log("parameters[%d]: %s", i, dexTypeIds.get(typeIdx).stringData);
            signature.append(dexTypeIds.get(typeIdx).stringData);  // 参数类型
        }
        return signature.toString();
    }

    /*
    struct DexFieldId {
        u2  classIdx;           // index into typeIds list for defining class
        u2  typeIdx;            // index into typeIds for field type
        u4  nameIdx;            // index into stringIds for field name
    };
    */
    private void parseDexField() {
        log("\nparse DexField");
        try {
            int fieldIdsSize = dex.getDexHeader().fieldIdsSize;
            for (int i = 0; i < fieldIdsSize; i++) {
                int classIdx = reader.readUnsignedShort();
                int typeIdx = reader.readUnsignedShort();
                int nameIdx = reader.readInt();
                DexFieldId dexFieldId = new DexFieldId(classIdx, typeIdx, nameIdx);

                log("field[%d]: %s->%s;%s", i, dexTypeIds.get(classIdx).stringData,
                        dexStringIds.get(nameIdx).stringData, dexTypeIds.get(typeIdx).stringData);
                dexFieldIds.add(dexFieldId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    struct DexMethodId {
        u2  classIdx;           // index into typeIds list for defining class
        u2  protoIdx;           // index into protoIds for method prototype
        u4  nameIdx;            // index into stringIds for method name
    };
    */
    private void parseDexMethod() {
        // "java/util/HashMap->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"
        log("\nparse DexMethod");
        try {
            int methodIdsSize = dex.getDexHeader().methodIdsSize;
            for (int i = 0; i < methodIdsSize; i++) {
                int classIdx = reader.readUnsignedShort();
                int protoIdx = reader.readUnsignedShort();
                int nameIdx = reader.readInt();
                DexMethodId dexMethodId = new DexMethodId(classIdx, protoIdx, nameIdx);

                int parametersOff = dexProtos.get(protoIdx).parametersOff;
                String signature = "";
                if (parametersOff > 0) {
                    signature = parseDexProtoParameters(parametersOff);
                }

                int returnTypeIdx = dexProtos.get(protoIdx).returnTypeIdx;
                String returnType = dexTypeIds.get(returnTypeIdx).stringData;

                log("method[%d]: %s->%s(%s)%s",
                        i,
                        dexTypeIds.get(classIdx).stringData, // 类名
                        dexStringIds.get(nameIdx).stringData, // 方法名
                        signature, // 参数列表
                        returnType // 返回值
                );
                dexMethodIds.add(dexMethodId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    360struct DexClassDef {
        u4  classIdx;           // index into typeIds for this class
        u4  accessFlags;
        u4  superclassIdx;      // index into typeIds for superclass
        u4  interfacesOff;      // file offset to DexTypeList
        u4  sourceFileIdx;      // index into stringIds for source file name
        u4  annotationsOff;     // file offset to annotations_directory_item
        u4  classDataOff;       // file offset to class_data_item
        u4  staticValuesOff;    // file offset to DexEncodedArray
    };
    * */
    private void parseDexClass() {
        log("\nparse DexClass");

    }
}
