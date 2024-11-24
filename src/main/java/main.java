import elfparse.ElfParser;
import parse.DexParser;
import utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by zsk
 * on 2024/11/17 22:13
 */

public class main {
    public static void parseDex(){
        File file = new File("src/main/resources/Hello.dex");
        try {
            DexParser dexParser = new DexParser(new FileInputStream(file), Utils.readAll(file));
            dexParser.parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void parseElf(){
        File file = new File("src/main/resources/libhello-jni.so");
        ElfParser elfParser = new ElfParser(Utils.readAll(file));
        elfParser.parse();
    }

    public static void main(String[] args) {
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        parseDex();
        parseElf();
    }
}
