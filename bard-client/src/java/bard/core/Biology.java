package bard.core;

import bard.core.interfaces.BiologyType;
import bard.core.interfaces.BiologyValues;

public class Biology extends Entity implements BiologyValues {
    private static final long serialVersionUID = 0xd707bf8ee3fc8386l;

    protected BiologyType type = BiologyType.Protein;

    public Biology () {}
    public Biology (String name) {
        super (name);
    }

    public BiologyType getType () { return type; }
    public void setType (BiologyType type) { this.type = type; }
}
