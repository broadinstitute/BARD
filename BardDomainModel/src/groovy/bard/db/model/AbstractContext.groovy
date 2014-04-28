/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
