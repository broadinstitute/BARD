package bard.core.interfaces;


public enum ExperimentRole {
    Primary,
    Counterscreen,
    SecondaryConfirmation,
    SecondaryAlternative,
    SecondaryOrthogonal,
    SecondarySelectivity;

    public static ExperimentRole valueOf(int i) {
        for (ExperimentRole c : values()) {
            if (i == c.ordinal())
                return c;
        }
        throw new IllegalArgumentException("Bogus ExperimentRole " + i);
    }
}
