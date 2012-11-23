package bard.core.interfaces;

public interface CompoundValues extends EntityValues {
    public static final String MolecularValue = "CompoundMolecularData";
    /**
     * Identifiers
     */
    public static final String PubChemCIDValue = "CompoundPubChemCID"; // long
    public static final String PubChemSIDValue = "CompoundPubChemSID"; // long
    public static final String ProbeIDValue = "CompoundProbeID"; // string
    public static final String CASValue = "CompoundCAS"; // string
    public static final String UNIIValue = "CompoundUNII"; // string
    public static final String IUPACNameValue = "CompoundIUPACName"; // string
}
