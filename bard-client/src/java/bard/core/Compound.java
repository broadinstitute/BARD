package bard.core;


import bard.core.interfaces.CompoundValues;

public class Compound extends Entity implements CompoundValues {
    private static final long serialVersionUID = 0xf59548a25c44645cl;



    private boolean isDrug;
    private Probe probe;

    public Compound () {}
    public Compound (String name) {
        super (name);
    }

    public boolean isDrug() {
        return this.isDrug;
    }

    public void setDrug(boolean drug) {
        this.isDrug = drug;
    }

}
