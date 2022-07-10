package blater.r88.machine;

import blater.r88.machine.cpu.OpCode;
import blater.r88.machine.cpu.OpCodes;

public class Memory {
    public static final int MAX_MEM = 65535;
    private final short[] memory = new short[MAX_MEM];

    public short[] copyBlock(int startAddress, int len) {
        short[] block = new short[len];
        int i=0;
        for (int n = startAddress; n<= startAddress +len; n++, i++)
           block[n] = memory[n];
        return block;
    }

    public void putBlock(int address, short[] bytes) {
        int i=0;
        for (int n = address; n<=bytes.length; n++, i++)
            poke(n, bytes[i]);
    }

    public void disassemble(int from, int len) {
        int skip = 0;
        for (int i = from; i < from + len; i++) {
            short b = peek(i);
            short a1 = 0;
            OpCode code = null;
            Integer argval = null;
            if (skip == 0) {
                code = OpCodes.of(b);
                if (code.hasArg1()) {
                    a1 = peek(i + 1);
                    if (!code.isWordParameter())
                        argval = (int) a1;
                    skip = 1;
                }
                if (code.isWordParameter()) {
                    short a2 = peek(i + 2);
                    argval = (a2 * 256) + a1;
                    skip = 2;
                }
            } else {
                skip--;
            }

            System.err.printf("? $%-5s : $%-5s : %-14s : %3s : %n", //%30s %n",
                Integer.toHexString(i),
                Integer.toHexString(b),
                code != null ? code.getMnemonic() + (argval != null ? " $" + Integer.toHexString(argval) : "") : "",
                (char) b//,
                //code != null ? code.getNotes() : ""
            );
        }
    }

    public void reset() {
        for (int n = 0; n <= MAX_MEM; n++)
            memory[n] = Byte.MIN_VALUE;

    }

    public short poke(int address, short value) {
        memory[address] = value;
        return 1;
    }

    public short pokeWord(int address, int word) {
        short high = (short) (word / 256);
        short low = (short) (word - (high*256));
        memory[address] = low;
        memory[address + 1] = high;
        return 2;
    }

    public short peek(int address) {
        if (address <= MAX_MEM)
            return memory[address];
        else
            return 0;
    }

    public int peekWord(int address) {
        int low = memory[address];
        int high = memory[address + 1];
        return (high * 256) + low;
    }
}
