package bard.db.dictionary

import bard.db.enums.AddChildMethod
import bard.db.enums.ExpectedValueType
import bard.db.enums.hibernate.AddChildMethodEnumUserType
import bard.db.enums.hibernate.ExpectedValueTypeEnumUserType

class Element extends AbstractElement {

    Set<BiologyDescriptor> biologyDescriptors = [] as Set<BiologyDescriptor>
    Set<InstanceDescriptor> instanceDescriptors = [] as Set<InstanceDescriptor>
    Set<AssayDescriptor> assayDescriptors = [] as Set<AssayDescriptor>

    Set<TreeRoot> treeRoots = [] as Set<TreeRoot>
    Set<OntologyItem> ontologyItems = [] as Set<OntologyItem>
    String curationNotes //Used in UI to explain why you are adding a new term
    Element replacedBy // when an element is retired, this can be set to indicate what term should be used in its place.
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
        replacedBy(nullable: true)
        expectedValueType(nullable: false)
        addChildMethod(nullable: false)

    }

    OntologyBreadcrumb getOntologyBreadcrumb() {
        return new OntologyBreadcrumb(this)
    }
}