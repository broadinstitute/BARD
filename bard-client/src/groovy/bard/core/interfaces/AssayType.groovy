package bard.core.interfaces

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/20/12
 * Time: 6:45 PM
 * To change this template use File | Settings | File Templates.
 */
public enum AssayType {
    Other, //0
    Screening, //1
    Confirmatory, //2
    Summary; //3

    public static AssayType valueOf(int i) {
        for (AssayType t : values()) {
            if (t.ordinal() == i) {
                return t;
            }
        }
        throw new IllegalArgumentException("Bogus AssayType " + i);
    }
}