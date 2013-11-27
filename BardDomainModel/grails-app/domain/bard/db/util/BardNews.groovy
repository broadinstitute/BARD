package bard.db.util

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Contains information that would be used to notify users of maintenance work etc
 */
class BardNews {
    String entryId
    Date datePublished
    Date entryDateUpdated
    String title
    String content
    String link
    String authorName
    String authorEmail
    String authorUri
    Date dateCreated = new Date()
    Date lastUpdated
    String modifiedBy

    static transients = []
    static mapping = {
        table('BARD_NEWS')
        id(column: "ID", generator: "sequence", params: [sequence: 'BARD_NEWS_ID_SEQ'])
    }

    static constraints = {
        entryId(nullable: false, blank: false, maxSize: 250)
        entryDateUpdated(nullable: true)
        title(nullable: false, blank: false, maxSize: 1020)
        content(nullable: true)
        link(nullable: false, blank: false, maxSize: 250)
        authorName(nullable: true, blank: false, maxSize: 125)
        authorEmail(nullable: true, blank: false, maxSize: 125)
        authorUri(nullable: true, blank: false, maxSize: 250)
        lastUpdated(nullable: true)
        modifiedBy(nullable: true)
    }
}
