package elfparse;

/**
 * Created by zsk
 * on 2024/11/24 01:17
 */

import utils.Utils;

import java.util.ArrayList;

// http://androidxref.com/9.0.0_r3/xref/external/syslinux/com32/include/sys/elf32.h#37

/**
 * dr => header
 * <p>
 * Elf32_Addr 4 4 无符号程序地址
 * Elf32_Half 2 2 无符号中等整数
 * Elf32_Off 4 4 无符号文件偏移
 * Elf32_SWord 4 4 有符号大整数
 * Elf32_Word 4 4 无符号大整数
 * unsigned char 1 1 无符号小整数
 */
public class ElfType32 {
    public elf32_rel rel;
    public elf32_rela rela;
    public ArrayList<Elf32_Sym> symList = new ArrayList<Elf32_Sym>();
    public elf32_hdr hdr;  //elf头部信息
    public ArrayList<elf32_phdr> phdrList = new ArrayList<elf32_phdr>();  // 可能会有多个程序头
    public ArrayList<elf32_shdr> shdrList = new ArrayList<elf32_shdr>();  // 可能会有多个段头

    public ElfType32() {
        rel = new elf32_rel();
        rela = new elf32_rela();
        hdr = new elf32_hdr();
    }

    /**
     * 主要用于 隐式重定位, 需要从目标位置的原始值中推导附加偏移量
     * typedef struct elf32_rel {
     * Elf32_Addr	r_offset;  // 重定位目标地址（需要被修改的地方）
     * Elf32_Word	r_info;  // 符号和类型信息
     * } Elf32_Rel;
     */
    public class elf32_rel {
        public byte[] r_offset = new byte[4];
        public byte[] r_info = new byte[4];

        @Override
        public String toString() {
            return "elf32_rel: "
                    + "\n\tr_offset: " + Utils.byteToHexReverse(r_offset)
                    + "\n\tr_info: " + Utils.byteToHexReverse(r_info);
        }

        ;
    }

    /**
     * 主要用于 显式重定位, 常用于动态链接的场景
     * typedef struct elf32_rela{
     * Elf32_Addr	r_offset;  // 重定位目标地址（需要被修改的地方）
     * Elf32_Word	r_info;  // 符号和类型信息
     * Elf32_Sword	r_addend;  // 附加的偏移量
     * } Elf32_Rela;
     */
    public class elf32_rela {
        public byte[] r_offset = new byte[4];
        public byte[] r_info = new byte[4];
        public byte[] r_addend = new byte[4];

        @Override
        public String toString() {
            return "elf32_rela: "
                    + "\n\tr_offset: " + Utils.byteToHexReverse(r_offset)
                    + "\n\tr_info: " + Utils.byteToHexReverse(r_info)
                    + "\n\tr_addend: " + Utils.byteToHexReverse(r_addend);
        }
    }

    /**
     * typedef struct elf32_sym{
     * Elf32_Word	st_name;  // 包含目标文件符号字符串表的索引，其中包含符号名的字符串表示。
     * Elf32_Addr	st_value;  // 此成员给出相关联的符号的取值。
     * Elf32_Word	st_size;  // 很多符号具有相关的尺寸大小。
     * unsigned char	st_info;  // 此成员给出符号的类型和绑定属性。
     * unsigned char	st_other;  // 该成员当前包含 0，其含义没有定义。
     * Elf32_Half	st_shndx;  // 每个符号表项都以和其他节区间的关系的方式给出定义。
     * } Elf32_Sym;
     */
    public static class Elf32_Sym {
        public byte[] st_name = new byte[4];
        public byte[] st_value = new byte[4];
        public byte[] st_size = new byte[4];
        public byte st_info;
        public byte st_other;
        public byte[] st_shndx = new byte[2];

        @Override
        public String toString() {
            return "Elf32_Sym: "
                    + "\n\tst_name: " + Utils.byteToHexReverse(st_name)
                    + "\n\tst_value: " + Utils.byteToHexReverse(st_value)
                    + "\n\tst_size: " + Utils.byteToHexReverse(st_size)
                    + "\n\tst_info: " + (st_info / 16)
                    + "\n\tst_other: " + (((short) st_other) & 0xF)
                    + "\n\tst_shndx: " + Utils.byteToHexReverse(st_shndx);
        }
    }

    public void printSymList() {
        for (int i = 0; i < symList.size(); i++) {
            System.out.println("The " + (i + 1) + " Symbol Table:");
            System.out.println(symList.get(i).toString() + "\n");
        }
    }

    /**
     * typedef struct elf32_hdr{
     * unsigned char	e_ident[EI_NIDENT];  // 魔数和文件标识
     * Elf32_Half	e_type;  // 文件类型
     * Elf32_Half	e_machine;  // 目标机器架构
     * Elf32_Word	e_version;  // 目标文件版本
     * Elf32_Addr	e_entry;  // 程序入口的虚拟地址。如果目标文件没有程序入口，可以为 0。
     * Elf32_Off	e_phoff;  // 程序头部表格的偏移。如果文件没有程序头部表格，可以为 0。
     * Elf32_Off	e_shoff;  // 节区头部表格的偏移量。如果文件没有节区头部表格，可以为 0。
     * Elf32_Word	e_flags;  // 保存与文件相关的，特定于处理器的标志。
     * Elf32_Half	e_ehsize;  // ELF 头部的大小
     * Elf32_Half	e_phentsize;  // 程序头部表格的表项大小
     * Elf32_Half	e_phnum;  // 程序头部表格的表项数目。可以为 0。
     * Elf32_Half	e_shentsize;  // 节区头部表格的表项大小
     * Elf32_Half	e_shnum;  // 节区头部表格的表项数目。可以为 0。
     * Elf32_Half	e_shstrndx;  // 节区头部表格中与节区名称字符串表相关的表项的索引。如果文件没有节区名称字符串表，此参数可以为 SHN_UNDEF。
     * } Elf32_Ehdr;
     * <p>
     * 其中，e_ident 数组给出了 ELF 的一些标识信息，这个数组中不同下标的含义
     * e_ident[] 标识索引
     * 名称 取值 目的
     * EI_MAG0 0 文件标识
     * EI_MAG1 1 文件标识
     * EI_MAG2 2 文件标识
     * EI_MAG3 3 文件标识
     * EI_CLASS 4 文件类
     * EI_DATA 5 数据编码
     * EI_VERSION 6 文件版本
     * EI_PAD 7 补齐字节开始处
     * EI_NIDENT 16 e_ident[]大小
     */
    public class elf32_hdr {
        public byte[] e_ident = new byte[16];
        public byte[] e_type = new byte[2];
        public byte[] e_machine = new byte[2];
        public byte[] e_version = new byte[4];
        public byte[] e_entry = new byte[4];
        public byte[] e_phoff = new byte[4];  // 这个字段是程序头(Program Header)内容在整个文件的偏移值，我们可以用这个偏移值来定位程序头的开始位置，用于解析程序头信息
        public byte[] e_shoff = new byte[4];  // 这个字段是段头(Section Header)内容在这个文件的偏移值，我们可以用这个偏移值来定位段头的开始位置，用于解析段头信息
        public byte[] e_flags = new byte[4];
        public byte[] e_ehsize = new byte[2];
        public byte[] e_phentsize = new byte[2];
        public byte[] e_phnum = new byte[2];  // 这个字段是程序头的个数，用于解析程序头信息
        public byte[] e_shentsize = new byte[2];
        public byte[] e_shnum = new byte[2];  //  这个字段是段头的个数，用于解析段头信息
        public byte[] e_shstrndx = new byte[2];  // 这个字段是String段在整个段列表中的索引值，这个用于后面定位String段的位置

        @Override
        public String toString() {
            return "elf32_hdr: "
                    + "\n\tmagic:" + Utils.byteToHexReverse(e_ident)
                    + "\n\te_type:" + Utils.byteToHexReverse(e_type)
                    + "\n\te_machine:" + Utils.byteToHexReverse(e_machine)
                    + "\n\te_version:" + Utils.byteToHexReverse(e_version)
                    + "\n\te_entry:" + Utils.byteToHexReverse(e_entry)
                    + "\n\te_phoff:" + Utils.byteToHexReverse(e_phoff)
                    + "\n\te_shoff:" + Utils.byteToHexReverse(e_shoff)
                    + "\n\te_flags:" + Utils.byteToHexReverse(e_flags)
                    + "\n\te_ehsize:" + Utils.byteToHexReverse(e_ehsize)
                    + "\n\te_phentsize:" + Utils.byteToHexReverse(e_phentsize)
                    + "\n\te_phnum:" + Utils.byteToHexReverse(e_phnum)
                    + "\n\te_shentsize:" + Utils.byteToHexReverse(e_shentsize)
                    + "\n\te_shnum:" + Utils.byteToHexReverse(e_shnum)
                    + "\n\te_shstrndx:" + Utils.byteToHexReverse(e_shstrndx);
        }
    }

    /**
     * typedef struct elf32_phdr{
     * Elf32_Word	p_type;  // 此数组元素描述的段的类型，或者如何解释此数组元素的信息
     * Elf32_Off	p_offset;  // 此成员给出从文件头到该段第一个字节的偏移。
     * Elf32_Addr	p_vaddr;  // 此成员给出段的第一个字节将被放到内存中的虚拟地址。因为 System V 忽略所有应用程序的物理地址信息，此字段对与可执行文件和共享目标文件而言具体内容是未指定的。
     * Elf32_Addr	p_paddr;  // 此成员仅用于与物理地址相关的系统中。
     * Elf32_Word	p_filesz;  // 此成员给出段在文件映像中所占的字节数。可以为 0。
     * Elf32_Word	p_memsz;  // 此成员给出段在内存映像中占用的字节数。可以为 0。
     * Elf32_Word	p_flags;  // 此成员给出与段相关的标志
     * Elf32_Word	p_align;  // 可加载的进程段的 p_vaddr 和 p_offset 取值必须合适，相对于对页面大小的取模而言。此成员给出段在文件中和内存中如何对齐。数值 0 和 1 表示不需要对齐。
     * } Elf32_Phdr;
     * 可执行 ELF 目标文件中的段类型如下所示
     * | 名字         | 取值        | 说明                                                                                                         |
     * |--------------|-------------|------------------------------------------------------------------------------------------------------------|
     * | PT_NULL    | 0           | 此数组元素未用。结构中其他成员都是未定义的。                                                                                     |
     * | PT_LOAD    | 1           | 此数组元素给出一个可加载的段，段的大小由 p_filesz 和 p_memsz描述。文件中的字节被映射到内存段开始处。如果 p_memsz 大于p_filesz，“剩余”的字节要清零。p_filesz 不能大于 p_memsz。可加载的段在程序头部表格中根据 p_vaddr 成员按升序排列。 |
     * | PT_DYNAMIC | 2           | 数组元素给出动态链接信息。                                                                                                 |
     * | PT_INTERP  | 3           | 数组元素给出一个 NULL 结尾的字符串的位置和长度，该字符串将被当作解释器调用。此类型段仅对可执行文件有意义，并且在文件中不能出现多次，必须在可加载段项目前面。           |
     * | PT_NOTE    | 4           | 此数组元素给出附加信息的位置和大小。                                                                                         |
     * | PT_SHLIB   | 5           | 此段类型被保留，不过语义未指定。包含这种类型的段的程序与 ABI 不符。                                                                           |
     * | PT_PHDR    | 6           | 此类型的数组元素如果存在，则给出了程序头部表自身的大小和位置，既包括在文件中也包括在内存中的信息。此类型的段在文件中不能出现多次。只有程序头部表是程序的内存映像的一部分时才起作用，并且必须在所有可加载段项目前面。 |
     * | PT_LOPROC  | 0x70000000  | 保留给处理器专用语义的段类型范围的起始值。                                                                                     |
     * | PT_HIPROC  | 0x7fffffff  | 保留给处理器专用语义的段类型范围的结束值。                                                                                     |
     */

    public static class elf32_phdr {
        public byte[] p_type = new byte[4];
        public byte[] p_offset = new byte[4];
        public byte[] p_vaddr = new byte[4];
        public byte[] p_paddr = new byte[4];
        public byte[] p_filesz = new byte[4];
        public byte[] p_memsz = new byte[4];
        public byte[] p_flags = new byte[4];
        public byte[] p_align = new byte[4];

        @Override
        public String toString() {
            return "elf32_phdr: "
                    + "\n\tp_type: " + Utils.byteToHexReverse(p_type)
                    + "\n\tp_offset: " + Utils.byteToHexReverse(p_offset)
                    + "\n\tp_vaddr: " + Utils.byteToHexReverse(p_vaddr)
                    + "\n\tp_paddr: " + Utils.byteToHexReverse(p_paddr)
                    + "\n\tp_filesz: " + Utils.byteToHexReverse(p_filesz)
                    + "\n\tp_memsz: " + Utils.byteToHexReverse(p_memsz)
                    + "\n\tp_flags: " + Utils.byteToHexReverse(p_flags)
                    + "\n\tp_align: " + Utils.byteToHexReverse(p_align);
        }
    }

    public void printPhdrList() {
        for (int i = 0; i < phdrList.size(); i++) {
            System.out.println("The " + (i + 1) + " Program Header:");
            System.out.println(phdrList.get(i).toString() + "\n");
        }
    }

    /**
     * typedef struct elf32_shdr {
     * Elf32_Word	sh_name;  // 给出节区名称。是节区头部字符串表节区（Section Header StringTable Section）的索引。名字是一个 NULL 结尾的字符串。
     * Elf32_Word	sh_type;  // 为节区的内容和语义进行分类。
     * Elf32_Word	sh_flags;  // 节区支持 1 位形式的标志，这些标志描述了多种属性。
     * Elf32_Addr	sh_addr;  // 如果节区将出现在进程的内存映像中，此成员给出节区的第一个字节应处的位置。否则，此字段为 0。
     * Elf32_Off	sh_offset;  // 此成员的取值给出节区的第一个字节与文件头之间的偏移。不过，SHT_NOBITS 类型的节区不占用文件的空间，因此其 sh_offset 成员给出的是其概念性的偏移。
     * Elf32_Word	sh_size;  // 此成员给出节区的长度（字节数）。除非节区的类型 是SHT_NOBITS，否则节区占用文件中的 sh_size 字节。类型为SHT_NOBITS 的节区长度可能非零，不过却不占用文件中的空间。
     * Elf32_Word	sh_link;  // 此成员给出节区头部表索引链接。其具体的解释依赖于节区类型
     * Elf32_Word	sh_info;  // 此成员给出附加信息，其解释依赖于节区类型。
     * Elf32_Word	sh_addralign;  // 某些节区带有地址对齐约束。例如，如果一个节区保存一个doubleword，那么系统必须保证整个节区能够按双字对齐。sh_addr对 sh_addralign 取模，结果必须为 0。目前仅允许取值为 0 和 2 的幂次数。数值 0 和 1 表示节区没有对齐约束。
     * Elf32_Word	sh_entsize;  // 某些节区中包含固定大小的项目，如符号表。对于这类节区，此成员给出每个表项的长度字节数。 如果节区中并不包含固定长度表项的表格，此成员取值为 0。
     * } Elf32_Shdr;
     */
    public static class elf32_shdr {
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

        @Override
        public String toString() {
            return "elf32_shdr: "
                    + "\n\tsh_name: " + Utils.byteToHexReverse(sh_name)  /*Utils.byte2Int(sh_name)*/
                    + "\n\tsh_type: " + Utils.byteToHexReverse(sh_type)
                    + "\n\tsh_flags: " + Utils.byteToHexReverse(sh_flags)
                    + "\n\tsh_add: " + Utils.byteToHexReverse(sh_addr)
                    + "\n\tsh_offset: " + Utils.byteToHexReverse(sh_offset)
                    + "\n\tsh_size: " + Utils.byteToHexReverse(sh_size)
                    + "\n\tsh_link: " + Utils.byteToHexReverse(sh_link)
                    + "\n\tsh_info: " + Utils.byteToHexReverse(sh_info)
                    + "\n\tsh_addralign: " + Utils.byteToHexReverse(sh_addralign)
                    + "\n\tsh_entsize: " + Utils.byteToHexReverse(sh_entsize);
        }
    }

    public void printShdrList() {
        for (int i = 0; i < shdrList.size(); i++) {
            System.out.println("The " + (i + 1) + " Section Header:");
            System.out.println(shdrList.get(i) + "\n");
        }
    }

    /****************节区类型—sh_type 字段********************/
    public static final int SHT_NULL = 0;  // 无效节，表示此节未使用。通常用于初始化节区表项或作为占位符。
    public static final int SHT_PROGBITS = 1;  // 此节区包含程序定义的信息，其格式和含义都由程序来解释。
    public static final int SHT_SYMTAB = 2;  // 此节区包含一个符号表，用于存储符号信息（如函数和变量的名字与地址）
    public static final int SHT_STRTAB = 3;  // 此节区包含字符串表, 用于存储字符串, 目标文件可能包含多个字符串表节区
    public static final int SHT_RELA = 4;  // 包含带附加值（addend）的重定位表项，例如 32 位目标文件中的 `Elf32_Rela` 类型。用于重定位符号地址，常见于动态链接。
    public static final int SHT_HASH = 5;  // 此节区包含符号哈希表。所有参与动态链接的目标都必须包含一个符号哈希表。
    public static final int SHT_DYNAMIC = 6;  // 此节区包含动态链接的信息。
    public static final int SHT_NOTE = 7;  // 注释节，用于存储一些附加信息，如 ABI 标记。
    public static final int SHT_NOBITS = 8;  // 无数据节，其内容不占用文件空间，例如 `.bss` 段。在运行时分配内存并初始化为 0。
    public static final int SHT_REL = 9;  // 包含不带附加值（addend）的重定位表项，例如 32 位目标文件中的 `Elf32_Rel` 类型。目标文件可能包含多个此类节区。
    public static final int SHT_SHLIB = 10;  // 保留节，目前未定义用途。
    public static final int SHT_DYNSYM = 11;  // 动态符号表，保存动态链接所需的符号子集。相比 `SHT_SYMTAB` 更精简，目的是节省空间。
    public static final int SHT_NUM = 12;  // 表示节区类型的数量（不是节区本身），通常用于验证节区类型的范围。
    public static final int SHT_LOPROC = 0x70000000;
    public static final int SHT_HIPROC = 0x7fffffff;
    public static final int SHT_LOUSER = 0x80000000;
    public static final int SHT_HIUSER = 0xffffffff;
    public static final int SHT_MIPS_LIST = 0x70000000;
    public static final int SHT_MIPS_CONFLICT = 0x70000002;
    public static final int SHT_MIPS_GPTAB = 0x70000003;
    public static final int SHT_MIPS_UCODE = 0x70000004;

    /**************** sh_flags 字段 ********************/
    public static final int SHF_WRITE = 0x1;
    public static final int SHF_ALLOC = 0x2;
    public static final int SHF_EXECINSTR = 0x4;
    public static final int SHF_MASKPROC = 0xf0000000;
    public static final int SHF_MIPS_GPREL = 0x10000000;

    /**
     * 常见特殊节区：
     *
     * | 名称        | 类型         | 属性                  | 含义                                                                              |
     * |-------------|--------------|-----------------------|----------------------------------------------------------------------------------|
     * | .bss        | SHT_NOBITS   | SHF_ALLOC+SHF_WRITE   | 包含将出现在程序的内存映像中的未初始化数据。当程序开始执行时，系统将其初始化为 0。此节区不占用文件空间。|
     * | .comment    | SHT_PROGBITS | (无)                  | 包含版本控制信息。                                                                     |
     * | .data       | SHT_PROGBITS | SHF_ALLOC+SHF_WRITE   | 包含初始化数据，将出现在程序的内存映像中。                                                  |
     * | .data1      | SHT_PROGBITS | SHF_ALLOC+SHF_WRITE   | 包含初始化数据，将出现在程序的内存映像中。                                                  |
     * | .debug      | SHT_PROGBITS | (无)                  | 包含用于符号调试的信息。                                                                 |
     * | .dynamic    | SHT_DYNAMIC  | SHF_ALLOC 或 SHF_WRITE| 包含动态链接信息。SHF_WRITE 是否设置取决于处理器。                                          |
     * | .dynstr     | SHT_STRTAB   | SHF_ALLOC             | 包含用于动态链接的字符串，大多数情况下代表了与符号表项相关的名称。                               |
     * | .dynsym     | SHT_DYNSYM   | SHF_ALLOC             | 包含动态链接符号表。                                                                    |
     * | .fini       | SHT_PROGBITS | SHF_ALLOC+SHF_EXECINSTR    | 包含可执行指令，是进程终止代码的一部分。程序正常退出时，系统将安排执行此代码。                 |
     * | .got        | SHT_PROGBITS | (无)                       | 包含全局偏移表。                                                                   |
     * | .hash       | SHT_HASH     | SHF_ALLOC                  | 包含符号哈希表。                                                                   |
     * | .init       | SHT_PROGBITS | SHF_ALLOC+SHF_EXECINSTR    | 包含可执行指令，是进程初始化代码的一部分。程序开始执行时，在调用主程序入口前执行此代码。         |
     * | .interp     | SHT_PROGBITS | (无/SHF_ALLOC)             | 包含程序解释器路径名，属性是否包含 SHF_ALLOC 取决于文件是否包含可加载段。                   |
     * | .line       | SHT_PROGBITS | (无)                       | 包含符号调试的行号信息，描述源程序与机器指令的对应关系。                                   |
     * | .note       | SHT_NOTE     | (无)                       | 包含注释信息，格式独立。                                                            |
     * | .plt        | SHT_PROGBITS | (无)                       | 包含过程链接表（Procedure Linkage Table）。                                        |
     * | .relname    | SHT_REL      | (无/SHF_ALLOC)             | 包含重定位信息，name 根据适用节区命名，如 .rel.text。                                 |
     * | .relaname   | SHT_RELA     | (无/SHF_ALLOC)             | 包含重定位信息，name 根据适用节区命名，如 .rela.text。                                |
     * | .rodata     | SHT_PROGBITS | SHF_ALLOC                  | 包含只读数据，通常参与进程映像的不可写段。                                            |
     * | .rodata1    | SHT_PROGBITS | SHF_ALLOC                  | 包含只读数据，通常参与进程映像的不可写段。                                            |
     * | .shstrtab   | SHT_STRTAB   | (无)                       | 包含节区名称。                                                                   |
     * | .strtab     | SHT_STRTAB   | (无/SHF_ALLOC)             | 包含字符串，通常代表符号表项相关的名称，SHF_ALLOC 取决于文件是否包含可加载段。             |
     * | .symtab     | SHT_SYMTAB   | (无/SHF_ALLOC)             | 包含符号表，SHF_ALLOC 取决于文件是否包含可加载段。                                    |
     * | .text       | SHT_PROGBITS | SHF_ALLOC+SHF_EXECINSTR    | 包含程序的可执行指令。                                                          |
     */

}
