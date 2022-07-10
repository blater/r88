package blater.r88.devices;

import blater.r88.machine.Memory;

public class MemoryHelper {
    public static int toWord(int hb, int lb) {
        return (hb * 256) + lb;
    }
    public static String readString(Memory memory, int addr, int maxlen) {
        StringBuilder sb = new StringBuilder(maxlen);
        for (int n=addr; n<=addr+maxlen; n++) {
            char c = (char) memory.peek(n);
            if (c == 0)
                break;
            sb.append(memory.peek(n));
        }
        return sb.toString();
    }
}
