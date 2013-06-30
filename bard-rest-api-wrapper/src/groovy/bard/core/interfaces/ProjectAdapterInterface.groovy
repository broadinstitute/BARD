package bard.core.interfaces;


import bard.core.Probe
import bard.core.rest.spring.assays.BardAnnotation

public interface ProjectAdapterInterface {

    public String getHighlight();

    public Long getId();

    public String getName();

    public String getDescription();


    public List<Probe> getProbes();

    public Integer getNumberOfExperiments();

    public BardAnnotation getAnnotations();

    public String getExperimentType(Long experimentId);
    public Map<Long, String> getExperimentTypes();
}
