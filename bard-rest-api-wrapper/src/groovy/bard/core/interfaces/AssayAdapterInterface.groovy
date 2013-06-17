package bard.core.interfaces
import bard.core.rest.spring.assays.BardAnnotation
import bard.core.rest.spring.assays.MinimumAnnotation
import bard.core.rest.spring.util.NameDescription

public interface AssayAdapterInterface {


    String getAssayStatus() ;
    String getDesignedBy();
    String getAssayTypeString();

    public String getName()
    public String getHighlight();

    public Long getCapAssayId()

    Double getScore()

    NameDescription getMatchingField()

    public String getTitle()

    public Long getBardAssayId()

    public AssayType getType()

    public AssayRole getRole()

    public AssayCategory getCategory()

    public String getLastUpdatedDate()

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

    public MinimumAnnotation getMinimumAnnotation()

}
