package bard.core.interfaces;


public enum ExperimentType {
    Other, //0
    Screening, //1
    Confirmatory, //2
    Summary; //3

    public static ExperimentType valueOf(int i) {
        for (ExperimentType t : values()) {
            if (t.ordinal() == i) {
                return t;
            }
        }
        throw new IllegalArgumentException("Bogus ExperimentType " + i);
    }
}
