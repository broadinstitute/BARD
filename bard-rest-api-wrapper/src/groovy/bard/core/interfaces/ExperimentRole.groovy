package bard.core.interfaces;


public enum ExperimentRole {
    Unknown(-1),
    Primary(1),
    Counterscreen(2),
    SecondaryConfirmation(3),
    SecondaryAlternative(4),
    SecondaryOrthogonal(5),
    SecondarySelectivity(6);
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
