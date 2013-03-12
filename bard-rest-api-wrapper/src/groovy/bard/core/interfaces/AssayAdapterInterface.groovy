package bard.core.interfaces;


import bard.core.rest.spring.assays.BardAnnotation

public interface AssayAdapterInterface {


    String getAssayStatus() ;
    String getDesignedBy();
    String getAssayTypeString();

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

    public List<BardAnnotation> getAnnotations()

    public List<String> getKeggDiseaseNames()

    public List<String> getKeggDiseaseCategories()

    public Map<String, List<String>> getKeggAnnotations()
}
