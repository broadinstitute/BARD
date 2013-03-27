package bard.db.model

import bard.db.dictionary.Element

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/2/12
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractContextItem {
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

    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static constraints = {

        attributeElement(nullable: false)
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

    abstract AbstractContext getContext()
}
