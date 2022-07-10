package blater.r88.assembler;

import blater.r88.machine.cpu.OpCode;
import blater.r88.machine.cpu.OpCodes;
import blater.r88.machine.Memory;
import lombok.SneakyThrows;

import java.util.List;

import static blater.r88.Log.log;

// input a file of asm instructions
// output a memory dump of assembled opcodes.
public class Assembler {
    Labels labels = new Labels();
    Memory memory = new Memory();
    static int line = 0;

    public Memory read(String filename) {
//        labelMap = firstPass(filename);
        memory = compilePass(filename);
        return memory;
    }

    //              jmp end                   >   jmp 0000     labels.end.resolvelist += (pc, NN);
    // label start:                start, -1  >                labels.start.address := pc
    //              lda a                     >   ldaa
    //              jmpnz 555                 >   jmpnz 555
    // label end:                  end, -2    >                labels.end.address == pc.   resolve(labels.end, memory); delete labels.end.resolvelist
    //              return;                   >   ret
    //public Map<String, Label> firstPass(String filename) {
    //    Tokenizer tokenizer = new Tokenizer(filename);
    //    while (tokenizer.hasMoreTokens()) {
    //        tokenizer.readNext();
    //        Optional<Label> label = Label.parseLabelDeclaration(tokenizer);
    //        label.ifPresent(l -> labelMap.put(l.getName(), l));
    //    }
    //    tokenizer.close();
    //    return labelMap;
    //}

    @SneakyThrows
    public Memory compilePass(String filename) {
        int pc = 0;
        Tokenizer tokenizer = new Tokenizer(filename);
        Token token = tokenizer.readNext();

        while (tokenizer.hasMoreTokens()) {
            log(">>token [" + token.getValue() + "] [" + tokenizer.getTokenizer().ttype + "]");

            if (Label.isLabel(token)) {
                Label label = labels.addLabel(tokenizer, pc);
                label.resolveForwardReferences(memory, pc, line);
                log("   >label [" + label.getName() + " at " + pc + "]");

            } else if (isOrg(token)) {
                Token newPc = tokenizer.readNext();
                pc = newPc.toWord();
                log("   >org  [pc is now:" + pc + "]" );

            } else if (isData(token)) {
                pc = Data.readDataBlock(memory, pc, tokenizer);
                log("   >data [inserting at: " + pc +"]");

            } else {
                OpCode opcode;
                Token token2;
                List<OpCode> matches = findMatches(token);
                if (matches.size() == 1) {
                    opcode = matches.get(0);
                    token2 = opcode.hasArg1() ? tokenizer.readNext() : new Token("");
                } else {
                    token2 = tokenizer.readNext();
                    opcode = refineMatches(token2, matches);
                }

                // found the opcode.
                log("   >opcode [" + opcode.getMnemonic() + " at:" + pc + "]");
                pc += memory.poke(pc, opcode.getOpcode());

                if (opcode.hasParameters()) {
                    // if the parameter is a label then resolve it to a value if we can.
                    // if we cant then "tryToResolve" will add it to the label as an unresolved reference
                    if (labels.isLabel(token2)
                    || (!token2.isRegister() && !token2.isNumber())) {
                        token2.setToLabelValue(labels.tryToResolve(token2, pc));
                    }

                    // the arg is a literal number of some sort
                    if (opcode.isWordParameter()) {
                        // 16 bit word expected
                        log("   >literal word arg [" + token2.getValue() + " at:" + pc + "]");
                        pc += memory.pokeWord(pc, token2.toWord());
                    } else if (opcode.isByteOffsetParameter() || token2.isByteOffset()) {
                        // expect 1 byte 2's complement offset
                        pc += memory.poke(pc, token2.toByteOffsetValue(pc));
                        log("   >byte offset arg [" + token2.getValue() + " at:" + pc + "]");
                    } else {
                        log("   >literal byte arge [" + token2.getValue() + "] put at:" + pc);
                        pc += memory.poke(pc, (short) token2.toWord());
                    }
                }
            }
            token = tokenizer.readNext();
            line++;
        }
        return memory;
    }

    private OpCode refineMatches(Token token2, List<OpCode> matches) {
        OpCode match = OpCodes.fromSecond(matches, token2.getValue());
        if (match == null)
            Error.syntaxError(token2.getValue());
        return match;
    }

    private List<OpCode> findMatches(Token token) {
        List<OpCode> matches = OpCodes.findMatches(token.getValue());
        if (matches.size() == 0)
            Error.syntaxError(token.getValue());
        return matches;
    }

    private boolean isOrg(Token token) {
        return token.getValue().equals("ADDRESS") || token.getValue().equals("ORG");
    }

    private boolean isData(Token token) {
        return token.getValue().equals("DATA");
    }
}