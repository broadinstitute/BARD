package bard.db.registration

import bard.db.enums.ReadyForExtraction
import bard.db.enums.hibernate.ReadyForExtractionEnumUserType
import bard.db.project.Project

/**
 * A Panel has a many-to-many relationship with Assays
 *
 * A Panel can have one or more Assay's associated to it
 */
class Panel {
    public static final int MODIFIED_BY_MAX_SIZE = 40
    public static final int PANEL_NAME_MAX_SIZE = 250
    public static final int PANEL_DESCRIPTION_MAX_SIZE = 1000

    String name
    String description
    ReadyForExtraction readyForExtraction = ReadyForExtraction.NOT_READY

    String modifiedBy
    // grails auto-timestamp
    Date dateCreated = new Date()
    Date lastUpdated = new Date()
    def capPermissionService

    Set<PanelAssay> panelAssays = [] as Set
    static hasMany = [panelAssays: PanelAssay]


    static constraints = {
        name(nullable: false, blank: false, maxSize: PANEL_NAME_MAX_SIZE)
        description(nullable: true, blank: false, maxSize: PANEL_DESCRIPTION_MAX_SIZE)
        readyForExtraction(nullable: false)
        dateCreated(nullable: false)
        lastUpdated(nullable: false)
        modifiedBy(nullable: true, blank: false, maxSize: MODIFIED_BY_MAX_SIZE)
    }

    static mapping = {
        id(column: "PANEL_ID", generator: "sequence", params: [sequence: 'PANEL_ID_SEQ'])
        readyForExtraction(type: ReadyForExtractionEnumUserType)
    }

    def afterInsert() {
        Panel.withNewSession {
            capPermissionService?.addPermission(this)
        }
    }

    def afterDelete() {
        Panel.withNewSession {
            capPermissionService?.removePermission(this)
        }
    }


    String getOwner() {
        Panel.withNewSession {
            return capPermissionService?.getOwner(this)
        }
    }
}