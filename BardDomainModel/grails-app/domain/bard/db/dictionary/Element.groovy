package bard.db.dictionary

class Element extends AbstractElement {

    Set<BiologyDescriptor> biologyDescriptors = [] as Set<BiologyDescriptor>
    Set<InstanceDescriptor> instanceDescriptors = [] as Set<InstanceDescriptor>
    Set<AssayDescriptor> assayDescriptors = [] as Set<AssayDescriptor>

    Set<TreeRoot> treeRoots = [] as Set<TreeRoot>
    Set<OntologyItem> ontologyItems = [] as Set<OntologyItem>

    static hasMany = [treeRoots: TreeRoot,
            ontologyItems: OntologyItem,
            assayDescriptors: AssayDescriptor,
            biologyDescriptors: BiologyDescriptor,
            instanceDescriptors: InstanceDescriptor]


    OntologyBreadcrumb getOntologyBreadcrumb(){
        return new OntologyBreadcrumb(this)
    }
}