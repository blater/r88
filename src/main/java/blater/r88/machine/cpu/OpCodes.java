package blater.r88.machine.cpu;

import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;

@Value
public class OpCodes {
    static List<OpCode> opcodes = new ArrayList<>();
    //Rocket88 ISA

    static Map<Short, OpCode> byByteCode;
    static Map<String, List<OpCode>> byFirstWord;

    public static OpCode of(short bytecode) {
        return byByteCode.get(bytecode);
    }

    private static void add(String mnemonic, String opcode, String arg1, String arg2, String aResult,
                            String bResult, String cResult, String pcResult, String spResult, String signResult,
                            String zeroResult, String carryResult, String intResult, String decResult, String breakResult,
                            String notes) {
        opcodes.add(OpCode.of(mnemonic, Short.decode("0x" + opcode), arg1, arg2, aResult, bResult, cResult, pcResult, spResult, signResult,
            zeroResult, carryResult, intResult, decResult, breakResult, notes));
    }

    static {
        add("NOP", "00", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Assembler should default to 00 for NOP opcode");
        add("NOP1", "04", "N", "X", "X", "X", "X", "+2", "X", "X", "X", "X", "X", "X", "0", "NOP with 1-byte argument");
        add("NOP2", "05", "NL", "NH", "X", "X", "X", "+3", "X", "X", "X", "X", "X", "X", "0", "NOP with 2-byte argument");
        add("LDAZ", "10", "X", "X", "0", "X", "X", "+1", "X", "0", "1", "X", "X", "X", "0", "");
        add("LDA #0", "10", "X", "X", "0", "X", "X", "+1", "X", "0", "1", "X", "X", "X", "0", "");
        add("LDA A", "11", "X", "X", "A", "X", "X", "+1", "X", "A < 0", "A == 0", "X", "X", "X", "0", "");
        add("LDA B", "12", "X", "X", "B", "X", "X", "+1", "X", "B < 0", "B == 0", "X", "X", "X", "0", "");
        add("LDA C", "13", "X", "X", "C", "X", "X", "+1", "X", "C < 0", "C == 0", "X", "X", "X", "0", "");
        add("LDA #N", "14", "N", "X", "N", "X", "X", "+2", "X", "N < 0", "N == 0", "X", "X", "X", "0", "");
        add("LDA NN", "15", "NL", "NH", "(NN)", "X", "X", "+3", "X", "(NN) < 0", "(NN) == 0", "X", "X", "X", "0", "");
        add("LDA (BC)", "16", "X", "X", "(BC)", "X", "X", "+1", "X", "(BC) < 0", "(BC) == 0", "X", "X", "X", "0", "");
        add("PLA", "17", "X", "X", "(SP)", "X", "X", "+1", "+1", "(SP) < 0", "(SP) == 0", "X", "X", "X", "0", "");
        add("CPAZ", "18", "", "", "", "", "", "+1", "", "", "A == 0", "", "", "", "", "Illegal Opcode for storing A to immediate zero; acts as NOP");
        add("CPA #0", "18", "", "", "", "", "", "+1", "", "", "A == 0", "", "", "", "", "Illegal Opcode for storing A to immediate zero; acts as NOP");
        add("CPA A", "19", "X", "X", "X", "X", "X", "+1", "X", "0", "1", "1", "X", "X", "0", "");
        add("CPA B", "1A", "X", "X", "X", "X", "X", "+1", "X", "A < B", "A == B", "A >= B", "X", "X", "0", "");
        add("CPA C", "1B", "X", "X", "X", "X", "X", "+1", "X", "A < C", "A == C", "A >= C", "X", "X", "0", "");
        add("CPA #N", "1C", "N", "X", "", "", "", "+2", "", "A < N", "A == N", "A >= N", "", "", "", "Illegal Opcode for storing A to immediate value; acts as NOP with 1-byte argument");
        add("STA NN", "1D", "NL", "NH", "X", "X", "X", "+3", "X", "X", "X", "X", "X", "X", "0", "Memory at address NN = A");
        add("STA (BC)", "1E", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Memory at address BC = A");
        add("PHA", "1F", "X", "X", "X", "X", "X", "+1", "-1", "X", "X", "X", "X", "X", "0", "A pushed to stack");
        add("LDBZ", "20", "X", "X", "X", "0", "X", "+1", "X", "0", "1", "X", "X", "X", "0", "");
        add("LDB #0", "20", "X", "X", "X", "0", "X", "+1", "X", "0", "1", "X", "X", "X", "0", "");
        add("LDB A", "21", "X", "X", "X", "A", "X", "+1", "X", "A < 0", "A == 0", "X", "X", "X", "0", "");
        add("LDB B", "22", "X", "X", "X", "B", "X", "+1", "X", "B < 0", "B == 0", "X", "X", "X", "0", "");
        add("LDB C", "23", "X", "X", "X", "C", "X", "+1", "X", "C < 0", "C == 0", "X", "X", "X", "0", "");
        add("LDB #N", "24", "N", "X", "X", "N", "X", "+2", "X", "N < 0", "N == 0", "X", "X", "X", "0", "");
        add("LDB NN", "25", "NL", "NH", "X", "(NN)", "X", "+3", "X", "(NN) < 0", "(NN) == 0", "X", "X", "X", "0", "");
        add("LDB (BC)", "26", "X", "X", "X", "(BC)", "X", "+1", "X", "(BC) < 0", "(BC) == 0", "X", "X", "X", "0", "");
        add("PLB", "27", "X", "X", "X", "(SP)", "X", "+1", "+1", "(SP) < 0", "(SP) == 0", "X", "X", "X", "0", "");
        add("CPBZ", "28", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for storing B to immediate zero; acts as NOP");
        add("CPB #0", "28", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for storing B to immediate zero; acts as NOP");
        add("CPB A", "29", "X", "X", "X", "X", "X", "+1", "X", "B < A", "B == A", "B >= A", "X", "X", "0", "");
        add("CPB B", "2A", "X", "X", "X", "X", "X", "+1", "X", "0", "1", "1", "X", "X", "0", "");
        add("CPB C", "2B", "X", "X", "X", "X", "X", "+1", "X", "B < C", "B == C", "B >= C", "X", "X", "0", "");
        add("CPB #N", "2C", "N", "X", "X", "X", "X", "+2", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for storing B to immediate value; acts as NOP with 1-byte argument");
        add("STB NN", "2D", "NL", "NH", "X", "X", "X", "+3", "X", "X", "X", "X", "X", "X", "0", "Memory at address NN = B");
        add("STB (BC)", "2E", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Memory at address BC = B");
        add("PHB", "2F", "X", "X", "X", "X", "X", "+1", "-1", "X", "X", "X", "X", "X", "0", "B pushed to stack");
        add("LDCZ", "30", "X", "X", "X", "X", "0", "+1", "X", "0", "1", "X", "X", "X", "0", "");
        add("LDC #0", "30", "X", "X", "X", "X", "0", "+1", "X", "0", "1", "X", "X", "X", "0", "");
        add("LDC A", "31", "X", "X", "X", "X", "A", "+1", "X", "A < 0", "A == 0", "X", "X", "X", "0", "");
        add("LDC B", "32", "X", "X", "X", "X", "B", "+1", "X", "B < 0", "B == 0", "X", "X", "X", "0", "");
        add("LDC C", "33", "X", "X", "X", "X", "C", "+1", "X", "C < 0", "C == 0", "X", "X", "X", "0", "");
        add("LDC #N", "34", "N", "X", "X", "X", "N", "+2", "X", "N < 0", "N == 0", "X", "X", "X", "0", "");
        add("LDC NN", "35", "NL", "NH", "X", "X", "(NN)", "+3", "X", "(NN) < 0", "(NN) == 0", "X", "X", "X", "0", "");
        add("LDC (BC)", "36", "X", "X", "X", "X", "(BC)", "+1", "X", "(BC) < 0", "(BC) == 0", "X", "X", "X", "0", "");
        add("PLC", "37", "X", "X", "X", "X", "(SP)", "+1", "+1", "(SP) < 0", "(SP) == 0", "X", "X", "X", "0", "");
        add("CPCZ", "38", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for storing C to immediate zero; acts as NOP");
        add("CPC #0", "38", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for storing C to immediate zero; acts as NOP");
        add("CPC A", "39", "X", "X", "X", "X", "X", "+1", "X", "C < A", "C == A", "C >= A", "X", "X", "0", "");
        add("CPC B", "3A", "X", "X", "X", "X", "X", "+1", "X", "C < B", "C == B", "C >= B", "X", "X", "0", "");
        add("CPC C", "3B", "X", "X", "X", "X", "X", "+1", "X", "0", "1", "1", "X", "X", "0", "");
        add("CPC #N", "3C", "N", "X", "X", "X", "X", "+2", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for storing C to immediate value; acts as NOP with 1-byte argument");
        add("STC NN", "3D", "NL", "NH", "X", "X", "X", "+3", "X", "X", "X", "X", "X", "X", "0", "Memory at address NN = C");
        add("STC (BC)", "3E", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Memory at address BC = C");
        add("PHC", "3F", "X", "X", "X", "X", "X", "+1", "-1", "X", "X", "X", "X", "X", "0", "C pushed to stack");
        add("EXD", "40", "X", "X", "X", "DDH", "DDL", "+1", "X", "X", "BC == 0", "X", "X", "X", "0", "Values exchanged between BC and DD");
        add("NOP", "41", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for Jumping to zero or register; acts as NOP");
        add("NOP", "42", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for Jumping to zero or register; acts as NOP");
        add("NOP", "43", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for Jumping to zero or register; acts as NOP");
        add("JMPR label", "44", "N = label offset", "X", "X", "X", "X", "+N", "X", "X", "X", "X", "X", "X", "0", "Assembler may interpret JMP NN as JMPR if destination address is within range, or support explicit +/- offset");
        add("JMP NN", "45", "NL", "NH", "X", "X", "X", "NN", "X", "X", "X", "X", "X", "X", "0", "");
        add("JMP (BC)", "46", "X", "X", "X", "X", "X", "(BC)", "X", "X", "X", "X", "X", "X", "0", "16-bit address will be fetched from memory address BC");
        add("NOP", "47", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to address on stack, acts as NOP");
        add("EXE", "48", "X", "X", "X", "EEH", "EEL", "+1", "X", "X", "BC == 0", "X", "X", "X", "0", "Values exchanged between BC and EE");
        add("NOP", "49", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for jumping to zero or register; acts as NOP");
        add("NOP", "4A", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for jumping to zero or register; acts as NOP");
        add("NOP", "4B", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for jumping to zero or register; acts as NOP");
        add("JSRR label", "4C", "N = label offset", "X", "X", "X", "X", "+N", "-2", "X", "X", "X", "X", "X", "0", "Return address pushed to stack. Assembler may interpret JSR NN as JSRR if destination address is within range, or support explicit +/- offset");
        add("JSR NN", "4D", "NL", "NH", "X", "X", "X", "NN", "-2", "X", "X", "X", "X", "X", "0", "Return address pushed to stack.  ");
        add("JSR (BC)", "4E", "X", "X", "X", "X", "X", "(BC)", "-2", "X", "X", "X", "X", "X", "0", "Return address pushed to stack. 16-bit address will be fetched from memory address BC");
        add("NOP", "4F", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to address on stack, acts as NOP");
        add("NOP", "50", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for jumping to zero or register; acts as NOP");
        add("NOP", "51", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for jumping to zero or register; acts as NOP");
        add("NOP", "52", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for jumping to zero or register; acts as NOP");
        add("NOP", "53", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for jumping to zero or register; acts as NOP");
        add("JPNZ label", "54", "N = label offset", "X", "X", "X", "X", "See Note", "X", "X", "X", "X", "X", "X", "0", "PC adds offset if Zero bit clear, adds 2 if set");
        add("JMPZ NN", "55", "NL", "NH", "X", "X", "X", "See Note", "X", "X", "X", "X", "X", "X", "0", "PC loaded with NN if Zero bit set, adds 3 if clear");
        add("JMPZ (BC)", "56", "X", "X", "X", "X", "X", "See Note", "X", "X", "X", "X", "X", "X", "0", "If Zero bit set, 16-bit address fetched from memory address BC and loaded into PC. If Zero bit clear, adds 1.");
        add("NOP", "57", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to address on stack, acts as NOP");
        add("NOP", "58", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for jumping to zero or register; acts as NOP");
        add("NOP", "59", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for jumping to zero or register; acts as NOP");
        add("NOP", "5A", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for jumping to zero or register; acts as NOP");
        add("NOP", "5B", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal Opcode for jumping to zero or register; acts as NOP");
        add("JSNZ label", "5C", "N = label offset", "X", "X", "X", "X", "See Note", "See Note", "X", "X", "X", "X", "X", "0", "PC adds offset and return address pushed to stack if Zero bit clear, PC adds 2 and no stack change if set");
        add("JSRZ NN", "5D", "NL", "NH", "X", "X", "X", "See Note", "See Note", "X", "X", "X", "X", "X", "0", "PC loaded with NN and return address pushed to stack if Zero bit set, PC adds 3 and no stack change if clear");
        add("JSRZ (BC)", "5E", "X", "X", "X", "X", "X", "See Note", "See Note", "X", "X", "X", "X", "X", "0", "If Zero bit set, 16-bit address fetched from memory address BC and loaded into PC and return address pushed to stack. If Zero bit clear, PC adds 1 and no stack change.");
        add("NOP", "5F", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to address on stack, acts as NOP");
        add("NOP", "60", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "61", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "62", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "63", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("JMPP label", "64", "N = label offset", "X", "X", "X", "X", "See Note", "X", "X", "X", "X", "X", "X", "0", "PC adds offset if Sign bit clear, adds 2 if set");
        add("JMPN NN", "65", "NL", "NH", "X", "X", "X", "See Note", "X", "X", "X", "X", "X", "X", "0", "PC loaded with NN if Sign bit set, adds 3 if clear");
        add("JMPN (BC)", "66", "X", "X", "X", "X", "X", "See Note", "X", "X", "X", "X", "X", "X", "0", "If Sign bit set, 16-bit address fetched from memory address BC and loaded into PC. If Sign bit clear, adds 1.");
        add("NOP", "67", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to address on stack, acts as NOP");
        add("NOP", "68", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "69", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "6A", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "6B", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("JSRP label", "6C", "N = label offset", "X", "X", "X", "X", "See Note", "See Note", "X", "X", "X", "X", "X", "0", "PC adds offset and return address pushed to stack if Sign bit clear, PC adds 2 and no stack change if set");
        add("JSRP NN", "6D", "NL", "NH", "X", "X", "X", "See Note", "See Note", "X", "X", "X", "X", "X", "0", "PC loaded with NN and return address pushed to stack if Sign bit set, PC adds 3 and no stack change if clear");
        add("JSRP (BC)", "6E", "X", "X", "X", "X", "X", "See Note", "See Note", "X", "X", "X", "X", "X", "0", "If Sign bit set, 16-bit address fetched from memory address BC and loaded into PC and return address pushed to stack. If Sign bit clear, PC adds 1 and no stack change.");
        add("NOP", "6F", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to address on stack, acts as NOP");
        add("NOP", "70", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "71", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "72", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "73", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("JPNC label", "74", "N = label offset", "X", "X", "X", "X", "See Note", "X", "X", "X", "X", "X", "X", "0", "PC adds offset if Carry bit clear, adds 2 if set");
        add("JMPC NN", "75", "NL", "NH", "X", "X", "X", "See Note", "X", "X", "X", "X", "X", "X", "0", "PC loaded with NN if Carry bit set, adds 3 if clear");
        add("JMPC (BC)", "76", "X", "X", "X", "X", "X", "See Note", "X", "X", "X", "X", "X", "X", "0", "If Carry bit set, 16-bit address fetched from memory address BC and loaded into PC. If Carry bit clear, adds 1.");
        add("NOP", "77", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to address on stack, acts as NOP");
        add("NOP", "78", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "79", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "7A", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("NOP", "7B", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to zero or register; acts as NOP");
        add("JSNC label", "7C", "N = label offset", "X", "X", "X", "X", "See Note", "See Note", "X", "X", "X", "X", "X", "0", "PC adds offset and return address pushed to stack if Carry bit clear, PC adds 2 and no stack change if set");
        add("JSRC NN", "7D", "NL", "NH", "X", "X", "X", "See Note", "See Note", "X", "X", "X", "X", "X", "0", "PC loaded with NN and return address pushed to stack if Sign bit set, PC adds 3 and no stack change if clear");
        add("JSRC (BC)", "7E", "X", "X", "X", "X", "X", "See Note", "See Note", "X", "X", "X", "X", "X", "0", "If Carry bit set, 16-bit address fetched from memory address BC and loaded into PC and return address pushed to stack. If Carry bit clear, PC adds 1 and no stack change.");
        add("NOP", "7F", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for jumping to address on stack, acts as NOP");
        add("ADDZ", "80", "X", "X", "A", "X", "X", "+1", "X", "A < 0", "A == 0", "0", "X", "X", "0", "Same effect as SUBZ");
        add("ADD #0", "80", "X", "X", "A", "X", "X", "+1", "X", "A < 0", "A == 0", "0", "X", "X", "0", "Same effect as SUBZ");
        add("ADD A", "81", "X", "X", "A+A", "X", "X", "+1", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADD B", "82", "X", "X", "A+B", "X", "X", "+1", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADD C", "83", "X", "X", "A+C", "X", "X", "+1", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADD #N", "84", "N", "X", "A+N", "X", "X", "+2", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADD NN", "85", "NL", "NH", "A+(NN)", "X", "X", "+3", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADD (BC)", "86", "X", "X", "A+(BC)", "X", "X", "+1", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("INC A", "87", "X", "X", "A+1", "X", "X", "+1", "X", "Sum < 0", "Sum == 0", "0", "X", "X", "0", "Binary only");
        add("ADCZ", "88", "X", "X", "A+Carry", "X", "X", "+1", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADC #0", "88", "X", "X", "A+Carry", "X", "X", "+1", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADC A", "89", "X", "X", "A+A+Carry", "X", "X", "+1", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADC B", "8A", "X", "X", "A+B+Carry", "X", "X", "+1", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADC C", "8B", "X", "X", "A+C+Carry", "X", "X", "+1", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADC #N", "8C", "N", "X", "A+N+Carry", "X", "X", "+2", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADC NN", "8D", "NL", "NH", "A+(NN)+Carry", "X", "X", "+3", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("ADC (BC)", "8E", "X", "X", "A+(BC)+Carry", "X", "X", "+1", "X", "Sum < 0", "Sum == 0", "See Note", "X", "X", "0", "If Decimal bit clear, addition is binary with carry for sum over 255. If Decimal bit set, addition is binary-coded decimal with carry for sum over 99");
        add("INC B", "8F", "X", "X", "X", "B+1", "X", "+1", "X", "Sum < 0", "Sum == 0", "0", "X", "X", "0", "Binary only");
        add("SUBZ", "90", "X", "X", "A", "X", "X", "+1", "X", "A < 0", "A == 0", "0", "X", "X", "0", "Same effect as ADDZ");
        add("SUB #0", "90", "X", "X", "A", "X", "X", "+1", "X", "A < 0", "A == 0", "0", "X", "X", "0", "Same effect as ADDZ");
        add("SUB A", "91", "X", "X", "0", "X", "X", "+1", "X", "0", "1", "0", "X", "X", "0", "");
        add("SUB B", "92", "X", "X", "A-B", "X", "X", "+1", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("SUB C", "93", "X", "X", "A-C", "X", "X", "+1", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("SUB #N", "94", "N", "X", "A-N", "X", "X", "+2", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("SUB NN", "95", "NL", "NH", "A-(NN)", "X", "X", "+3", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("SUB (BC)", "96", "X", "X", "A-(BC)", "X", "X", "+1", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("INC C", "97", "X", "X", "X", "X", "C+1", "+1", "X", "Sum < 0", "Sum == 0", "0", "X", "X", "0", "Binary only");
        add("SBCZ", "98", "X", "X", "A-Carry", "X", "X", "+1", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("SBC#0", "98", "X", "X", "A-Carry", "X", "X", "+1", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("SBC A", "99", "X", "X", "0-Carry", "X", "X", "+1", "X", "Carry", "~Carry", "X", "X", "X", "0", "Carry is preserved. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("SBC B", "9A", "X", "X", "A-B-Carry", "X", "X", "+1", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("SBC C", "9B", "X", "X", "A-C-Carry", "X", "X", "+1", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("SBC #N", "9C", "N", "X", "A-N-Carry", "X", "X", "+2", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("SBC NN", "9D", "NL", "NH", "A-(NN)-Carry", "X", "X", "+3", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("SBC (BC)", "9E", "X", "X", "A-(BC)-Carry", "X", "X", "+1", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Carry is set if borrow required. Subtraction will be decimal if Decimal bit is set, binary if clear.");
        add("DEC A", "9F", "X", "X", "A-1", "X", "X", "+1", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Binary only");
        add("NOP", "A0", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for shifting immediate value.");
        add("LSR A", "A1", "X", "X", "A >> 1", "X", "X", "+1", "X", "0", "A >> 1 == 0", "See Note", "X", "X", "0", "MSB zero-filled. Carry bit receives LSB from value before shift");
        add("LSR B", "A2", "X", "X", "X", "B >> 1", "X", "+1", "X", "0", "B >> 1 == 0", "See Note", "X", "X", "0", "MSB zero-filled. Carry bit receives LSB from value before shift");
        add("LSR C", "A3", "X", "X", "X", "X", "C >> 1", "+1", "X", "0", "C >> 1 == 0", "See Note", "X", "X", "0", "MSB zero-filled. Carry bit receives LSB from value before shift");
        add("NOP", "A4", "N", "X", "X", "X", "X", "+2", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for shifting immediate value.");
        add("LSR NN", "A5", "NL", "NH", "X", "X", "X", "+3", "X", "0", "(NN) >> 1 == 0", "See Note", "X", "X", "0", "Byte in memory shifted. MSB zero-filled. Carry bit receives LSB from value before shift");
        add("LSR (BC)", "A6", "X", "X", "X", "X", "X", "+1", "X", "0", "(BC) >> 1 == 0", "See Note", "X", "X", "0", "Byte in memory shifted. MSB zero-filled. Carry bit receives LSB from value before shift");
        add("DEC B", "A7", "X", "X", "X", "B-1", "X", "+1", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Binary only");
        add("NOP", "A8", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for shifting immediate value.");
        add("ROR A", "A9", "X", "X", "A >> 1", "X", "X", "+1", "X", "0", "A >> 1 == 0", "See Note", "X", "X", "0", "MSB filled with Carry. Carry bit receives LSB from value before shift");
        add("ROR A", "AA", "X", "X", "X", "B >> 1", "X", "+1", "X", "0", "B >> 1 == 0", "See Note", "X", "X", "0", "MSB filled with Carry. Carry bit receives LSB from value before shift");
        add("ROR A", "AB", "X", "X", "X", "X", "C >> 1", "+1", "X", "0", "C >> 1 == 0", "See Note", "X", "X", "0", "MSB filled with Carry. Carry bit receives LSB from value before shift");
        add("NOP", "AC", "N", "X", "X", "X", "X", "+2", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for shifting immediate value.");
        add("ROR NN", "AD", "NL", "NH", "X", "X", "X", "+3", "X", "0", "(NN) >> 1 == 0", "See Note", "X", "X", "0", "Byte in memory shifted. MSB filled with Carry. Carry bit receives LSB from value before shift");
        add("ROR (BC)", "AE", "X", "X", "X", "X", "X", "+1", "X", "0", "(BC) >> 1 == 0", "See Note", "X", "X", "0", "Byte in memory shifted. MSB filled with Carry. Carry bit receives LSB from value before shift");
        add("DEC C", "AF", "X", "X", "X", "X", "C-1", "+1", "X", "Diff < 0", "Diff == 0", "See Note", "X", "X", "0", "Binary only");
        add("NOP", "B0", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for shifting immediate value.");
        add("ASL A", "B1", "X", "X", "A << 1", "X", "X", "+1", "X", "0", "A << 1 == 0", "See Note", "X", "X", "0", "LSB zero-filled. Carry bit receives MSB from value before shift");
        add("ASL B", "B2", "X", "X", "X", "B << 1", "X", "+1", "X", "0", "B << 1 == 0", "See Note", "X", "X", "0", "LSB zero-filled. Carry bit receives MSB from value before shift");
        add("ASL C", "B3", "X", "X", "X", "X", "C << 1", "+1", "X", "0", "C << 1 == 0", "See Note", "X", "X", "0", "LSB zero-filled. Carry bit receives MSB from value before shift");
        add("NOP", "B4", "N", "X", "X", "X", "X", "+2", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for shifting immediate value.");
        add("ASL NN", "B5", "NL", "NH", "X", "X", "X", "+3", "X", "0", "(NN) << 1 == 0", "See Note", "X", "X", "0", "Byte in memory shifted. LSB zero-filled. Carry bit receives MSB from value before shift");
        add("ASL (BC)", "B6", "X", "X", "X", "X", "X", "+1", "X", "0", "(BC) << 1 == 0", "See Note", "X", "X", "0", "Byte in memory shifted. LSB zero-filled. Carry bit receives MSB from value before shift");
        add("NEG", "B7", "X", "X", "0-A", "X", "X", "+1", "X", "A > 0", "A == 0", "See Note", "X", "X", "0", "Binary only");
        add("NOP", "B8", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for shifting immediate value.");
        add("ROL A", "B9", "X", "X", "A << 1", "X", "X", "+1", "X", "0", "A << 1 == 0", "See Note", "X", "X", "0", "LSB filled with Carry. Carry bit receives MSB from value before shift");
        add("ROL B", "BA", "X", "X", "X", "B << 1", "X", "+1", "X", "0", "B << 1 == 0", "See Note", "X", "X", "0", "LSB filled with Carry. Carry bit receives MSB from value before shift");
        add("ROL C", "BB", "X", "X", "X", "X", "C << 1", "+1", "X", "0", "C << 1 == 0", "See Note", "X", "X", "0", "LSB filled with Carry. Carry bit receives MSB from value before shift");
        add("NOP", "BC", "N", "X", "X", "X", "X", "+2", "X", "X", "X", "X", "X", "X", "0", "Illegal opcode for shifting immediate value.");
        add("ROL NN", "BD", "NL", "NH", "X", "X", "X", "+3", "X", "0", "(NN) << 1 == 0", "See Note", "X", "X", "0", "Byte in memory shifted. LSB filled with Carry. Carry bit receives MSB from value before shift");
        add("ROL (BC)", "BE", "X", "X", "X", "X", "X", "+1", "X", "0", "(BC) << 1 == 0", "See Note", "X", "X", "0", "Byte in memory shifted. LSB filled with Carry. Carry bit receives MSB from value before shift");
        add("INC BC", "BF", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
        add("ORZ", "C0", "X", "X", "X", "X", "X", "+1", "X", "A < 0", "A == 0", "X", "X", "X", "0", "");
        add("ORA #0", "C0", "X", "X", "X", "X", "X", "+1", "X", "A < 0", "A == 0", "X", "X", "X", "0", "");
        add("ORA A", "C1", "X", "X", "X", "X", "X", "+1", "X", "A < 0", "A == 0", "X", "X", "X", "0", "");
        add("ORA B", "C2", "X", "X", "A | B", "X", "X", "+1", "X", "A | B < 0", "A == B == 0", "X", "X", "X", "0", "");
        add("ORA C", "C3", "X", "X", "A | C", "X", "X", "+1", "X", "A | C < 0", "A == C == 0", "X", "X", "X", "0", "");
        add("ORA #N", "C4", "N", "X", "A | N", "X", "X", "+2", "X", "A | N < 0", "A == N == 0", "X", "X", "X", "0", "");
        add("ORA NN", "C5", "NL", "NH", "A | (NN)", "X", "X", "+3", "X", "A | (NN) < 0", "A == (NN) == 0", "X", "X", "X", "0", "");
        add("ORA (BC)", "C6", "X", "X", "A | (BC)", "X", "X", "+1", "X", "A | (BC) < 0", "A == (BC) == 0", "X", "X", "X", "0", "");
        add("INV B", "C7", "X", "X", "X", "~B", "X", "+1", "X", "B >= 0", "B == 255", "X", "X", "X", "0", "");
        add("NORZ", "C8", "X", "X", "~A", "X", "X", "+1", "X", "A >= 0", "A == 255", "X", "X", "X", "0", "");
        add("NOR #0", "C8", "X", "X", "~A", "X", "X", "+1", "X", "A >= 0", "A == 255", "X", "X", "X", "0", "");
        add("NOR A", "C9", "X", "X", "~A", "X", "X", "+1", "X", "A >= 0", "A == 255", "X", "X", "X", "0", "");
        add("NOR B", "CA", "X", "X", "~(A | B)", "X", "X", "+1", "X", "~(A | B) < 0", "~(A | B) == 0", "X", "X", "X", "0", "");
        add("NOR C", "CB", "X", "X", "~(A | C)", "X", "X", "+1", "X", "~(A | C) < 0", "~(A | C) == 0", "X", "X", "X", "0", "");
        add("NOR #N", "CC", "N", "X", "~(A | N)", "X", "X", "+2", "X", "~(A | N) < 0", "~(A | N) == 0", "X", "X", "X", "0", "");
        add("NOR NN", "CD", "NL", "NH", "~(A | (NN))", "X", "X", "+3", "X", "~(A | (NN)) < 0", "~(A | (NN)) == 0", "X", "X", "X", "0", "");
        add("NOR (BC)", "CE", "X", "X", "~(A | (NN))", "X", "X", "+1", "X", "~(A | (NN)) < 0", "~(A | (NN)) == 0", "X", "X", "X", "0", "");
        add("INV C", "CF", "X", "X", "X", "X", "~C", "+1", "X", "C >= 0", "C == 255", "X", "X", "X", "0", "");
        add("ANDZ", "D0", "X", "X", "0", "X", "X", "+1", "X", "0", "1", "X", "X", "X", "0", "");
        add("AND #0", "D0", "X", "X", "0", "X", "X", "+1", "X", "0", "1", "X", "X", "X", "0", "");
        add("AND A", "D1", "X", "X", "X", "X", "X", "+1", "X", "A < 0", "A == 0", "X", "X", "X", "0", "");
        add("AND B", "D2", "X", "X", "A & B", "X", "X", "+1", "X", "(A & B) < 0", "(A & B) == 0", "X", "X", "X", "0", "");
        add("AND C", "D3", "X", "X", "A & C", "X", "X", "+1", "X", "(A & C) < 0", "(A & C) == 0", "X", "X", "X", "0", "");
        add("AND #N", "D4", "N", "X", "A & N", "X", "X", "+2", "X", "(A & N) < 0", "(A & N) == 0", "X", "X", "X", "0", "");
        add("AND NN", "D5", "NL", "NH", "A & (NN)", "X", "X", "+3", "X", "A & (NN) < 0", "A & (NN) == 0", "X", "X", "X", "0", "");
        add("AND (BC)", "D6", "X", "X", "A & (BC)", "X", "X", "+1", "X", "A & (BC) < 0", "A & (BC) == 0", "X", "X", "X", "0", "");
        add("SED", "D7", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "1", "0", "");
        add("NNDZ", "D8", "X", "X", "255", "X", "X", "+1", "X", "1", "0", "X", "X", "X", "0", "");
        add("NND #0", "D8", "X", "X", "255", "X", "X", "+1", "X", "1", "0", "X", "X", "X", "0", "");
        add("NND A", "D9", "X", "X", "~A", "X", "X", "+1", "X", "A >= 0", "A == 255", "X", "X", "X", "0", "");
        add("NND B", "DA", "X", "X", "~(A & B)", "X", "X", "+1", "X", "~(A & B) < 0", "~(A & B) == 0", "X", "X", "X", "0", "");
        add("NND C", "DB", "X", "X", "~(A & C)", "X", "X", "+1", "X", "~(A & C) < 0", "~(A & C) == 0", "X", "X", "X", "0", "");
        add("NND #N", "DC", "N", "X", "~(A & N)", "X", "X", "+2", "X", "~(A & N) < 0", "~(A & N) == 0", "X", "X", "X", "0", "");
        add("NND NN", "DD", "NL", "NH", "~(A & (NN))", "X", "X", "+3", "X", "~(A & (NN)) < 0", "~(A & (NN)) == 0", "X", "X", "X", "0", "");
        add("NND (BC)", "DE", "X", "X", "~(A & (BC))", "X", "X", "+1", "X", "~(A & (BC)) < 0", "~(A & (BC)) == 0", "X", "X", "X", "0", "");
        add("CLD", "DF", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "0", "0", "");
        add("XORZ", "E0", "X", "X", "X", "X", "X", "+1", "X", "A < 0", "A == 0", "X", "X", "X", "0", "");
        add("XOR #0", "E0", "X", "X", "X", "X", "X", "+1", "X", "A < 0", "A == 0", "X", "X", "X", "0", "");
        add("XOR A", "E1", "X", "X", "0", "X", "X", "+1", "X", "0", "1", "X", "X", "X", "0", "");
        add("XOR B", "E2", "X", "X", "A ^ B", "X", "X", "+1", "X", "A ^ B < 0", "A ^ B == 0", "X", "X", "X", "0", "");
        add("XOR C", "E3", "X", "X", "A ^ C", "X", "X", "+1", "X", "A ^ C < 0", "A ^ C == 0", "X", "X", "X", "0", "");
        add("XOR #N", "E4", "N", "X", "A ^ N", "X", "X", "+2", "X", "A ^ N < 0", "A ^ N == 0", "X", "X", "X", "0", "");
        add("XOR NN", "E5", "NL", "NH", "A ^ (NN)", "X", "X", "+3", "X", "A ^ (NN) < 0", "A ^ (NN) == 0", "X", "X", "X", "0", "");
        add("XOR (BC)", "E6", "X", "X", "A ^ (BC)", "X", "X", "+1", "X", "A ^ (BC) < 0", "A ^ (BC) == 0", "X", "X", "X", "0", "");
        add("PHP", "E7", "X", "X", "X", "X", "X", "+1", "-1", "X", "X", "X", "X", "X", "0", "Status Register P (SZCIDKNOP) pushed to stack");
        add("XNRZ", "E8", "X", "X", "~A", "X", "X", "+1", "X", "A >= 0", "A == 255", "X", "X", "X", "0", "");
        add("XNR #0", "E8", "X", "X", "~A", "X", "X", "+1", "X", "A >= 0", "A == 255", "X", "X", "X", "0", "");
        add("XNR A", "E9", "X", "X", "255", "X", "X", "+1", "X", "1", "0", "X", "X", "X", "0", "");
        add("XNR B", "EA", "X", "X", "~(A ^ B)", "X", "X", "+1", "X", "~(A ^ B) < 0", "~(A ^ B) == 0", "X", "X", "X", "0", "");
        add("XNR C", "EB", "X", "X", "~(A ^ C)", "X", "X", "+1", "X", "~(A ^ C) < 0", "~(A ^ C) == 0", "X", "X", "X", "0", "");
        add("XNR #N", "EC", "N", "X", "~(A ^ N)", "X", "X", "+2", "X", "~(A ^ N) < 0", "~(A ^ N) == 0", "X", "X", "X", "0", "");
        add("XNR NN", "ED", "NL", "NH", "~(A ^ (NN))", "X", "X", "+3", "X", "~(A ^ (NN)) < 0", "~(A ^ (NN)) == 0", "X", "X", "X", "0", "");
        add("XNR (BC)", "EE", "X", "X", "~(A ^ (BC))", "X", "X", "+1", "X", "~(A ^ (BC)) < 0", "~(A ^ (BC)) == 0", "X", "X", "X", "0", "");
        add("PLP", "EF", "X", "X", "X", "X", "X", "+1", "+1", "(SP) bit 7", "(SP) bit 6", "(SP) bit 5", "(SP) bit 4", "(SP) bit 3", "(SP) bit 2", "");
        add("SEI", "F0", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "1", "X", "0", "");
        add("CLI", "F1", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "0", "X", "0", "");
        add("RTS", "F2", "X", "X", "X", "X", "X", "(SP)", "+2", "X", "X", "X", "X", "X", "0", "");
        add("RTI", "F3", "X", "X", "(SP+2)", "(SP+1)", "(SP)", "(SP+4)", "+6", "(SP+3) bit 7", "(SP+3) bit 6", "(SP+3) bit 5", "(SP+3) bit 4", "(SP+3) bit 3", "(SP+3) bit 2", "");
        add("HLT", "F4", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "");
        add("BRK", "F5", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "1", "");
        add("RST", "F6", "X", "X", "X", "X", "X", "(RESET)", "X", "X", "X", "X", "X", "X", "0", "Execution immediately jumps to address in RESET vector at $FFFE");
        add("LDS NN", "F7", "NL", "NH", "X", "X", "X", "+3", "NN", "X", "X", "X", "X", "X", "0", "");
        add("LDS BC", "F8", "X", "X", "X", "X", "X", "+1", "BC", "X", "X", "X", "X", "X", "0", "");
        add("LDS (BC)", "F9", "X", "X", "X", "X", "X", "+1", "(BC)", "X", "X", "X", "X", "X", "0", "16-Bit address fetched from memory at address BC");
        add("LBCS", "FA", "X", "X", "X", "SPH", "SPL", "+1", "X", "X", "X", "X", "X", "X", "0", "SP read into BC, but not changed");
        add("CLC", "FB", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "0", "X", "X", "0", "");
        add("SEC", "FC", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "1", "X", "X", "0", "");
        add("INC BC", "FD", "X", "X", "X", "See Note", "C+1", "+1", "X", "X", "BC == 0", "X", "X", "X", "0", "Binary only. BC = BC+1, so B is incremented if C == 255");
        add("DEC BC", "FE", "", "", "", "", "", "", "", "", "", "", "", "", "", "");
        add("NOP", "FF", "X", "X", "X", "X", "X", "+1", "X", "X", "X", "X", "X", "X", "0", "For future expansion, currently NOP");


        //Map<String, String> phoneBook =
        byByteCode = opcodes.stream()
                .collect(Collectors.toMap(OpCode::getOpcode, o->o, (a1, a2) -> a1));

        /*
            .collect(
                groupingBy(OpCode::getOpcode, mapping(OpCode::getOpcode, toList()));
//            .collect(groupingBy(OpCode::getOpcode, mapping(opcode -> opcode, toList())))
            ;
            //.collect(toMap(OpCode::getOpcode, o -> o));
*/
        byFirstWord = opcodes.stream()
            .collect(groupingBy(o -> o.getMnemonic().split(" ")[0]));
    }

    public static OpCode fromTokens(String first, String second) {
        return fromSecond(findMatches(first), second);
    }

    public static List<OpCode> findMatches(String first) {
        List<OpCode> matches = byFirstWord.get(first);
        return matches == null ? Collections.emptyList() : matches;
    }

    public static OpCode fromSecond(List<OpCode> firstMatches, String second) {
        String target = getPrefix(second);
        return firstMatches.stream()
            .filter(o -> target.equals(o.getMnemonic().split(" ")[1]))
            .findFirst()
            .orElse(null);
    }

    private static String getPrefix(String second) {
        if (second.equals("A")
            || second.equals("B")
            || second.equals("C")
            || second.equals("SP")
            || second.equals("PC")
            || second.equals("(BC)") )
            return second;

        if (second.startsWith("#0"))
            return "#0";
        else if ((second.startsWith("#") && second.length() <= 3 )   // #F, #1F, but not #3FA
              || (second.startsWith("#$") && second.length() <= 4))  // #$FF
            return "#N";
        else if (second.startsWith("#") || second.startsWith("$") || isNumber(second))  // #FF00, $FF00, 0xFFFF
            return "NN";
        else
            return "NN";
    }
}
