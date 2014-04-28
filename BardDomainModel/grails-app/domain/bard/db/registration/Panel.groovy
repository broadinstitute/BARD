/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
        ownerRole(nullable: false)
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

        final String objectOwner = this.ownerRole?.displayName

        return objectOwner
    }

    String getDisplayName() {
        return "${this.id} - ${name}"
    }
}
