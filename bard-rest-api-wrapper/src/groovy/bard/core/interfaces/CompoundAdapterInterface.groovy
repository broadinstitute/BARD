package bard.core.interfaces

import bard.core.rest.spring.compounds.ProbeAnnotation

public interface CompoundAdapterInterface {
    public String getHighlight();
    public boolean isDrug();
    public List<ProbeAnnotation> getProbeAnnotations();
    ProbeAnnotation getProbeCid();
    ProbeAnnotation getProbe();
    ProbeAnnotation getProbeSid();
    public boolean hasProbeAnnotations();
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
