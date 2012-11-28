package bard.core.adapter;


import bard.core.Compound
import bard.core.Format
import bard.core.MolecularValue
import bard.core.Value
import bard.core.interfaces.MolecularData
import org.apache.commons.lang3.StringUtils

public class CompoundAdapter
extends EntityAdapter<Compound> implements MolecularData {
    private final Object myLock = new Object()
    bard.core.rest.spring.compounds.Compound restCompound

    protected MolecularValue mv;
    private String probeId;

    public CompoundAdapter() {
    }

    public CompoundAdapter(bard.core.rest.spring.compounds.Compound restCompound) {
        this.restCompound = restCompound
    }

    public CompoundAdapter(Compound compound) {
        setCompound(compound);
    }

    public boolean isDrug() {
        if (restCompound) {
            return restCompound.compoundClass == 'Drug'
        }
        return this.getCompound()?.isDrug()
    }

    public String getProbeId() {

        if (this.probeId == null) {
            synchronized (myLock) {
                if (restCompound) {
                    this.probeId = restCompound.probeId ?: ""
                } else {
                    if (this.getCompound()?.getValue(Compound.ProbeIDValue)) {
                        this.probeId = this.getCompound().getValue(Compound.ProbeIDValue).toString()
                    } else {
                        this.probeId = ""
                    }
                }
            }
        }
        return this.probeId
    }

    public boolean isProbe() {
        return StringUtils.isNotBlank(this.getProbeId())
    }

    public Compound getCompound() { return (Compound) getEntity(); }

    public void setCompound(Compound compound) {
        setEntity(compound);
        mv = (MolecularValue) compound.getValue(Compound.MolecularValue);
    }
    //Deprecated
    public List<Long> getPubChemSIDs() {

        Collection<Value> values =
            getEntity().getValues(Compound.PubChemSIDValue);
        List<Long> sids = [];
        for (Value value : values) {
            sids.add((Long) value.getValue());
        }
        return sids;
    }

    public Long getPubChemCID() {
        if (restCompound) {
            return restCompound.cid
        }
        Value cid = getEntity()?.getValue(Compound.PubChemCIDValue);
        if (cid) {
            return (Long) cid.getValue()
        }
        return null
    }

    public String getStructureSMILES() {
        if (restCompound) {
            return this.restCompound.smiles
        }
        return (String) toFormat(Format.SMILES);
    }

    public String getStructureMOL() {
        return (String) toFormat(Format.MOL);
    }

    public Object getStructureNative() {
        return toFormat(Format.NATIVE);
    }

    /*
    * MolecularData interface
    */

    public String formula() {
        return mv ? mv.formula() : "";
    }

    public Double mwt() {
        if (restCompound) {
            return restCompound.mwt
        }
        return mv ? mv.mwt() : null;
    }

    public Double exactMass() {
        if (restCompound) {
            return restCompound.exactMass
        }
        return mv ? mv.exactMass() : null;
    }

    public Integer hbondDonor() {
        if (restCompound) {
            return restCompound.hbondDonor
        }
        return mv ? mv.hbondDonor() : null;
    }

    public Integer hbondAcceptor() {
        if (restCompound) {
            return restCompound.hbondAcceptor
        }
        return mv ? mv.hbondAcceptor() : null;
    }

    public Integer rotatable() {
        if (restCompound) {
            return restCompound.rotatable
        }
        return mv ? mv.rotatable() : null;
    }

    public Integer definedStereo() {
        return mv ? mv.definedStereo() : null;
    }

    public Integer stereocenters() {
        return mv ? mv.stereocenters() : null;
    }

    public Double TPSA() {
        if (restCompound) {
            return restCompound.tpsa
        }
        return mv ? mv.TPSA() : null;
    }

    public Double logP() {
        if (restCompound) {
            return restCompound.xlogp
        }
        return mv ? mv.logP() : null;
    }

    public Boolean ruleOf5() {
        return mv ? mv.ruleOf5() : null;
    }
    //TODO: Add chemaxon library for this to work
    public int[] fingerprint() {
        return mv ? mv.fingerprint() : null;
    }

    public Object toFormat(Format format) {
        return mv ? mv.toFormat(format) : null;
    }

    public byte[] toImage(int size, int background) {
        return mv ? mv.toImage(size, background) : null
    }

    public void setMolecule(Object input) {
        if (mv) {
            mv.setMolecule(input);
        }
    }

    public String getName() {
        return restCompound.name;
    }

    public String getIupacName() {

        return restCompound?.iupacName;
    }

    public String getUrl() {
        return restCompound?.url
    }

    public Integer getComplexity() {
        return restCompound?.complexity
    }

    public String getCompoundClass() {
        return restCompound?.compoundClass
    }

    public int getNumberOfAssays() {
        if (restCompound) {
            return restCompound?.numAssay?.intValue()
        }
    }

    public int getNumberOfActiveAssays() {
        if (restCompound) {
            return restCompound?.numActiveAssay?.intValue()
        }
    }

    public String resourcePath() {
        return restCompound?.resourcePath
    }
}
