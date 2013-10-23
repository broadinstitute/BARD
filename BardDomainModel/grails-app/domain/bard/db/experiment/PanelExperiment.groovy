package bard.db.experiment

import bard.db.registration.Assay
import bard.db.registration.Panel
import bard.db.registration.PanelAssay

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 10/23/13
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
class PanelExperiment {

    Panel panel

    static belongsTo = [panel: Panel]

    Set<PanelAssay> experiments = [] as Set
    static hasMany = [experiments: Experiment]

    Date dateCreated = new Date()
    // grails auto-timestamp
    Date lastUpdated = new Date()

    static mapping = {
        id(column: 'PANEL_EXPRMT_ID', generator: 'sequence', params: [sequence: 'PANEL_EXPRMT_ID_SEQ'])
    }

    static constraints = {
        panel(nullable: false)
        dateCreated(nullable: false)
        lastUpdated(nullable: true)
    }
}
