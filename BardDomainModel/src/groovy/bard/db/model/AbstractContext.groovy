package bard.db.model

import bard.db.dictionary.Descriptor
import bard.db.dictionary.Element
import bard.db.enums.ContextType
import bard.db.guidance.Guidance
import bard.db.guidance.GuidanceAware
import bard.db.guidance.GuidanceRule
import bard.db.guidance.GuidanceUtils
import bard.db.registration.AssayContextItem
import bard.db.registration.AttributeType

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/2/12
 * Time: 11:33 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractContext implements GuidanceAware {
    private static final int CONTEXT_NAME_MAX_SIZE = 128
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
    ContextType contextType;

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static constraints = {
        contextName(nullable: true, blank: false, maxSize: CONTEXT_NAME_MAX_SIZE)
        contextType(nullable: false)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    static transients = ["preferredName", 'itemSubClass','atLeastOneNonFixedContextItem','guidanceRules']

    boolean atLeastOneNonFixedContextItem(){
        for(AssayContextItem assayContextItem : this.getContextItems()){
            if(assayContextItem.attributeType != AttributeType.Fixed){
                return true
            }
        }
        return false
    }
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


    String getPreferredName() {
        return this.contextName;
    }

    void setPreferredName(String name) {
        this.contextName = name;
    }

    abstract List<? extends AbstractContextItem> getContextItems()

    abstract AbstractContextOwner getOwner()

    abstract String getSimpleClassName()

    abstract void addContextItem(AbstractContextItem item);

    /**
     *
     * @return an Element that represents a defining Attribute and currently serves to classify this context as pertaining to a particular type.
     * Not many of these are defined at this point, only biology and probe report
     */
    Element getDataExportContextType() {
        final Element biology = Element.findByLabel(BIOLOGY_LABEL)
        final Element probeReport = Element.findByLabel(PROBE_REPORT_LABEL)
        if (getContextItems().find { AbstractContextItem item -> item.attributeElement == biology }) {
            return biology
        } else if (getContextItems().find { AbstractContextItem item -> item.attributeElement == probeReport }) {
            return probeReport
        } else {
            return null
        }
    }

    /**
     * @return the SubClass of the Item this Context is expecting
     */
    abstract Class<? extends AbstractContextItem> getItemSubClass()

    /**
     * subclasses that need to utilize some guidance should override and add rules
     */
    @Override
    List<GuidanceRule> getGuidanceRules(){
        []
    }

    /**
     * @return a list of Guidance messages
     */
    @Override
    List<Guidance> getGuidance() {
        GuidanceUtils.getGuidance(getGuidanceRules())
    }

}
