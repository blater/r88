package blater.r88.machine.cpu;

import lombok.Getter;

@Getter
public enum RegisterType {
    A(8),
    B(8),
    C(8),
    BC(16),
    PC(16),
    SP(16),
    P(8)
    ;

    private final int bits;

    RegisterType(int bits) {
        this.bits = bits;
    }
}
