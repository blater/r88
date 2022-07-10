package blater.r88.machine.cpu;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OpCode {
    String mnemonic;
    short opcode;
    String arg1;
    String arg2;
    String aResult;
    String bResult;
    String cResult;
    String pcResult;
    String spResult;
    String signResult;
    String zeroResult;
    String carryResult;
    String intResult;
    String decResult;
    String breakResult;
    String notes;

    public boolean hasParameters() {
        return !arg1.isEmpty();
    }

    public boolean hasArg1() {
        return !arg1.isEmpty();
    }

    public boolean isWordParameter() {
        return !arg2.isEmpty();
    }

    public  boolean hasAResult() {
        return !aResult.isEmpty();
    }
    public  boolean hasBResult() {
        return !bResult.isEmpty();
    }
    public  boolean hasCResult() {
        return !cResult.isEmpty();
    }

    public static OpCode of(
        String mnemonic, short opcode, String arg1, String arg2, String aResult,
        String bResult, String cResult, String pcResult, String spResult, String signResult,
        String zeroResult, String carryResult, String intResult, String decResult, String breakResult,
        String notes) {
        return OpCode.builder()
            .mnemonic(mnemonic)
            .opcode(opcode)
            .arg1(cnv(arg1))
            .arg2(cnv(arg2))
            .aResult(cnv(aResult))
            .bResult(cnv(bResult))
            .cResult(cnv(cResult))
            .pcResult(cnv(pcResult))
            .spResult(cnv(spResult))
            .signResult(cnv(signResult))
            .zeroResult(cnv(zeroResult))
            .carryResult(cnv(carryResult))
            .intResult(cnv(intResult))
            .decResult(cnv(decResult))
            .breakResult(cnv(breakResult))
            .notes(cnv(notes))
            .build();
    }

    private static String cnv(String val) {
        return val.equals("X") ? "" : val;
    }

    public boolean isByteOffsetParameter() {
        return getArg1().equals("N = label offset");
    }
}
