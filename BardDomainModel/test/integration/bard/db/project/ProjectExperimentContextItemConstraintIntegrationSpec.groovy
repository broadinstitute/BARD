package bard.db.project

import bard.db.experiment.ExperimentContextItem
import bard.db.model.AbstractContextItemIntegrationSpec
import org.junit.Before

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:42 AM
 * To change this template use File | Settings | File Templates.
 */
class ProjectExperimentContextItemConstraintIntegrationSpec extends AbstractContextItemIntegrationSpec<ProjectExperimentContextItem> {

    @Before
    @Override
    void doSetup() {
        this.domainInstance = constructInstance([:])
    }

    ProjectExperimentContextItem constructInstance(Map props) {
        def instance = ProjectExperimentContextItem.buildWithoutSave(props)
        instance.attributeElement.save(failOnError:true, flush: true)

        return instance
    }

}