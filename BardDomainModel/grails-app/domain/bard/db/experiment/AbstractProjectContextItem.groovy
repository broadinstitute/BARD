package bard.db.experiment

import bard.db.dictionary.Element

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/22/12
 * Time: 11:43 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractProjectContextItem {

    private static final int EXT_VALUE_ID_MAX_SIZE = 60
    private static final int VALUE_DISPLAY_MAX_SIZE = 500
    private static final int MODIFIED_BY_MAX_SIZE = 40



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
    AbstractProjectContextItem parentGroupProjectContext

    static mapping = {
        table('PROJECT_CONTEXT_ITEM')
        id(column: "PROJECT_CONTEXT_ITEM_ID", generator: "sequence", params: [sequence: 'PROJECT_CONTEXT_ITEM_ID_SEQ'])
        parentGroupProjectContext(column: 'GROUP_PROJECT_CONTEXT_ID')

        discriminator(column: 'DISCRIMINATOR')

        attributeElement(column: 'ATTRIBUTE_ID')
        valueElement(column: 'VALUE_ID')
        qualifier(column: "QUALIFIER", sqlType: "char", length: 2)
    }

    static constraints = {
        attributeElement()
        valueElement(nullable: true)
        parentGroupProjectContext(nullable: true)
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
