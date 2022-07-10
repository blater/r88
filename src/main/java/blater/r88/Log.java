package blater.r88;

import lombok.Data;

@Data
public class Log {
    public static final int OFF = 0;
    public static final int DEBUG = 1;
    public static byte level = OFF;
    public static void log(String s) {
        if (level  > OFF)
            System.err.println(s);
    }
}
