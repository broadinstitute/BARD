package bard.core.interfaces;

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
