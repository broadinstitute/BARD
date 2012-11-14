package bard.core.interfaces;


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
