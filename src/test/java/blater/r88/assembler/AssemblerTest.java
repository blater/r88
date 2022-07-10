package blater.r88.assembler;

import blater.r88.Log;
import blater.r88.machine.Memory;
import org.junit.jupiter.api.Test;

class AssemblerTest {
    @Test
    void read() {
        Log.level = Log.DEBUG;
        String file = "asmtest1.asm";
        Assembler asm = new Assembler();
        Memory memory = asm.read(file);
        memory.disassemble(16384, 30);
    }
}