package bard.db.project

import bard.db.experiment.Experiment

/**
 * Created with IntelliJ IDEA.
 * User: pmontgom
 * Date: 10/25/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectSingleExperiment extends ProjectExperiment {
    static mapping = {
        discriminator value: "single"
    }

    Experiment experiment

    static constraints = {
        experiment()
    }
}
