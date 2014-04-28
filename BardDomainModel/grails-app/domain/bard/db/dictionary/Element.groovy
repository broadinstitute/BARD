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

package bard.db.dictionary

import bard.db.enums.AddChildMethod
import bard.db.enums.ExpectedValueType
import bard.db.enums.hibernate.AddChildMethodEnumUserType
import bard.db.enums.hibernate.ExpectedValueTypeEnumUserType
import org.apache.commons.lang3.StringUtils
import org.springframework.validation.Errors

class Element extends AbstractElement {

    Set<BiologyDescriptor> biologyDescriptors = [] as Set<BiologyDescriptor>
    Set<InstanceDescriptor> instanceDescriptors = [] as Set<InstanceDescriptor>
    Set<AssayDescriptor> assayDescriptors = [] as Set<AssayDescriptor>

    Set<TreeRoot> treeRoots = [] as Set<TreeRoot>
    Set<OntologyItem> ontologyItems = [] as Set<OntologyItem>
    String curationNotes //Used in UI to explain why you are adding a new term
    ExpectedValueType expectedValueType = ExpectedValueType.NONE
    AddChildMethod addChildMethod = AddChildMethod.NO

    boolean disableUpdateReadyForExtraction = false

    /**
     * the element hierarchy objects that have this element as the parentElement
     */
    Set<ElementHierarchy> parentHierarchies = [] as Set<ElementHierarchy>
    /**
     * the element hierarchy objects that have this element as the childElement
     */
    Set<ElementHierarchy> childHierarchies = [] as Set<ElementHierarchy>

    static transients = ['disableUpdateReadyForExtraction']

    static hasMany = [treeRoots: TreeRoot,
            ontologyItems: OntologyItem,
            assayDescriptors: AssayDescriptor,
            biologyDescriptors: BiologyDescriptor,
            instanceDescriptors: InstanceDescriptor,
            parentHierarchies: ElementHierarchy,
            childHierarchies: ElementHierarchy
    ]

    static mappedBy = [parentHierarchies: "parentElement",
            childHierarchies: "childElement"
    ]
//    static mapping = {
//        expectedValueType(type: ExpectedValueTypeEnumUserType)
//        addChildMethod(type: AddChildMethodEnumUserType)
//    }
    static constraints = {
        curationNotes(nullable: true, maxSize: DESCRIPTION_MAX_SIZE)
        expectedValueType(nullable: false, validator: { val, obj, errors ->
            if (val == ExpectedValueType.EXTERNAL_ONTOLOGY && !StringUtils.trimToNull(obj.externalURL)) {
                errors.rejectValue("externalURL", "When ExpectedValue is set to ExternalOntology, externalURL can not be empty")
                return false
            }
            return true
        })
        addChildMethod(nullable: false)
        label(nullable: false, validator: { val, obj, errors ->
            if (val.size() > 30 && obj.expectedValueType != ExpectedValueType.NONE && !StringUtils.trimToNull(obj.abbreviation)) {
                errors.rejectValue("abbreviation", "If the label length is > 30 char and expected value_type is not 'none', then the abbreviation must be not empty")
                return false
            }
            return true
        })
    }

    OntologyBreadcrumb getOntologyBreadcrumb() {
        return new OntologyBreadcrumb(this)
    }

    @Override
    boolean equals(Object obj) {
        if (obj && obj instanceof Element) {
            Element other = (Element)obj

            if (other.id && this.id) {
                return ((Element)obj).id == this.id
            } else if (other.id || this.id) {
                return false
            } else {
                if (other.label && this.label) {
                    return other.label.equals(this.label)
                } else if (other.label || this.label) {
                    return false
                } else {
                    return super.equals(other)
                }
            }
        } else {
            return false
        }
    }

    @Override
    int hashCode() {
        if (this.id) {
            return this.id
        } else if (this.label) {
            return this.label.hashCode()
        } else {
            return super.hashCode()
        }
    }
}
