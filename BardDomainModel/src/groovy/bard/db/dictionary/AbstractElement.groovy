package bard.db.dictionary

import bard.db.enums.ReadyForExtraction

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/22/12
 * Time: 1:46 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class AbstractElement {

    private static final int LABEL_MAX_SIZE = 128
    private static final int DESCRIPTION_MAX_SIZE = 1000
    private static final int ABBREVIATION_MAX_SIZE = 20
    private static final int SYNONYMS_MAX_SIZE = 1000
    private static final int BARD_URI_MAX_SIZE = 250
    private static final int EXTERNAL_URL_MAX_SIZE = 1000
    private static final int READY_FOR_EXTRACTION_MAX_SIZE = 20
    private static final int ELEMENT_STATUS_MAX_SIZE = 20
    private static final int MODIFIED_BY_MAX_SIZE = 40


    ElementStatus elementStatus = ElementStatus.Pending
    String label
    String description
    String abbreviation
    String synonyms
    Element unit
    String bardURI
    String externalURL
    ReadyForExtraction readyForExtraction = ReadyForExtraction.Pending

    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    String modifiedBy

    static constraints = {
        elementStatus(nullable: false, maxSize: ELEMENT_STATUS_MAX_SIZE)

        label(nullable: false, unique: true, maxSize: LABEL_MAX_SIZE)
        unit(nullable: true)
        abbreviation(nullable: true, maxSize: ABBREVIATION_MAX_SIZE)
        bardURI(nullable: true, maxSize: BARD_URI_MAX_SIZE)
        description(nullable: true, maxSize: DESCRIPTION_MAX_SIZE)
        synonyms(nullable: true, maxSize: SYNONYMS_MAX_SIZE)
        externalURL(nullable: true, maxSize: EXTERNAL_URL_MAX_SIZE)

        readyForExtraction(nullable: false, maxSize: READY_FOR_EXTRACTION_MAX_SIZE)

        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }
    static mapping = {
        id(column: 'ELEMENT_ID', generator: 'sequence', params: [sequence: 'ELEMENT_ID_SEQ'])
        bardURI(column: 'BARD_URI')
        externalURL(column: 'EXTERNAL_URL')
    }
}
enum ElementStatus {
    Pending,
    Published,
    Deprecated,
    Retired
}
