package bard.db.model

import bard.db.dictionary.Descriptor
import bard.db.dictionary.Element
import bard.db.registration.AssayContextItem
import org.apache.commons.lang.StringUtils

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/2/12
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractContext{
    private static final int CONTEXT_NAME_MAX_SIZE = 128
    private static final int CONTEXT_GROUP_MAX_SIZE = 256
    private static final int MODIFIED_BY_MAX_SIZE = 40

    /**
     * these labels or portions of labels are pulled out of the ontology and are an order of preference for sorting and naming of cards
     */
    private static final List<String> KEY_LABELS = ['assay component role', 'assay component type', 'detection', 'assay readout', 'wavelength', 'number']

    private static final Map<String, String> KEY_LABEL_NAME_MAP = ['assay component role': 'label',
            'assay component type': 'label', 'detection': 'detection method',
            'assay readout': 'assay readout', 'wavelength': 'fluorescence/luminescence',
            'number': 'result detail']


    private static final String BIOLOGY_LABEL = 'biology'
    private static final String PROBE_REPORT_LABEL = 'probe report'

    String contextName
    String contextGroup

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static constraints = {
        contextName(nullable: true, blank: false, maxSize: CONTEXT_NAME_MAX_SIZE)
        contextGroup(nullable: true, blank: false, maxSize: CONTEXT_GROUP_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    static transients = ["preferredName", 'contextType', 'itemSubClass']

    /**
     *
     * @return
     */
    Descriptor getPreferredDescriptor() {
        Descriptor preferredDescriptor
        List<Descriptor> preferredDescriptors = getContextItems().collect { it.attributeElement.ontologyBreadcrumb.preferedDescriptor }
        preferredDescriptors = preferredDescriptors.findAll() // hack to eliminate any nulls (Elements (971 and 1329)
        for (String keyLabel in KEY_LABELS) {
            if (preferredDescriptors.any { it?.label?.contains(keyLabel) }) {
                preferredDescriptor = preferredDescriptors.find { it.label.contains(keyLabel) }
                break
            }
        }

        if (preferredDescriptor == null && preferredDescriptors) {
            preferredDescriptor = preferredDescriptors.first()
        }
        return preferredDescriptor
    }

    /**
     * get the SectionKey based on the contextGroup
     * @return
     */
    String getSectionKey(){
        String sectionKey = owner.getSectionKeyForContextGroup(contextGroup)
        return sectionKey
    }

    String getPreferredName() {
        String preferredName = 'undefined'
        if (StringUtils.isNotBlank(this.contextName)) {
            preferredName = this.contextName
        } else {
            preferredName = getPreferredDescriptor()?.label
            for (Map.Entry entry in KEY_LABEL_NAME_MAP) {
                if (preferredName && preferredName.contains(entry.key)) {
                    if ('label' != entry.value) {
                        preferredName = entry.value
                    }
                    break
                }
            }
        }
        return preferredName
    }

    void setPreferredName(String name) {
        this.contextName = name;
    }

    abstract List getContextItems()

    abstract AbstractContextOwner getOwner()

    abstract String getSimpleClassName()

    abstract void addContextItem(AbstractContextItem item);

    /**
     *
     * @return an Element that represents a defining Attribute and currently serves to classify this context as pertaining to a particular type.
     * Not many of these are defined at this point, only biology and probe report
     */
    Element getContextType(){
        final Element biology = Element.findByLabel(BIOLOGY_LABEL)
        final Element probeReport = Element.findByLabel(PROBE_REPORT_LABEL)
        if(getContextItems().find{AbstractContextItem item-> item.attributeElement == biology}){
            return biology
        }
        else if ( getContextItems().find{AbstractContextItem item-> item.attributeElement == probeReport}){
            return probeReport
        }
        else {
            return null
        }
    }

    /**
     * @return the SubClass of the Item this Context is expecting
     */
    abstract Class<? extends AbstractContextItem> getItemSubClass()
}
