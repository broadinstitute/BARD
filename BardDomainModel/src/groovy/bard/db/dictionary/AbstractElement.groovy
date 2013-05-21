package bard.db.dictionary

import bard.db.enums.AddChildMethod
import bard.db.enums.ExpectedValueType
import bard.db.enums.ReadyForExtraction
import bard.db.enums.hibernate.AddChildMethodEnumUserType
import bard.db.enums.hibernate.ExpectedValueTypeEnumUserType
import bard.db.enums.hibernate.ReadyForExtractionEnumUserType

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/22/12
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractElement {

    public static final int LABEL_MAX_SIZE = 128
    public static final int DESCRIPTION_MAX_SIZE = 1000
    public static final int ABBREVIATION_MAX_SIZE = 20
    public static final int SYNONYMS_MAX_SIZE = 1000
    public static final int BARD_URI_MAX_SIZE = 250
    private static final int EXTERNAL_URL_MAX_SIZE = 1000
    private static final int MODIFIED_BY_MAX_SIZE = 40


    ElementStatus elementStatus = ElementStatus.Pending
    String label
    String description
    String abbreviation
    String synonyms
    Element unit
    String bardURI
    String externalURL
    ReadyForExtraction readyForExtraction = ReadyForExtraction.NOT_READY
    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    String modifiedBy



    static constraints = {
        elementStatus(nullable: false)

        label(nullable: false, unique: true, maxSize: LABEL_MAX_SIZE)
        unit(nullable: true)
        abbreviation(nullable: true, maxSize: ABBREVIATION_MAX_SIZE)
        bardURI(nullable: true, maxSize: BARD_URI_MAX_SIZE)
        description(nullable: true, maxSize: DESCRIPTION_MAX_SIZE)
        synonyms(nullable: true, maxSize: SYNONYMS_MAX_SIZE)
        externalURL(nullable: true, maxSize: EXTERNAL_URL_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
    static mapping = {
        id(column: 'ELEMENT_ID', generator: 'sequence', params: [sequence: 'ELEMENT_ID_SEQ'])
        bardURI(column: 'BARD_URI')
        externalURL(column: 'EXTERNAL_URL')
        readyForExtraction(type: ReadyForExtractionEnumUserType)
    }
}

