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

import acl.CapPermissionService
import bard.db.people.Role
import org.springframework.security.access.prepost.PreAuthorize

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/31/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class PanelService {
    CapPermissionService capPermissionService
    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Panel', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Panel updatePanelOwnerRole(Long id, Role ownerRole){
        Panel panel = Panel.findById(id)
        panel.ownerRole = ownerRole

        panel.save(flush: true)

        capPermissionService.updatePermission(panel, ownerRole)
        return Panel.findById(id)
    }
    /**
     *
     * @param name
     * @param id
     * @return
     */
    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Panel', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public Panel updatePanelName(String name, Long id) {
        Panel panel = Panel.findById(id)

        if (panel) {
            panel.name = name
            panel.save(flush: true)
        }
        return Panel.findById(id)
    }
    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Panel', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    Panel updatePanelDescription(String description, Long id) {
        Panel panel = Panel.findById(id)

        if (panel) {
            panel.description = description
            panel.save(flush: true)
        }
        return Panel.findById(id)
    }
    /**
     * Both the panel and the assay must exist
     * Only the owner of the panel can associate an assay to the panel
     * @param assay
     * @param panel
     * @param id
     */
    @PreAuthorize("hasPermission(#id,'bard.db.registration.Panel',admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public void associateAssay(Assay assay, Long id) {
        Panel panel = Panel.findById(id)
        PanelAssay panelAssay = new PanelAssay()
        panelAssay.assay = assay
        panelAssay.panel = panel

        panel.addToPanelAssays(panelAssay)
        assay.addToPanelAssays(panelAssay)
    }
    /**
     * Both the assay and the panel must exist and only the owner of the panel
     * can dissociate it an assay from the panel
     * @param assay
     * @param panel
     * @param id
     * @return
     */
    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Panel', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    public boolean disassociateAssay(Assay assay, Long id) {
        Panel panel = Panel.findById(id)
        PanelAssay found = null;
        for (panelAssay in panel.panelAssays) {
            if (panelAssay.assay == assay && panelAssay.panel == panel) {
                found = panelAssay;
                break;
            }
        }

        if (found == null) {
            return false;
        } else {
            panel.removeFromPanelAssays(found)
            assay.removeFromPanelAssays(found)
            found.delete(flush: true)
            return true;
        }
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Panel', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void deletePanel(Long id) {
        Panel panel = Panel.findById(id)
        PanelAssay.deleteAll(PanelAssay.findAllByPanel(panel))
        panel.delete(flush: true)
    }

    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Panel', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void associateAssays(Long id, List<Assay> assays) {
        for (Assay assay : assays) {
            associateAssay(assay, id)
        }
    }
    @PreAuthorize("hasPermission(#id, 'bard.db.registration.Panel', admin) or hasRole('ROLE_BARD_ADMINISTRATOR')")
    void disassociateAssays(Long id, List<Assay> assays) {
        for (Assay assay : assays) {
            disassociateAssay(assay,id)
        }
    }


}
