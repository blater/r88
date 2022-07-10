package blater.r88.assembler;

public class Error {

    public static void badDirective(int line, String token) {
        error("Invalid compiler directive", line, token);
    }

    public static void syntaxError(String token) {
        error("Syntax error", Assembler.line, token);
    }

    public static void fileError(String file) {
        System.err.println("cant find file " + file);
        System.exit(1);
    }

    public static void tokenReadError(int lineno, Exception e) {
        error("Token failed to read error", lineno, null);
    }

    private static void error(String type, int line, String val) {
        System.err.print(type + " at line " + line);
        if (val != null)
            System.err.print(", near '" + val + "'");
        System.err.println();
        System.exit(1);
    }

    public static void badOffset(String opcodeArg1) {
        error("1 byte offset to label must be <127 or > -127 ", Assembler.line, "label '"+opcodeArg1+"'");
    }

    public static void numberExpected(String message, String token) {
        error ("number expected. " + message, Assembler.line, token);
    }

    public static void cannotResolveLabel(String token) {
        error ("unexpectedly cannot resolve label. ", Assembler.line, token);
    }
}
