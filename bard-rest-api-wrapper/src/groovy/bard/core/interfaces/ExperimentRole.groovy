package bard.core.interfaces;


public enum ExperimentRole {
    Unknown(-1),
    Primary(0),
    Counterscreen(1),
    SecondaryConfirmation(2),
    SecondaryAlternative(3),
    SecondaryOrthogonal(4),
    SecondarySelectivity(5);
    private int role
    ExperimentRole(int role){
      this.role = role
    }
    public static ExperimentRole valueOf(int i) {
        for (ExperimentRole c : values()) {
            if (i == c.role)
                return c;
        }
        throw new IllegalArgumentException("Bogus ExperimentRole " + i);
    }
}
