package blater.r88.machine;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Registers {
    short A;
    short B;
    short C;
    int PC;
    int SP;
    int DD;
    int EE;


    boolean sign;
    boolean zero;
    boolean carry;
    boolean interrupt;
    boolean dec;
    boolean breaK;
}
