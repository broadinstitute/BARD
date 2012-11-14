package bard.core.interfaces;

import bard.core.Format;

import java.io.Serializable;

/**
 * data associated with a molecule entity
 */
public interface MolecularData extends Serializable {

    /*
     * properties
     */
    String formula ();
    Double mwt (); // molecular weight
    Double exactMass ();
    Integer hbondDonor (); // count of H-bond donors
    Integer hbondAcceptor (); // count of H-bond acceptors
    Integer rotatable (); // count of rotatable bonds
    Integer definedStereo (); // count of defined stereo centers
    Integer stereocenters (); // count of stereo centers in the molecule
    Double TPSA (); // topological polar surface area
    Double logP (); // logP
    Boolean ruleOf5 ();

    int[] fingerprint (); // bit fingerprint for indexing/searching

    /*
     * output formats
     */
    public Object toFormat (Format format);
    public byte[] toImage (int size, int background);

    /*
     * input
     */
    public void setMolecule (Object input);
}
