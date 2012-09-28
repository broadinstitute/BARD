package bard.db.dictionary

class Element extends AbstractElement {

    Set<TreeRoot> treeRoots = [] as Set<TreeRoot>
    Set<OntologyItem> ontologyItem = [] as Set<OntologyItem>
    Set<ElementHierarchy> childElementRelationships = [] as Set<ElementHierarchy>
    Set<ElementHierarchy> parentElementRelationships = [] as Set<ElementHierarchy>


    Set<BiologyDescriptor> biologyDescriptors = [] as Set<BiologyDescriptor>
    Set<InstanceDescriptor> instanceDescriptors = [] as Set<InstanceDescriptor>
    Set<AssayDescriptor> assayDescriptors = [] as Set<AssayDescriptor>

    static hasMany = [treeRoots: TreeRoot,
            ontologyItems: OntologyItem,
            childElementRelationships: ElementHierarchy,
            parentElementRelationships: ElementHierarchy,
            assayDescriptors: AssayDescriptor,
            biologyDescriptors: BiologyDescriptor,
            instanceDescriptors: InstanceDescriptor]

    static mappedBy = [childElementRelationships: "childElement",
            parentElementRelationships: "parentElement"]


    OntologyBreadcrumb getOntologyBreadcrumb(){
        return new OntologyBreadcrumb(this)
    }
}