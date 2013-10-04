package bard.db.registration

import bard.db.enums.ReadyForExtraction
import bard.db.enums.hibernate.ReadyForExtractionEnumUserType
import bard.db.people.Role

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

    Role ownerRole //The team that owns this object. This is used by the ACL to allow edits etc
    static belongsTo = [ownerRole: Role]
    static constraints = {
        name(nullable: false, blank: false, maxSize: PANEL_NAME_MAX_SIZE)
        description(nullable: true, blank: false, maxSize: PANEL_DESCRIPTION_MAX_SIZE)
        readyForExtraction(nullable: false)
        dateCreated(nullable: false)
        lastUpdated(nullable: false)
        ownerRole(nullable:true)
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

    boolean hasOwnerRoleChanged //Transient bit that is set to true, if the ownerRole is updated
    static transients = ['hasOwnerRoleChanged']


    def afterUpdate(){
        Panel.withNewSession {
            if(this.hasOwnerRoleChanged){ //update owner role if it changed
                capPermissionService.updatePermission(this,this.ownerRole)
            }

        }
    }

    def beforeUpdate()
    {
        // check if an actual change has been made and ownerRole has been changed
        if(this.isDirty() && this.getDirtyPropertyNames().contains("ownerRole"))
        {
            this.hasOwnerRoleChanged = true//set this true if the ownerRole has been updated
        }
    }
    String getOwner() {
        return this.ownerRole?.displayName
    }

}