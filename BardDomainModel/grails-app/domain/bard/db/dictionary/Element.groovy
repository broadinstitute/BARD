package bard.db.dictionary

class Element extends AbstractElement {

    static hasMany = [treeRoots: TreeRoot,
            ontologyItems: OntologyItem,
            childElementRelationships: ElementHierarchy,
            parentElementRelationships: ElementHierarchy]

    static mappedBy = [childElementRelationships: "childElement",
            parentElementRelationships: "parentElement"]

}
