package bard.core

import bard.core.interfaces.SubstanceValues

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 11/16/12
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
class Substance extends Entity implements SubstanceValues {

    public Substance() {}

    public Substance(String name) {
        super(name);
    }
}
