package bard.core.interfaces;


import bard.core.Value
import bard.core.interfaces.AssayCategory
import bard.core.interfaces.AssayRole
import bard.core.interfaces.AssayType

public interface AssayAdapterInterface {
    public String getName()

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

    public Collection<Value> getAnnotations()

    public List<String> getKeggDiseaseNames()

    public List<String> getKeggDiseaseCategories()

    public Map<String, List<String>> getKeggAnnotations()
}
