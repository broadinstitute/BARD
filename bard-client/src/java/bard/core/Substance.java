package bard.core;


import bard.core.interfaces.SubstanceValues;

public class Substance extends Entity implements SubstanceValues {
    private static final long serialVersionUID = 0xf59548a25c44645cl;

    public Substance () {}
    public Substance (String name) {
        super (name);
    }
}
