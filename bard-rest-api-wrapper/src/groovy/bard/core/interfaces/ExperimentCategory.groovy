package bard.core.interfaces;


public enum ExperimentCategory {
    Unknown,
    MLSCN,
    MLPCN,
    MLSCN_AP,
    MLPCN_AP;

    public static ExperimentCategory valueOf(int i) {
        for (ExperimentCategory c : values()) {
            if (i == c.ordinal())
                return c;
        }
        return Unknown;
    }
}
