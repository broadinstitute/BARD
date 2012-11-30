package bard.core.interfaces;


public interface CompoundAdapterInterface {
    public boolean isDrug();

    public String getProbeId();

    public boolean isProbe();

    public Long getId();

    public Long getPubChemCID();

    public String getStructureSMILES();

    public Double mwt();

    public Double exactMass();

    public Integer hbondDonor();

    public Integer hbondAcceptor();

    public Integer rotatable();

    public Double TPSA();

    public Double logP();


    public String getName();

    public String getIupacName();

    public String getUrl();

    public Integer getComplexity();

    public String getCompoundClass();

    public int getNumberOfAssays();

    public int getNumberOfActiveAssays();

    public String resourcePath();
}
