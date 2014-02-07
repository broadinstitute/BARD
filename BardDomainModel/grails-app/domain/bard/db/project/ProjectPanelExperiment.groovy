package bard.db.project

import bard.db.experiment.Experiment
import bard.db.experiment.PanelExperiment

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 10/25/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectPanelExperiment extends ProjectExperiment {
    static mapping = {
        discriminator value: "panel"
        panelExperiment(column: "PANEL_EXPRMT_ID")
    }

    PanelExperiment panelExperiment;

    @Override
    Collection<Experiment> getExperiments() {
        return Collections.unmodifiableCollection(panelExperiment.experiments);
    }
}
