package bard.db.dictionary

class Element extends AbstractElement {

    Set<BiologyDescriptor> biologyDescriptors = [] as Set<BiologyDescriptor>
    Set<InstanceDescriptor> instanceDescriptors = [] as Set<InstanceDescriptor>
    Set<AssayDescriptor> assayDescriptors = [] as Set<AssayDescriptor>

    Set<TreeRoot> treeRoots = [] as Set<TreeRoot>
    Set<OntologyItem> ontologyItems = [] as Set<OntologyItem>

    /**
     * the element hierarchy objects that have this element as the parentElement
     */
    Set<ElementHierarchy> parentHierarchies = [] as Set<ElementHierarchy>
    /**
     * the element hierarchy objects that have this element as the childElement
     */
    Set<ElementHierarchy> childHierarchies = [] as Set<ElementHierarchy>

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

    OntologyBreadcrumb getOntologyBreadcrumb(){
        return new OntologyBreadcrumb(this)
    }
}