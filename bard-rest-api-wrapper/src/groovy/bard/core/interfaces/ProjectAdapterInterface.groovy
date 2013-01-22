package bard.core.interfaces;


import bard.core.Probe
import bard.core.Value
import bard.core.rest.spring.assays.BardAnnotation

public interface ProjectAdapterInterface {

    public String getHighlight();

    public Long getId();

    public String getName();

    public String getDescription();

    public String getGrantNumber();

    public String getLaboratoryName();

    public List<Probe> getProbes();

    public Integer getNumberOfExperiments();

    public List<BardAnnotation> getAnnotations();

    public Map<String, String> getDictionaryTerms();

    public Map<String, List<String>> getKeggAnnotations();
}
