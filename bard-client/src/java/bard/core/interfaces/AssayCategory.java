package bard.core.interfaces;

public enum AssayCategory {
    Unknown,
    MLSCN,
    MLPCN,
    MLSCN_AP,
    MLPCN_AP;

    public static AssayCategory valueOf(int i) {
        for (AssayCategory c : values()) {
            if (i == c.ordinal())
                return c;
        }
        return Unknown;
    }
}
