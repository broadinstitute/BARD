package bard.core.interfaces

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/20/12
 * Time: 6:44 PM
 * To change this template use File | Settings | File Templates.
 */
public enum AssayRole {
    Primary,
    Counterscreen,
    SecondaryConfirmation,
    SecondaryAlternative,
    SecondaryOrthogonal,
    SecondarySelectivity;

    public static AssayRole valueOf(int i) {
        for (AssayRole c : values()) {
            if (i == c.ordinal())
                return c;
        }
        throw new IllegalArgumentException("Bogus AssayRole " + i);
    }
}
