package bard.core.interfaces

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/20/12
 * Time: 6:43 PM
 * To change this template use File | Settings | File Templates.
 */
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