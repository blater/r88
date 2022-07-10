package blater.r88.assembler;

import blater.r88.machine.cpu.OpCode;
import lombok.Data;
import org.apache.commons.lang3.math.NumberUtils;

@Data
public class Token {
    private static final String QUOTE = "\"";

    private String value;
    private int ival;

    private OpCode opcode;

    private boolean isOpcode;
    private boolean isRegister;
    private boolean isByte;
    private boolean isWord;

    private boolean isLabelAddress = false;

    public Token(String val) {
        setValue(val);
    }

    public void setToLabelValue(String val) {
        setValue(val);
        isLabelAddress = true;
    }

    private void setValue(String val) {
        value = val == null ? "" : val;
    }

    public boolean isRegister() {
        return value.equals("A")
            || value.equals("B")
            || value.equals("C")
            || value.equals("SP")
            || value.equals("PC")
            || value.equals("(BC)");
    }

    public String valueWithoutSuffix() {
        return withoutSuffix(value);
    }

    private String withoutSuffix(String value) {
        return value == null || value.length() < 2
            ? value
            : value.endsWith(":") ? value.substring(0, value.length() - 2) : value;
    }

    public String valueWithoutPrefix() {
        return withoutHashPrefix(value);
    }

    private String withoutHashPrefix(String value) {
        return (value != null && value.length() > 0 && value.startsWith("#")) ? value.substring(1) : value;
    }

    private String toStandardHexPrefix(String value) {
        return value.startsWith("$") ? "0x" + value.substring(1) : value;
    }

    public int toWord() {
        if (value == null || value.equals(""))
            return 0;

        String val = toStandardHexPrefix(withoutHashPrefix(withoutSuffix(value)));
        int number = 0;
        try {
            number = NumberUtils.createInteger(val);
        } catch (NumberFormatException e) {
            Error.numberExpected(e.getMessage(), value);
        }
        return number;
    }

    public boolean isQuotedStringStart() {
        return value.startsWith(QUOTE);
    }

    public boolean isQuotedStringEnd() {
        return value.endsWith(QUOTE);
    }

    private boolean isLiteralByteArg() {
        boolean isByte = false;
        if (value.startsWith("#")                                 // it's a properly prefixed literal byte
            || (value.startsWith("$") && value.length() <= 3)    // unprefixed literal, but has $ hex prefix and <= FF
            || (value.startsWith("0X") && value.length() <= 4)  // unprefixed literal, but has 0x hex prefix and <= FF
            || (value.startsWith("0B") && value.length() <= 10)) // unprefixed literal, but has binary prefix and <= 8 bits
        {
            isByte = true;
        } else if (NumberUtils.isNumber(value)) {
            // It's a decimal numbrer with no literal prefix.  Check if it will fit into one byte.
            int number = NumberUtils.createInteger(value);
            if (number >= -127 && number <= 255) // a negative will be 2's-complimented later on.
                isByte = true;
        }
        return isByte;
    }

    public boolean isByteOffset() {
        return value.startsWith("#");
    }

    public short toByteOffsetValue(int pc) {
        int offset = 0;
        int intValue = toWord();

        if (isLabelAddress)
            //   label is an absolute address, so calculate offset.
            offset = intValue - pc;
        else
            //   literal value is already an offset
            offset = intValue;

        if (offset > 127 || offset < -128)
            Error.badOffset(value);

        // 2's complement if negative
        if (offset < 0)
            offset = 255 + offset;

        return (short) offset;
    }

    public boolean isNumber() {
        if (value == null || value.length() == 0)
            return false;
        else
            return value.startsWith("#")
                || value.startsWith("$")
                || value.startsWith("0X")
                || value.startsWith("0B")
                || Character.isDigit(value.charAt(0))
                ;
    }
}
