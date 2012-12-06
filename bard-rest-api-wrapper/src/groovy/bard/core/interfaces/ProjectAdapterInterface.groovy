package bard.core.interfaces;


import bard.core.Probe
import bard.core.Value

public interface ProjectAdapterInterface {


    public Long getId();

    public String getName();

    public String getDescription();

    public String getGrantNumber();

    public String getLaboratoryName();

    public List<Probe> getProbes();

    public Integer getNumberOfExperiments();

    public Collection<Value> getAnnotations();

    public Map<String, String> getDictionaryTerms();

    public Map<String, List<String>> getKeggAnnotations();
}
