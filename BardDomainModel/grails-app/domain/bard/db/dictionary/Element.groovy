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