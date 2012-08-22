package bard.db.dictionary

class Element extends AbstractElement {

    static hasMany = [treeRoots: TreeRoot,
                ontologyItems: OntologyItem,
                childElementRelationships: ElementHierarchy,
                parentElementRelationships: ElementHierarchy]

        static mappedBy = [childElementRelationships: "childElement",
                parentElementRelationships: "parentElement"]

        static mapping = {
            id(column: 'ELEMENT_ID', generator: 'sequence', params: [sequence: 'ELEMENT_ID_SEQ'])
            unit(column: 'unit')
            externalURL(column: 'external_url')
        }
}
