package bard.core.adapter;


import bard.core.interfaces.CompoundAdapterInterface
import bard.core.rest.spring.compounds.Compound
import org.apache.commons.lang3.StringUtils

public class CompoundAdapter implements CompoundAdapterInterface {
    final Compound compound

    public CompoundAdapter(final Compound compound) {
        this.compound = compound
    }


    public boolean isDrug() {
        return this.compound.compoundClass == 'Drug'
    }

    public String getProbeId() {
        return this.compound.probeId
    }

    public boolean isProbe() {
        return StringUtils.isNotBlank(this.getProbeId())
    }

    public Long getId() {
        return this.getPubChemCID()
    }

    @Deprecated //use getId instead
    public Long getPubChemCID() {
        return this.compound.cid
    }
    /**
     *
     * @return
     */
    @Deprecated //use getSmiles instead
    public String getStructureSMILES() {
        return this.compound.smiles
    }

    @Deprecated //No longer used
    public String getStructureMOL() {
        return null
    }

    @Deprecated // No longer used
    public Object getStructureNative() {
        return null
    }

    @Deprecated //No longer available in pay load
    public String formula() {
        return "";
    }

    public Double mwt() {
        return this.compound.mwt
    }

    public Double exactMass() {
        return this.compound.exactMass
    }

    public Integer hbondDonor() {
        return compound.hbondDonor
    }

    public Integer hbondAcceptor() {
        return compound.hbondAcceptor
    }

    public Integer rotatable() {
        return compound.rotatable
    }

    @Deprecated // No longer used
    public Integer definedStereo() {
        return null;
    }

    @Deprecated //No longer used
    public Integer stereocenters() {
        return null;
    }

    public Double TPSA() {
        return compound.tpsa
    }

    public Double logP() {
        return compound.xlogp
    }


    public String getName() {
        return compound.name;
    }

    public String getIupacName() {
        return compound.iupacName;
    }

    public String getUrl() {
        return compound.url
    }

    public Integer getComplexity() {
        return compound.complexity
    }
    //TODO: Make compound class an Enum
    public String getCompoundClass() {
        return compound.compoundClass
    }

    public int getNumberOfAssays() {
        return compound.getNumAssay().intValue()
    }

    public int getNumberOfActiveAssays() {
        return compound.getNumActiveAssay().intValue()
    }

    public String resourcePath() {
        return compound.resourcePath
    }
}