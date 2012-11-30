package bard.core.adapter;


import bard.core.Format
import bard.core.rest.spring.compounds.Compound
import org.apache.commons.lang3.StringUtils
import bard.core.interfaces.CompoundAdapterInterface

public class CompoundAdapter implements CompoundAdapterInterface{
    private final Object myLock = new Object()
    Compound compound
    private String probeId;

    public CompoundAdapter() {
    }

    public CompoundAdapter(Compound compound) {
        this.compound = compound
    }


    public boolean isDrug() {
        return compound?.compoundClass == 'Drug'
    }

    public String getProbeId() {

        if (this.probeId == null) {
            synchronized (myLock) {
                this.probeId = compound?.probeId ?: ""
            }
        }
        return this.probeId
    }

    public boolean isProbe() {
        return StringUtils.isNotBlank(this.getProbeId())
    }
    public Long getId(){
        return getPubChemCID()
    }
    public Long getPubChemCID() {
        return compound?.cid
    }

    public String getStructureSMILES() {
        return this.compound?.smiles
    }

    public String getStructureMOL() {
        return null
    }

    public Object getStructureNative() {
        return null
    }

    public String formula() {
        return "";
    }

    public Double mwt() {
        return compound?.mwt
    }

    public Double exactMass() {
        return compound?.exactMass
    }

    public Integer hbondDonor() {
        return compound?.hbondDonor
    }

    public Integer hbondAcceptor() {
        return compound?.hbondAcceptor
    }

    public Integer rotatable() {
        return compound?.rotatable
    }

    public Integer definedStereo() {
        return null;
    }

    public Integer stereocenters() {
        return null;
    }

    public Double TPSA() {
        return compound?.tpsa
    }

    public Double logP() {
        return compound?.xlogp
    }

    public Boolean ruleOf5() {
        return null;
    }

    public int[] fingerprint() {
        return null;
    }

    public Object toFormat(Format format) {
        return null;
    }

    public byte[] toImage(int size, int background) {
        return null
    }

    public void setMolecule(Object input) {

    }

    public String getName() {
        return compound?.name;
    }

    public String getIupacName() {
        return compound?.iupacName;
    }

    public String getUrl() {
        return compound?.url
    }

    public Integer getComplexity() {
        return compound?.complexity
    }

    public String getCompoundClass() {
        return compound?.compoundClass
    }

    public int getNumberOfAssays() {
        return compound?.numAssay?.intValue()
    }

    public int getNumberOfActiveAssays() {
        return compound?.numActiveAssay?.intValue()
    }

    public String resourcePath() {
        return compound?.resourcePath
    }
}
