package blater.r88.machine.cpu;

public enum StatusValueType {
    S("sign"),
    Z("zero"),
    C("carry"),
    I("index?"),
    D("dinosaur"),
    K("karaoke")
    ;

    private final String desc;

    StatusValueType(String desc) {
        this.desc = desc;
    }
}
