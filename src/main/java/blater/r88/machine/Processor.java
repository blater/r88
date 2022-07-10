package blater.r88.machine;

import blater.r88.machine.cpu.FixedVectorType;
import blater.r88.machine.cpu.OpCode;
import blater.r88.machine.cpu.OpCodes;
import lombok.Getter;

@Getter
public class Processor {
    private final Registers registers = new Registers();
    private final Memory memory = new Memory();

    public Processor() {
        reset();
    }

    public void reset() {
        memory.reset();
        // at power on the RESET vector will be fetched and execution will begin from there
        registers.PC = FixedVectorType.RESET.getAddr();
        run();
    }

    public void run() {
        boolean running = true;
        while (running) {
            runTimeSlice();
            running = runInterrupt();
        }
    }

    public void runTimeSlice() {
        int slice = 2;
        while (slice-- > 0) {
            execute();
        }
    }

    private boolean runInterrupt() {
        pushWord(registers.PC);
        registers.PC = getWordFrom(FixedVectorType.IRQ.getAddr());
        return true;
    }

    private void pushWord(int value) {
        short hb= (short) (value/256);
        short lb = (short) (value-hb);
        push(lb);
        push(hb);
    }

    private int popWord() {
        short lb= pop();
        short hb = pop();
        return (hb * 256) + lb;
    }

    private void push(short value) {
        memory.poke(registers.SP--, value);
    }

    private short pop() {
        return memory.peek(registers.SP++);
    }

    private void execute() {
        short val = memory.peek(registers.PC++);
        OpCode opcode = OpCodes.of(val);
        Short arg1 = getArg1(opcode);
        Short arg2 = getArg2(opcode);

        runOpcode(opcode, arg1, arg2);
    }

    private void runOpcode(OpCode opcode, Short arg1, Short arg2) {
        if (opcode.hasAResult())
            registers.A = applyOperationToRegister(opcode, arg1, arg2);
        if (opcode.hasBResult())
            registers.B = applyOperationToRegister(opcode, arg1, arg2);
        if (opcode.hasCResult())
            registers.C = applyOperationToRegister(opcode, arg1, arg2);
    }

    private short applyOperationToRegister(OpCode opcode, Short arg1, Short arg2) {
        short ret = 0;
        switch (opcode.getAResult()) {
            case "0" -> ret = loadByteIntoRegister((short) 0);
            case "A" -> {
            }
            case "B" -> ret = loadByteIntoRegister(registers.B);
            case "C" -> ret = loadByteIntoRegister(registers.C);
            case "N" -> ret = loadByteIntoRegister(arg1);
            case "(NN)" -> ret = loadByteIntoRegister(memory.peek(addressOf(arg1, arg2)));
            case "(BC)" -> ret = loadByteIntoRegister(memory.peek(addressOf(registers.C, registers.B)));
            case "(SP)" -> ret = loadByteIntoRegister(memory.peek(registers.SP--));

            case "(A+A)" -> ret = addByte(registers.A, registers.A);
            case "(A+B)" -> ret = addByte(registers.A, registers.B);
            case "(A+C)" -> ret = addByte(registers.A, registers.C);
        }
        return ret;
    }

    /*
    private void setAResult(OpCode opcode, Short arg1, Short arg2) {
        switch (opcode.getAResult()) {
            case "0" -> registers.A = loadByte(0);
            case "A" -> { }
            case "B" -> registers.A = loadByte(registers.B);
            case "C" -> registers.A = loadByte(registers.C);
            case "N" -> registers.A = loadByte(arg1);
            case "(NN)" -> registers.A = loadByte(memory.peek(toAddress(arg1, arg2)));
            case "(BC)" -> registers.A = loadByte(memory.peek(toAddress(registers.C, registers.B)));
            case "(SP)" -> registers.A = loadByte(memory.peek(registers.SP--));

            case "(A+A)" -> registers.A = addByte(registers.A, registers.A);
            case "(A+B)" -> registers.A = addByte(registers.A, registers.B);
            case "(A+C)" -> registers.A = addByte(registers.A, registers.C);

        }
    }
     */

    private int addressOf(short lb, short hb) {
        return hb * 256 + lb;
    }

    private short loadByteIntoRegister(short val) {
        registers.sign = val > 127;
        registers.zero = val == 0;
        return val;
    }

    private int getWordFrom(int address) {
        return addressOf(memory.peek(address), memory.peek(address+1));
    }

    private short addByte(short a, short b) {
        if (a > 255) a = 255;
        if (b > 255) b = 255;
        int r = a + b;
        if (r > 255) {
            r = r - 255;
            registers.carry = true;
        } else {
            registers.carry = false;
        }
        return (short) r;
    }

    Short getArg1(OpCode opcode) {
        return opcode.hasArg1()
            ? memory.peek(registers.PC++)
            : null;
    }

    Short getArg2(OpCode opcode) {
        return opcode.isWordParameter()
            ? memory.peek(registers.PC++)
            : null;
    }
}
