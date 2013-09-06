package bard.db.project

import bard.db.registration.AssayContextItem
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll
import bard.db.model.AbstractContextItemConstraintUnitSpec
import bard.db.project.ProjectContextItem

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */
@Build(ProjectContextItem)
@Unroll
class ProjectContextItemConstraintUnitSpec extends AbstractContextItemConstraintUnitSpec<ProjectContextItem> {

    @Before
    @Override
    void doSetup() {
        this.domainInstance = constructInstance([:])
    }

    ProjectContextItem constructInstance(Map props) {
        def instance = ProjectContextItem.buildWithoutSave(props)
        instance.attributeElement.save(failOnError:true, flush: true)

        return instance
    }
}
