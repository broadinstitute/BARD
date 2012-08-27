package bard.db.experiment

import bard.db.dictionary.Element

/**
 * RunContextItem is a key value pair that either describes the Experiment a paticular
 * Result.  Therefore, there are 2 concrete subclasses of RunContextItem, ExperimentContextItem
 * and ResultContextItem.  Each concreate subclass has a reference to it's owner either an
 * Experiment or Result.
 */
abstract class RunContextItem {

    private static final int EXT_VALUE_ID_MAX_SIZE = 60
    private static final int VALUE_DISPLAY_MAX_SIZE = 500
    private static final int MODIFIED_BY_MAX_SIZE = 40

    RunContextItem groupResultContext

    Element attributeElement
    Element valueElement

    String extValueId
    String qualifier
    Float valueNum
    Float valueMin
    Float valueMax
    String valueDisplay

    Date dateCreated
    Date lastUpdated
    String modifiedBy



    static mapping = {
        id(column: 'RUN_CONTEXT_ITEM_ID', generator: 'sequence', params: [sequence: 'RUN_CONTEXT_ITEM_ID_SEQ'])
        groupResultContext(column: "GROUP_RUN_CONTEXT_ID")

        discriminator( column: 'DISCRIMINATOR')

        attributeElement(column: 'ATTRIBUTE_ID')
        valueElement(column: 'VALUE_ID')
        qualifier(column: "QUALIFIER", sqlType: "char", length: 2)
    }

    static constraints = {
        groupResultContext nullable: true

        attributeElement()
        valueElement(nullable: true)

        extValueId(nullable: true, blank: false, maxSize: EXT_VALUE_ID_MAX_SIZE)
        qualifier(nullable: true, blank: false, inList: ['= ', '< ', '<=', '> ', '>=', '<<', '>>', '~ '])

        valueNum(nullable: true)
        valueMin(nullable: true)
        valueMax(nullable: true)
        valueDisplay(nullable: true, blank: false, maxSize: VALUE_DISPLAY_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true,)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)

    }
}
