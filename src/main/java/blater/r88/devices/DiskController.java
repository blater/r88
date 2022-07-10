package blater.r88.devices;

import blater.r88.machine.Memory;
import blater.r88.machine.Processor;

import java.util.HashMap;
import java.util.Map;

public class DiskController {
    private static final short MAX_FILENAME = 0xF;
    private static final int MAX_DISKS = 0x8;

    private static final short CMD_REGISTER = 1;
    private static final short CMD_DEREGISTER = 2;
    private static final short CMD_LOAD = 3;
    private static final short CMD_SAVE = 4;
    private static final short CMD_DELETE = 5;

    private static final short PARAM_CMD = 0;
    private static final short PARAM_DEVICE_ID = 1;
    private static final short PARAM_FILENAME_LB = 2;
    private static final short PARAM_FILENAME_HB = 3;
    private static final short PARAM_LEN_LB = 4;
    private static final short PARAM_LEN_HB = 5;
    private static final short PARAM_START_ADDR_LB = 6;
    private static final short PARAM_START_ADDR_HB = 7;

    Map<Short, Disk> deviceMap = new HashMap<>(MAX_DISKS);

    public void execute(short[] params, Processor cpu) {
        // execution command from MemoryMappedIO
        short command = params[PARAM_CMD];
        int len = MemoryHelper.toWord(params[PARAM_LEN_HB], params[PARAM_LEN_LB]);
        int filenameAddr = MemoryHelper.toWord(params[PARAM_FILENAME_HB], params[PARAM_FILENAME_LB]);
        int startAddr = MemoryHelper.toWord(params[PARAM_START_ADDR_HB], params[PARAM_START_ADDR_LB]);
        String nameParam = MemoryHelper.readString(cpu.getMemory(), filenameAddr, MAX_FILENAME);

        short deviceId = params[PARAM_DEVICE_ID];
        boolean res;
        switch (command) {
            case CMD_REGISTER -> res = register(deviceId);
            case CMD_DEREGISTER -> res = deregister(deviceId);
            // case CMD_STATUS -> res = status(deviceId);
            case CMD_LOAD -> res = load(cpu.getMemory(), deviceId, startAddr, nameParam);
            case CMD_SAVE -> res = save(cpu.getMemory(), deviceId, startAddr, len, nameParam);
            case CMD_DELETE -> res = delete(deviceId, nameParam);
            default -> res = false;
        }
        setCpuFlags(cpu, res);
    }

    private boolean delete(short deviceId, String nameParam) {
        deviceMap.get(deviceId).delete(nameParam);
        return true;
    }

    private boolean save(Memory memory, short deviceId, int startAddress, int len, String filename) {
        short[] bytes = memory.copyBlock(startAddress, len);
        deviceMap.get(deviceId).save(bytes, filename);
        return true;
    }

    void setCpuFlags(Processor cpu, boolean result) {
        cpu.getRegisters().setZero(result);
    }


    boolean register(short deviceId) {
        if (!deviceMap.containsKey(deviceId)) {
            Disk disk = new Disk();
            deviceMap.put(deviceId, disk);
            return true;
        }
        return false;
    }

    boolean deregister(short deviceId) {
        if (deviceMap.containsKey(deviceId)) {
            deviceMap.remove(deviceId);
            return true;
        }
        return false;
    }

    boolean load(Memory memory, short deviceId, int startAddress, String filename) {
        short[] bytes = deviceMap.get(deviceId).load(filename);
        memory.putBlock(startAddress, bytes);
        return true;
    }
}
