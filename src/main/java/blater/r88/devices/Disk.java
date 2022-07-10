package blater.r88.devices;

public class Disk {
    private String name;
    private String root;

    public String status() {
       return "name: "  + name + ", root: " +root;
    }
    public void init(String root) {

    }
    public void insert(String name) {

    }
    public void eject() {

    }
    public short[] load(String filename) {

        return new short[0];
    }
    public void save(short[] block, String filename) {

    }

    public void delete(String nameParam) {
    }
}
