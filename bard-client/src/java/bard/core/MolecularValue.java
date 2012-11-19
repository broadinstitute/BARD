package bard.core;


import bard.core.impl.MolecularDataJChemImpl;
import bard.core.interfaces.MolecularData;

public class MolecularValue extends Value implements MolecularData {
    private static final long serialVersionUID = 0x3c9dd6bc271d71c9l;

     protected MolecularData moldata;

    protected MolecularValue () {
        this.moldata = new MolecularDataJChemImpl();
    }
    public MolecularValue (Value parent) {
        super (parent);
        this.moldata = new MolecularDataJChemImpl();
    }
    public MolecularValue (Value parent, String id) {
        super (parent, id);
        this.moldata = new MolecularDataJChemImpl();
    }
    public MolecularValue (DataSource source) {
        this (source, null);
    }
    public MolecularValue (DataSource source, String id) {
        super (source, id);
        this.moldata = new MolecularDataJChemImpl();
    }
    public MolecularValue (DataSource source, String id, 
                           MolecularData moldata) {
        super (source, id);
        this.moldata = moldata;
    }

    @Override
    public Object getValue () { return moldata; }
    public void setValue (MolecularData moldata) {
        this.moldata = moldata;
    }
    
    /*
     * MolecularData delegation
     */
    public String formula () { return moldata.formula(); }
    public Double mwt () { // molecular weight
        return moldata.mwt();
    }
    public Double exactMass () { return moldata.exactMass(); }
    public Integer hbondDonor () { // count of H-bond donors
        return moldata.hbondDonor();
    }
    public Integer hbondAcceptor () { // count of H-bond acceptors
        return moldata.hbondAcceptor();
    }
    public Integer rotatable () { // count of rotatable bonds
        return moldata.rotatable();
    }
    public Integer definedStereo () { // count of defined stereo centers
        return moldata.definedStereo();
    }
    public Integer stereocenters () { // count of stereo centers in the molecule
        return moldata.stereocenters();
    }
    public Double TPSA () { // topological polar surface area
        return moldata.TPSA();
    }
    public Double logP () { // logP
        return moldata.logP();
    }
    public Boolean ruleOf5 () { return moldata.ruleOf5(); }

    public int[] fingerprint () { // bit fingerprint for indexing/searching
        return moldata.fingerprint();
    }

    /*
     * output formats
     */
    public Object toFormat (Format format) { 
        return moldata.toFormat(format); 
    }
    public byte[] toImage (int size, int background) { 
        return moldata.toImage(size, background); 
    }

    /*
     * input
     */
    public void setMolecule (Object input) {
        moldata.setMolecule(input);
    }
}

