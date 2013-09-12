package bard.db.registration

import org.springframework.security.access.prepost.PreAuthorize

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/31/12
 * Time: 3:36 PM
 * To change this template use File | Settings | File Templates.
 */
class PanelService {

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