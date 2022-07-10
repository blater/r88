package blater.r88.machine.cpu;

import lombok.Getter;

@Getter
public enum FixedVectorType {
    IRQ(0xFFFA),
    NMI(0xFFFC),
    RESET(0xFFFE);

    private final int addr;

    FixedVectorType(int addr) {
        this.addr = addr;
    }
}
