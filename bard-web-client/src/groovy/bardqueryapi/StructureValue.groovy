package bardqueryapi

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 2/8/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */
class StructureValue implements WebQueryValue {

    public String toString() {
        return this.getClass().getName()
    }

    Map getValue() {
        return [sid: this.sid,
                cid: this.cid,
                smiles: this.smiles,
                name: this.name,
                numberOfActiveAssays: this.numActive,
                numberOfAssays: numAssays]
    }

    Long cid
    String smiles
    Long sid
    String name
    int numActive
    int numAssays
}
