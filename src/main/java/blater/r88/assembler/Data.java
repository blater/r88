package blater.r88.assembler;

import blater.r88.machine.Memory;

import static blater.r88.Log.log;

public class Data {

    public static int readDataBlock(Memory memory, int pc, Tokenizer tokenizer) {
        // read block of data until we see a semicolon
        Token data = tokenizer.readNext();
        return data.isQuotedStringStart()
            ? readStringDataIntoMemory(memory, pc, tokenizer)
            : readHexDataIntoMemory(memory, pc, tokenizer);
    }

    // keep pushing chars into mem until we see ";
    public static int readStringDataIntoMemory(Memory memory, int pc, Tokenizer tokenizer) {
        Token token = tokenizer.getLastToken();
        do {
            pc = storeString(memory, token, pc);
            if (!token.isQuotedStringEnd())
                token = tokenizer.readNext();
        } while (!token.isQuotedStringEnd());
        return pc;
    }

    public static int storeString(Memory memory, Token token, int pc) {
        log(">> storeString [" + token + "] into " + pc);
        for (char c : token.getValue().toCharArray()) {
            if (c == '~')
                c = ' ';
            if (c != '"')
                memory.poke(pc++, (short) c);
        }
        return pc;
    }

    public static int readHexDataIntoMemory(Memory memory, int pc, Tokenizer tokenizer) {
        // todo
        return pc;
    }

}
