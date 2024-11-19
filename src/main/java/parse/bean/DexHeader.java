package parse.bean;

import utils.Reader;
import utils.TransformUtils;

import java.io.IOException;

import static utils.Reader.log;

/**
 * Created by zsk
 * on 2024/11/17 21:39
 */
public class DexHeader {
    /*
       u1: unsigned 1 byte
       u2: unsigned 2 byte
       u4: unsigned 4 byte
    */
    /*
    struct DexHeader {
        u1  magic[8];
        u4  checksum;
        u1  signature[kSHA1DigestLen];
        u4  fileSize;
        u4  headerSize;
        u4  endianTag;
        u4  linkSize;
        u4  linkOff;
        u4  mapOff;
        u4  stringIdsSize;
        u4  stringIdsOff;
        u4  typeIdsSize;
        u4  typeIdsOff;
        u4  protoIdsSize;
        u4  protoIdsOff;
        u4  fieldIdsSize;
        u4  fieldIdsOff;
        u4  methodIdsSize;
        u4  methodIdsOff;
        u4  classDefsSize;
        u4  classDefsOff;
        u4  dataSize;
        u4  dataOff;
    };
    */

    private Reader reader;
    public String magic;
    public long checkSum;
    public String signature;
    public int fileSize;
    public int headerSize;
    public int endianTag;
    public int linkSize;
    public int linkOff;
    public int mapOff;
    public int stringIdsSize;
    public int stringIdsOff;
    public int typeIdsSize;
    public int typeIdsOff;
    public int protoIdsSize;
    public int protoIdsOff;
    public int fieldIdsSize;
    public int fieldIdsOff;
    public int methodIdsSize;
    public int methodIdsOff;
    public int classDefsSize;
    public int classDefsOff;
    public int dataSize;
    public int dataOff;

    public DexHeader(Reader reader) {
        this.reader = reader;
    }

    public void parse() {
        try {
            this.magic = TransformUtils.bytes2String(reader.readOrigin(8));
            log("magic: %s", magic);

            this.checkSum = reader.readUnsignedInt();
            log("checkSum: %d", checkSum);

            this.signature = TransformUtils.byte2HexStr(reader.readOrigin(20));
            log("signature: %s", signature);

            this.fileSize = reader.readInt();
            log("fileSize: %d", fileSize);

            this.headerSize = reader.readInt();
            log("headerSize: %d", headerSize);

            this.endianTag = reader.readInt();
            log("endianTag: 0x%x", endianTag);

            this.linkSize = reader.readInt();
            log("linkSize: %d", linkSize);

            this.linkOff = reader.readInt();
            log("linkOff: %d", linkOff);

            this.mapOff = reader.readInt();
            log("mapOff: %d", mapOff);

            this.stringIdsSize = reader.readInt();
            log("stringIdsSize: %d", stringIdsSize);

            this.stringIdsOff = reader.readInt();
            log("stringIdsOff: %d", stringIdsOff);

            this.typeIdsSize = reader.readInt();
            log("typeIdsSize: %d", typeIdsSize);

            this.typeIdsOff = reader.readInt();
            log("typeIdsOff: %d", typeIdsOff);

            this.protoIdsSize = reader.readInt();
            log("protoIdsSize: %d", protoIdsSize);

            this.protoIdsOff = reader.readInt();
            log("protoIdsOff: %d", protoIdsOff);

            this.fieldIdsSize = reader.readInt();
            log("fieldIdsSize: %d", fieldIdsSize);

            this.fieldIdsOff = reader.readInt();
            log("fieldIdsOff: %d", fieldIdsOff);

            this.methodIdsSize = reader.readInt();
            log("methodIdsSize: %d", methodIdsSize);

            this.methodIdsOff = reader.readInt();
            log("methodIdsOff: %d", methodIdsOff);

            this.classDefsSize = reader.readInt();
            log("classDefsSize: %d", classDefsSize);

            this.classDefsOff = reader.readInt();
            log("classDefsOff: %d", classDefsOff);

            this.dataSize = reader.readInt();
            log("dataSize: %d", dataSize);

            this.dataOff = reader.readInt();
            log("dataOff: %d", dataOff);
        } catch (IOException e) {
            e.printStackTrace();
            log("parse dex header error!");
        }
    }
}
