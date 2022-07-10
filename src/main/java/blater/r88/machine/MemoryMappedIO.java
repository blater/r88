package blater.r88.machine;

import blater.r88.devices.DiskController;

public enum MemoryMappedIO {
    DISK_CMD(0xFF00)
    ;

    DiskController dc = new DiskController();

    public static final short IOParamLen = 0x8;

    int addr;
    MemoryMappedIO (int addr) {
       this.addr = addr;
    }
    // triggered on interrupt
    public void interrupt (Processor cpu) {
        for (MemoryMappedIO ioport: MemoryMappedIO.values()) {
            if (cpu.getMemory().peek(ioport.addr) != 0)  {
                short[] params = cpu.getMemory().copyBlock(ioport.addr, IOParamLen);
                //lets do something!
                switch(ioport) {
                    case DISK_CMD -> dc.execute(params, cpu);
                }
            }
        }
    }

    // save a block of memory to disk
    public void save() {

    }
}
