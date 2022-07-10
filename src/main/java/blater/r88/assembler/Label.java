package blater.r88.assembler;

import blater.r88.machine.cpu.OpCode;
import blater.r88.machine.cpu.OpCodes;
import blater.r88.machine.Memory;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Data
public class Label {
    String name;
    Integer value;

    List<Integer> needsResolving = new ArrayList<>();

    private static final String LABEL_KEYWORD = "LABEL";
    private static final String LABEL_SUFFIX = ":";

    public Label(String name) {
        this.name = name;
    }

    public static boolean isLabel(Token tkn) {
        return (tkn.getValue().endsWith(LABEL_SUFFIX)
            || tkn.getValue().equals(LABEL_KEYWORD));
    }

    public static Optional<Label> parseLabelDeclaration(Tokenizer tokenizer) {
        Token tkn = tokenizer.getLastToken();
        if (tkn.getValue().endsWith(LABEL_SUFFIX))
            return of(new Label(tkn.valueWithoutSuffix()));
        else if (tkn.getValue().equals(LABEL_KEYWORD))
            return of(new Label(tokenizer.readNext().getValue()));
        else
            return empty();

    }

    public void resolveForwardReferences(Memory memory, int pc, int line) {
        //   resolve any forward references
        for (int resolveAt : getNeedsResolving()) {
            OpCode opcodeToResolveFor = OpCodes.of(memory.peek(resolveAt));
            if (opcodeToResolveFor.isWordParameter()) {
                // address is 16 bit word
                memory.pokeWord(resolveAt + 1, pc);
            } else {
                // address is 8 byte 2's compliment offset
                int offset = resolveAt - pc;
                //            0        - 200   = -200
                //           100       - 0     = +100
                if (offset > 127 || offset < -127) {
                    Error.badOffset(OpCodes.of(memory.peek(resolveAt)).getMnemonic() + " : offset is " + offset + " bytes");
                } else {
                    // 2's complement
                    if (offset < 0)
                        offset = offset + 255;
                }
                memory.poke(resolveAt + 1, (short) offset);
            }
        }
        getNeedsResolving().clear();
    }
}
