package elfparse;

import utils.Utils;

/**
 * Created by zsk
 * on 2024/11/24 01:06
 */
public class ElfParser {
    private byte[] elfData;

    public ElfParser(byte[] elfData) {
        this.elfData = elfData;
    }

    public static ElfType32 type_32 = new ElfType32();

    public void parse() {
        /**
         * 链接视图 执行视图
         * ELF头部 ELF头部
         * 程序头部表（可选） 程序头部表
         * 节区 1 段 1
         * ...
         * 节区 n 段 2
         * ...
         * ... ...
         * 节区头部表 节区头部表（可选）
         *
         * 文件开始处是一个 ELF 头部（ELF Header），用来描述整个文件的组织。节区部分包含链接视图的大量信息：指令、数据、符号表、重定位信息等等。
         * 程序头部表（Program Header Table），如果存在的话，告诉系统如何创建进程映像。用来构造进程映像的目标文件必须具有程序头部表，可重定位文件不需要这个表。
         * 节区头部表（Section Heade Table）包含了描述文件节区的信息，每个节区在表中都有一项，每一项给出诸如节区名称、节区大小这类信息。用于链接的目标文件必须包含节区头部表，其他目标文件可以有，也可以没有这个表。
         * */
        parseElfHeader();
        parseProgramHeaderList();
        praseSectionHeaderList();
        parseSymbolTableList();
        parseStringTableList();
    }

    private void parseElfHeader() {
        /**
         * public byte[] e_ident = new byte[16];
         * public short e_type;
         * public short e_machine;
         * public int e_version;
         * public int e_entry;
         * public int e_phoff;
         * public int e_shoff;
         * public int e_flags;
         * public short e_ehsize;
         * public short e_phentsize;
         * public short e_phnum;
         * public short e_shentsize;
         * public short e_shnum;
         * public short e_shstrndx;
         **/
        System.out.println("++++++Elf header++++++");
        type_32.hdr.e_ident = Utils.copy(this.elfData, 0, 16);
        type_32.hdr.e_type = Utils.copy(this.elfData, 16, 2);
        type_32.hdr.e_machine = Utils.copy(this.elfData, 18, 2);
        type_32.hdr.e_version = Utils.copy(this.elfData, 20, 4);
        type_32.hdr.e_entry = Utils.copy(this.elfData, 24, 4);
        type_32.hdr.e_phoff = Utils.copy(this.elfData, 28, 4);
        type_32.hdr.e_shoff = Utils.copy(this.elfData, 32, 4);
        type_32.hdr.e_flags = Utils.copy(this.elfData, 36, 4);
        type_32.hdr.e_ehsize = Utils.copy(this.elfData, 40, 2);
        type_32.hdr.e_phentsize = Utils.copy(this.elfData, 42, 2);
        type_32.hdr.e_phnum = Utils.copy(this.elfData, 44, 2);
        type_32.hdr.e_shentsize = Utils.copy(this.elfData, 46, 2);
        type_32.hdr.e_shnum = Utils.copy(this.elfData, 48, 2);
        type_32.hdr.e_shstrndx = Utils.copy(this.elfData, 50, 2);
        System.out.println("header:\n" + type_32.hdr + "\n");
    }

    private void parseProgramHeaderList() {
        System.out.println("++++++Program header++++++");
        int header_offset = Utils.byte2Int(type_32.hdr.e_phoff);
        int header_size = Utils.byte2Short(type_32.hdr.e_phentsize); // 32 位的 ELF 文件（Elf32_Phdr 结构），程序头表项的大小固定为 32 字节。或者读取 e_phentsize
        int header_count = Utils.byte2Short(type_32.hdr.e_phnum);  // 头部的个数
        byte[] des = new byte[header_size];
        for (int i = 0; i < header_count; i++) {
            System.arraycopy(this.elfData, i * header_size + header_offset, des, 0, header_size);
            type_32.phdrList.add(parseProgramHeader(des));
        }
        type_32.printPhdrList();
    }

    private static ElfType32.elf32_phdr parseProgramHeader(byte[] header) {
        /**
         public int p_type;
         public int p_offset;
         public int p_vaddr;
         public int p_paddr;
         public int p_filesz;
         public int p_memsz;
         public int p_flags;
         public int p_align;
         */
        ElfType32.elf32_phdr phdr = new ElfType32.elf32_phdr();
        phdr.p_type = Utils.copy(header, 0, 4);
        phdr.p_offset = Utils.copy(header, 4, 4);
        phdr.p_vaddr = Utils.copy(header, 8, 4);
        phdr.p_paddr = Utils.copy(header, 12, 4);
        phdr.p_filesz = Utils.copy(header, 16, 4);
        phdr.p_memsz = Utils.copy(header, 20, 4);
        phdr.p_flags = Utils.copy(header, 24, 4);
        phdr.p_align = Utils.copy(header, 28, 4);
        return phdr;
    }

    private void praseSectionHeaderList() {
        System.out.println("++++++Section header++++++");
        int header_offset = Utils.byte2Int(type_32.hdr.e_shoff);
        int header_size = Utils.byte2Short(type_32.hdr.e_shentsize);  //  32位ELF文件中，每一个表项Entry的长度是0x28=40个字节
        int header_count = Utils.byte2Short(type_32.hdr.e_shnum);
        byte[] des = new byte[header_size];
        for (int i = 0; i < header_count; i++) {
            System.arraycopy(this.elfData, i * header_size + header_offset, des, 0, header_size);
            type_32.shdrList.add(parseSectionHeader(des));
        }
        type_32.printShdrList();
    }

    private static ElfType32.elf32_shdr parseSectionHeader(byte[] header) {
        /**
         public byte[] sh_name = new byte[4];
         public byte[] sh_type = new byte[4];
         public byte[] sh_flags = new byte[4];
         public byte[] sh_addr = new byte[4];
         public byte[] sh_offset = new byte[4];
         public byte[] sh_size = new byte[4];
         public byte[] sh_link = new byte[4];
         public byte[] sh_info = new byte[4];
         public byte[] sh_addralign = new byte[4];
         public byte[] sh_entsize = new byte[4];
         */
        ElfType32.elf32_shdr shdr = new ElfType32.elf32_shdr();
        shdr.sh_name = Utils.copy(header, 0, 4);
        shdr.sh_type = Utils.copy(header, 4, 4);
        shdr.sh_flags = Utils.copy(header, 8, 4);
        shdr.sh_addr = Utils.copy(header, 12, 4);
        shdr.sh_offset = Utils.copy(header, 16, 4);
        shdr.sh_size = Utils.copy(header, 20, 4);
        shdr.sh_link = Utils.copy(header, 24, 4);
        shdr.sh_info = Utils.copy(header, 28, 4);
        shdr.sh_addralign = Utils.copy(header, 32, 4);
        shdr.sh_entsize = Utils.copy(header, 36, 4);
        return shdr;
    }

    /**
     * 解析符号表信息(Symbol Table)内容
     **/
    public void parseSymbolTableList() {
        System.out.println("+++++++++++++++++++Symbol Table++++++++++++++++++");
        // 这里需要注意的是：在Elf表中没有找到SymbolTable的数目，但是我们仔细观察Section中的Type=DYNSYM段的信息可以得到，这个段的大小和偏移地址，而SymbolTable的结构大小是固定的16个字节
        // 那么这里的数目=大小/结构大小
        // 首先在SectionHeader中查找到dynsym段的信息
        int offset_sym = 0;
        int total_sym = 0;
        for (ElfType32.elf32_shdr shdr : type_32.shdrList) {
            if (Utils.byte2Int(shdr.sh_type) == ElfType32.SHT_DYNSYM) {
                total_sym = Utils.byte2Int(shdr.sh_size);
                offset_sym = Utils.byte2Int(shdr.sh_offset);
                break;
            }
        }
        int num_sym = total_sym / 16;
        System.out.println("sym num=" + num_sym);

        int header_size = 16;  //16个字节
        byte[] des = new byte[header_size];
        for (int i = 0; i < num_sym; i++) {
            System.arraycopy(this.elfData, i * header_size + offset_sym, des, 0, header_size);
            type_32.symList.add(parseSymbolTable(des));
        }
        type_32.printSymList();
    }

    private static ElfType32.Elf32_Sym parseSymbolTable(byte[] header) {
        /**
         public byte[] st_name = new byte[4];
         public byte[] st_value = new byte[4];
         public byte[] st_size = new byte[4];
         public byte st_info;
         public byte st_other;
         public byte[] st_shndx = new byte[2];
         */
        ElfType32.Elf32_Sym sym = new ElfType32.Elf32_Sym();
        sym.st_name = Utils.copy(header, 0, 4);
        sym.st_value = Utils.copy(header, 4, 4);
        sym.st_size = Utils.copy(header, 8, 4);
        sym.st_info = header[12];
        sym.st_other = header[13];
        sym.st_shndx = Utils.copy(header, 14, 2);
        return sym;
    }

    /**
     * 解析字符串表信息(String Table)内容
     **/
    private static void parseStringTableList() {
        System.out.println("+++++++++++++++++++Symbol Table++++++++++++++++++");
        // 这里需要注意的是：在Elf表中没有找到StringTable的数目，但是我们仔细观察Section中的Type=STRTAB段的信息，可以得到，这个字符串的大段的大小和偏移地址，但是我们这时候我们不知道大小，所以就获取不到数目了
        // 这里我们可以查看Section结构中的name字段：表示偏移值，那么我们可以通过这个值来获取字符串的大小
        // 可以这么理解：当前段的name值 减去 上一段的name的值 = (上一段的name字符串的长度)
        // 首先获取每个段的name的字符串大小
        int prename_len = 0;
        int[] lens = new int[type_32.shdrList.size()];
        int total = 0;
        for (int i = 0; i < type_32.shdrList.size(); i++) {
            if (Utils.byte2Int(type_32.shdrList.get(i).sh_type) == ElfType32.SHT_STRTAB) {
                int curname_offset = Utils.byte2Int(type_32.shdrList.get(i).sh_name);
                lens[i] = curname_offset - prename_len - 1;
                if (lens[i] < 0) {
                    lens[i] = 0;
                }
                total += lens[i];
                System.out.println("total:" + total);
                prename_len = curname_offset;
                // 这里需要注意的是，最后一个字符串的长度，需要用总长度减去前面的长度总和来获取到
                if (i == (lens.length - 1)) {
                    System.out.println("size:" + Utils.byte2Int(type_32.shdrList.get(i).sh_size));
                    lens[i] = Utils.byte2Int(type_32.shdrList.get(i).sh_size) - total - 1;
                }
            }
        }
        for (int i = 0; i < lens.length; i++) {
            System.out.println("len:" + lens[i]);
        }
    }
}
