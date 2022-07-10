package blater.r88;


import blater.r88.assembler.Assembler;
import blater.r88.machine.Memory;
import org.apache.commons.cli.*;

public class Main {
    static CommandLineParser parser = new DefaultParser();
    static Options o = setOptions();
    static CommandLine cmd;
    private static void usage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("cmd", o);
        System.exit(1);
    }

    private static Options setOptions() {
        Options o = new Options();
        o.addOption(Option.builder("v")
            .longOpt("debug").desc("verbose / debug mode").build());
        o.addOption(Option.builder("d")
            .longOpt("disassemble").desc("disassemble the given core file").build());
        o.addOption(Option.builder("a")
            .longOpt("assemble").desc("assemble the given .asm file").build());
        o.addOption(Option.builder("l")
            .longOpt("load").desc("load the given core file").build());
        o.addOption(Option.builder("r")
            .longOpt("run").desc("run the given core file").build());
        o.addOption(Option.builder("x")
            .longOpt("justdoit").desc("assemble/load/run the given asm file").build());
        return o;
    }

    public static void main(String[] args) {
        try {
            cmd = parser.parse(o, args);
        } catch (ParseException e) {
            usage();
        }

        if (cmd.getArgList().size() < 1) {
            usage();
        }
        String file = cmd.getArgList().get(0);

        Memory memory = new Assembler().read(file);
        memory.disassemble(16384, 40);
    }

}
