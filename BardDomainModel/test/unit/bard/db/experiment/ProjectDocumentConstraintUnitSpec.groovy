package bard.db.experiment

import bard.db.model.AbstractDocumentConstraintUnitSpec
import grails.buildtestdata.mixin.Build
import org.junit.Before
import spock.lang.Unroll
import bard.db.project.ProjectDocument

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 11/5/12
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */

@Build(ProjectDocument)
@Unroll
class ProjectDocumentConstraintUnitSpec extends AbstractDocumentConstraintUnitSpec {

    @Before
    @Override
    void doSetup() {
        domainInstance = ProjectDocument.buildWithoutSave()
    }
}