package bard.core

import bard.core.interfaces.BiologyType
import bard.core.interfaces.BiologyValues

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/16/12
 * Time: 1:39 PM
 * To change this template use File | Settings | File Templates.
 */
class Biology  extends Entity implements BiologyValues {
    protected BiologyType type = BiologyType.Protein;

    public Biology () {}
    public Biology (String name) {
        super (name);
    }

    public BiologyType getType () { return type; }
    public void setType (BiologyType type) { this.type = type; }
}
