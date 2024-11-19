package parse;

import parse.bean.*;
import parse.bean.clazz.*;
import utils.Reader;
import utils.TransformUtils;
import utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static utils.Reader.log;
import static utils.Utils.byteArrayToHex;

// http://androidxref.com/9.0.0_r3/xref/dalvik/libdex/DexFile.h

public class DexParser {
    public static int POSITION = 0;
    private Dex dex;
    private Reader reader;
    private byte[] dexData;
    private List<DexStringId> dexStringIds = new ArrayList<>();
    private List<DexTypeId> dexTypeIds = new ArrayList<>();
    private List<DexProtoId> dexProtos = new ArrayList<>();
    private List<DexFieldId> dexFieldIds = new ArrayList<>();
    private List<DexMethodId> dexMethodIds = new ArrayList<>();
    private List<DexClass> dexClasses = new ArrayList<>();

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
    struct DexClassDef {
        u4  classIdx;           // 表示当前类在 typeIds 表中的索引
        u4  accessFlags;        // 表示类的访问权限修饰符, 0x0001: public, 0x0010: final, 0x0200: interface, 0x0400: abstract等
        u4  superclassIdx;      // 表示父类在 typeIds 表中的索引。
        u4  interfacesOff;      // file offset to DexTypeList
        u4  sourceFileIdx;      // 表示该类的源文件名在 stringIds 表中的索引。
        u4  annotationsOff;     // file offset to annotations_directory_item
        u4  classDataOff;       // file offset to class_data_item
        u4  staticValuesOff;    // 表示类的静态字段的默认值的文件偏移量。
    };
    struct DexClassData {
        DexClassDataHeader header;
        DexField*          staticFields;
        DexField*          instanceFields;
        DexMethod*         directMethods;
        DexMethod*         virtualMethods;
    };
    struct DexClassDataHeader {
        u4 staticFieldsSize;  // 静态字段个数
        u4 instanceFieldsSize;  // 实例字段个数
        u4 directMethodsSize;  // 直接方法个数
        u4 virtualMethodsSize; // 虚方法个数
    };
    struct DexField {
        u4 fieldIdx;    // index to a field_id_item
        u4 accessFlags;
    };
    struct DexMethod {
        u4 methodIdx;    // index to a method_id_item
        u4 accessFlags;
        u4 codeOff;      // file offset to a code_item
    };
    */
    private void parseDexClass() {
        log("\nparse DexClass");
        try {
            int classDefsSize = dex.getDexHeader().classDefsSize;
            for (int i = 0; i < classDefsSize; i++) {
                int classIdx = reader.readInt();
                int accessFlags = reader.readInt();
                int superclassIdx = reader.readInt();
                int interfacesOff = reader.readInt();
                int sourceFileIdx = reader.readInt();
                int annotationsOff = reader.readInt();
                int classDataOff = reader.readInt();
                int staticValuesOff = reader.readInt();

                DexClass dexClass = new DexClass(classIdx, accessFlags, superclassIdx,
                        interfacesOff, sourceFileIdx, annotationsOff, classDataOff, staticValuesOff);
                log("class[%d]: %s", i, dexClass.toString());
                dexClasses.add(dexClass);

                String accessFlagsDescription = parseAccessFlags(accessFlags);
                log("   classIdx: %s", dexTypeIds.get(classIdx).stringData);
                log("   accessFlags: %s", accessFlagsDescription);
                log("   superclassIdx: %s", dexTypeIds.get(superclassIdx).stringData);
                log("   interfacesOff: %s", dexTypeIds.get(interfacesOff).stringData);

                if (sourceFileIdx != -1) {
                    log("   sourceFileIdx: %s", dexStringIds.get(sourceFileIdx).stringData);
                }
                if (annotationsOff > 0) {
                    parseAnnotationsDirectoryItem(annotationsOff);
                }
                if (staticValuesOff > 0) {
                    parseStaticValues(staticValuesOff);
                }
                parseClassData(classDataOff);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String parseAccessFlags(int accessFlags) {
        StringBuilder sb = new StringBuilder();

        if ((accessFlags & 0x0001) != 0) sb.append("public ");
        if ((accessFlags & 0x0002) != 0) sb.append("private ");
        if ((accessFlags & 0x0004) != 0) sb.append("protected ");
        if ((accessFlags & 0x0008) != 0) sb.append("static ");
        if ((accessFlags & 0x0010) != 0) sb.append("final ");
        if ((accessFlags & 0x0020) != 0) sb.append("synchronized ");
        if ((accessFlags & 0x0040) != 0) sb.append("volatile ");
        if ((accessFlags & 0x0080) != 0) sb.append("transient ");
        if ((accessFlags & 0x0100) != 0) sb.append("native ");
        if ((accessFlags & 0x0200) != 0) sb.append("interface ");
        if ((accessFlags & 0x0400) != 0) sb.append("abstract ");
        if ((accessFlags & 0x0800) != 0) sb.append("strictfp ");
        if ((accessFlags & 0x1000) != 0) sb.append("synthetic ");
        if ((accessFlags & 0x2000) != 0) sb.append("annotation ");
        if ((accessFlags & 0x4000) != 0) sb.append("enum ");
        if ((accessFlags & 0x10000) != 0) sb.append("constructor ");
        if ((accessFlags & 0x20000) != 0) sb.append("declared_synchronized ");

        return sb.toString().trim();
    }

    private void parseAnnotationsDirectoryItem(int annotationsOff) {
    }

    /*
    struct DexEncodedArray {
        u1  array[1];                   // data in encoded_array format
    };
    */
    private void parseStaticValues(int staticValuesOff) {
    }

    private void parseClassData(int class_data_off) {
        POSITION = class_data_off;
        int staticFieldsSize = Utils.readUnsignedLeb128(dexData, POSITION);
        int instanceFieldsSize = Utils.readUnsignedLeb128(dexData, POSITION);
        int directMethodsSize = Utils.readUnsignedLeb128(dexData, POSITION);
        int virtualMethodsSize = Utils.readUnsignedLeb128(dexData, POSITION);

        DexClassData dexClassData = new DexClassData(staticFieldsSize, instanceFieldsSize, directMethodsSize, virtualMethodsSize);
        log("   classData: %s", dexClassData.toString());

        // static field
        for (int i = 0; i < staticFieldsSize; i++) {
            int fieldIdx = Utils.readUnsignedLeb128(dexData, POSITION);
            int accessFlags = Utils.readUnsignedLeb128(dexData, POSITION);
            EncodedField encodedField = new EncodedField(fieldIdx, accessFlags);
            DexFieldId dexFieldId = dexFieldIds.get(fieldIdx);
            log("   static field[%d]: %s->%s;%s", i, dexTypeIds.get(dexFieldId.classIdx).stringData,
                    dexStringIds.get(dexFieldId.nameIdx).stringData, dexTypeIds.get(dexFieldId.typeIdx).stringData);
        }

        // instance field
        for (int i = 0; i < instanceFieldsSize; i++) {
            int fieldIdx = Utils.readUnsignedLeb128(dexData, POSITION);
            int accessFlags = Utils.readUnsignedLeb128(dexData, POSITION);
            EncodedField encodedField = new EncodedField(fieldIdx, accessFlags);
            DexFieldId dexFieldId = dexFieldIds.get(fieldIdx);
            log("   instance field[%d]: %s->%s;%s", i, dexTypeIds.get(dexFieldId.classIdx).stringData,
                    dexStringIds.get(dexFieldId.nameIdx).stringData, dexTypeIds.get(dexFieldId.typeIdx).stringData);
        }

        // direct method
        for (int i = 0; i < directMethodsSize; i++) {
            int methodIdx = Utils.readUnsignedLeb128(dexData, POSITION);
            int accessFlags = Utils.readUnsignedLeb128(dexData, POSITION);
            int codeOff = Utils.readUnsignedLeb128(dexData, POSITION);
            EncodedMethod encodedMethod = new EncodedMethod(methodIdx, accessFlags, codeOff);
            DexMethodId dexMethodId = dexMethodIds.get(methodIdx);

            // 函数的参数，返回值
            int parametersOff = dexProtos.get(dexMethodId.protoIdx).parametersOff;
            String signature = "";
            if (parametersOff > 0) {
                signature = parseDexProtoParameters(parametersOff);
            }
            int returnTypeIdx = dexProtos.get(dexMethodId.protoIdx).returnTypeIdx;
            String returnType = dexTypeIds.get(returnTypeIdx).stringData;

            log("   direct method[%d]: %s->%s(%s)%s",
                    i,
                    dexTypeIds.get(dexMethodId.classIdx).stringData,  // 类名
                    dexStringIds.get(dexMethodId.nameIdx).stringData,  // 方法名
                    signature,  // 参数列表
                    returnType); // 返回值

            parseDexCode(codeOff);
        }
    }

    //struct DexCode {
    //    u2  registersSize;  // 寄存器个数
    //    u2  insSize;        // 参数的个数
    //    u2  outsSize;       // 调用其他方法时使用的寄存器个数
    //    u2  triesSize;      // try/catch 语句个数
    //    u4  debugInfoOff;   // debug 信息的偏移量
    //    u4  insnsSize;      // 指令集的个数
    //    u2  insns[1];       // 指令集, 每个指令占 2 字节 (u2)。
    //    /* followed by optional u2 padding */  // 2 字节，用于对齐
    //    /* followed by try_item[triesSize] */
    //    /* followed by uleb128 handlersSize */
    //    /* followed by catch_handler_item[handlersSize] */
    //};
    private void parseDexCode(int code_off) {
        int registersSize = TransformUtils.bytes2UnsignedShort(Utils.copy(dexData, code_off, 2));
        int insSize = TransformUtils.bytes2UnsignedShort(Utils.copy(dexData, code_off + 2, 2));
        int outsSize = TransformUtils.bytes2UnsignedShort(Utils.copy(dexData, code_off + 4, 2));
        int triesSize = TransformUtils.bytes2UnsignedShort(Utils.copy(dexData, code_off + 6, 2));
        int debugInfoOff = TransformUtils.bytes2Int(Utils.copy(dexData, code_off + 8, 4));
        int insnsSize = TransformUtils.bytes2Int(Utils.copy(dexData, code_off + 12, 4));
        int[] insns = new int[insnsSize];
        for (int i = 0; i < insnsSize; i++) {
            int insns_ = TransformUtils.bytes2UnsignedShort(Utils.copy(dexData, code_off + 16 + i * 2, 2));
            insns[i] = insns_;
        }
        DexCode dexCode = new DexCode(registersSize, insSize, outsSize, triesSize, debugInfoOff, insnsSize, insns);
        log("   dexcode: %s", dexCode.toString());

        byte[] byteArray = dexCodeToByteArray(dexCode);
        System.out.println(byteArrayToHex(byteArray));
        // 字节码 03 00 01 00 02 00 00 00 79 02 00 00 08 00 00 00
        //       62 00 01 00 62 01 00 00 6e 20 03 00 10 00 0e 00
        //  62 00 01 00 : sget-object v0, Ljava/lang/System;->out:Ljava/io/PrintStream;
        //  62 01 00 00 : sget-object v1, LHello;->HELLO_WORLD:Ljava/lang/String;
        //  6E 20 03 00 : invoke-virtual {v0, v1}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V
        //  OE OO : return-void

    }

    public static byte[] dexCodeToByteArray(DexCode dexCode) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            output.write(toLittleEndianBytes(dexCode.registersSize, 2));
            output.write(toLittleEndianBytes(dexCode.insSize, 2));
            output.write(toLittleEndianBytes(dexCode.outsSize, 2));
            output.write(toLittleEndianBytes(dexCode.triesSize, 2));
            output.write(toLittleEndianBytes(dexCode.debugInfoOff, 4));
            output.write(toLittleEndianBytes(dexCode.insnsSize, 4));
            for (int insn : DexCode.transformToInt(dexCode.insns)) {
                output.write(toLittleEndianBytes(insn, 2)); // 每条指令占 2 字节
            }
            // 字节对齐 (4 字节对齐)
            while (output.size() % 4 != 0) {
                output.write(0x00);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error converting DexCode to byte array", e);
        }
        return output.toByteArray();
    }

    private static byte[] toLittleEndianBytes(int value, int size) {
        byte[] result = new byte[size];
        for (int i = 0; i < size; i++) {
            result[i] = (byte) ((value >> (i * 8)) & 0xFF); // 小端模式
        }
        return result;
    }
}
