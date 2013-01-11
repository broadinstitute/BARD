package bard.core.interfaces;


import bard.core.rest.spring.assays.AssayAnnotation

public interface AssayAdapterInterface {
    public String getName()
    public String getHighlight();

    public Long getCapAssayId()


    public AssayType getType()

    public AssayRole getRole()

    public AssayCategory getCategory()

    public String getDescription()

    public Long getId()

    public String getProtocol()

    public String getComments()

    public Long getAid()

    public String getSource()

    public List<AssayAnnotation> getAnnotations()

    public List<String> getKeggDiseaseNames()

    public List<String> getKeggDiseaseCategories()

    public Map<String, List<String>> getKeggAnnotations()
}
