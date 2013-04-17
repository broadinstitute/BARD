package bard.db.project

import bard.db.model.StandardContextItemValueValidationUnitSpec
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 8/23/12
 * Time: 12:31 AM
 * To change this template use File | Settings | File Templates.
 */
@Build(ProjectExperimentContextItem)
@Unroll
class ProjectExperimentContextItemValueValidationUnitSpec extends StandardContextItemValueValidationUnitSpec<ProjectExperimentContextItem> {
    @Before
    void doSetup() {
        domainInstance = ProjectExperimentContextItem.buildWithoutSave()
    }
}