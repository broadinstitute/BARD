package bard.db.registration

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 09/3/13
 * Time: 11:58 AM
 *
 * Join Table that links Assay's to Panel's
 *
 */
class PanelAssay {

    Panel panel
    Assay assay

    Date dateCreated = new Date()
    // grails auto-timestamp
    Date lastUpdated = new Date()

    static belongsTo = [panel: Panel]

    static mapping = {
        id(column: 'PANEL_ASSAY_ID', generator: 'sequence', params: [sequence: 'PANEL_ASSAY_ID_SEQ'])
    }
    static constraints = {
        assay(nullable: false)
        panel(nullable: false)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
        panel(unique: 'assay')
    }


}
