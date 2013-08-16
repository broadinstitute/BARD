package bard.db.model

import bard.db.dictionary.Descriptor
import bard.db.dictionary.Element
import bard.db.enums.ContextType
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
    private static final int MODIFIED_BY_MAX_SIZE = 40

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

    static transients = ["preferredName", 'itemSubClass']

    /**
     *
     * @return
     */
    /*
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
    */

    String getPreferredName() {
        return this.contextName;
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
    Element getDataExportContextType(){
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
