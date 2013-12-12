package bard.db.project

import bard.db.dictionary.Element
import bard.db.dictionary.StageTree
import bard.db.enums.Status
import bard.db.experiment.Experiment
import bard.db.people.Role
import bard.db.registration.Assay
import bard.db.registration.ExternalReference
import bard.db.registration.IdType
import bard.db.registration.MergeAssayDefinitionService
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import org.junit.Before
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import static test.TestUtils.assertFieldValidationExpectations

/**
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 12/9/12
 * Time: 8:30 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(ProjectController)
@Build([Role, Assay, Project, ProjectSingleExperiment, Experiment, ProjectStep, Element, ExternalReference, StageTree])
@Mock([Role, Assay, Project, ProjectSingleExperiment, Experiment, ProjectStep, Element, ExternalReference, StageTree])
@TestMixin(GrailsUnitTestMixin)
@Unroll
class AssociateExperimentsCommandUnitSpec extends Specification {
    @Shared Project project
    ProjectService projectService
    SpringSecurityService springSecurityService
    MergeAssayDefinitionService mergeAssayDefinitionService

    Role role


    @Before
    void setup() {
        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
        role = Role.build()
        springSecurityService = Mock(SpringSecurityService)
        projectService = Mock(ProjectService)
        mergeAssayDefinitionService = Mock(MergeAssayDefinitionService)
    }

    void "test project Id constraints #desc"() {
        given:
        def domainInstance = new AssociateExperimentsCommand()
        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)


        where:
        desc                        | field       | valueUnderTest | valid | errorCode
        'null projectId'            | "projectId" | null           | false | 'nullable'
        'project Id does not exist' | "projectId" | 20000          | false | 'associateExperimentsCommand.project.id.not.found'
    }

    void "test stage Id constraints #desc"() {

        given:
        final Experiment experiment = Experiment.build(ownerRole: role)
        final String experimentIdAsString = experiment.id.toString()

        def domainInstance = new AssociateExperimentsCommand(
                mergeAssayDefinitionService: this.mergeAssayDefinitionService,
                projectService: this.projectService,
                fromAddPage: true,
                projectId: Project.build(ownerRole: role).id,
                sourceEntityIds: experimentIdAsString)
        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'

        timeMethodisInvoked * domainInstance.mergeAssayDefinitionService.convertIdToEntity(_, _) >> { experiment }
        timeMethodisInvoked * domainInstance.projectService.isExperimentAssociatedWithProject(_, _) >> { return false }

        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)


        where:
        desc                      | field     | valueUnderTest | valid | errorCode                                        | timeMethodisInvoked
        'null stageId'            | "stageId" | null           | true  | null                                             | 1
        'stage Id does not exist' | "stageId" | 200000         | false | 'associateExperimentsCommand.stage.id.not.found' | 1
    }


    void "test experiment Ids constraints #desc"() {

        given:
        String field = "experimentIds"
        final Assay assay = Assay.build(ownerRole: role)
        final Experiment experiment = Experiment.build(ownerRole: role)

        def domainInstance = new AssociateExperimentsCommand(
                mergeAssayDefinitionService: this.mergeAssayDefinitionService,
                projectService: this.projectService,
                fromAddPage: true,
                idType: IdType.ADID,
                validateExperimentIds: true,
                projectId: Project.build(ownerRole: role).id,
                sourceEntityIds: assay.id.toString())
        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'

        timeMethodisInvoked * domainInstance.mergeAssayDefinitionService.convertIdToEntity(_, _) >> { experiment }
        timeMethodisInvoked * domainInstance.projectService.isExperimentAssociatedWithProject(_, _) >> { return false }

        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)


        where:
        desc                          | valueUnderTest | valid | errorCode                                             | timeMethodisInvoked
        'null experiment Ids'         | null           | false | 'associateExperimentsCommand.experiment.ids.min.size' | 1
        'experimentId does not exist' | [20000]        | false | 'associateExperimentsCommand.experiment.id.not.found' | 1
    }

    void "test source Entity Ids constraints #desc"() {
        given:
        String field = "sourceEntityIds"
        // final Assay assay = Assay.build(ownerRole: role)
        //final Experiment experiment = Experiment.build(ownerRole: role)

        def domainInstance = new AssociateExperimentsCommand(
                mergeAssayDefinitionService: this.mergeAssayDefinitionService,
                projectService: this.projectService,
                fromAddPage: true,
                idType: idType,
                projectId: Project.build(ownerRole: role).id)
        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)


        where:
        desc                                               | valueUnderTest | idType      | valid | errorCode
        'null sourceEntityIds'                             | null           | IdType.ADID | false | 'associateExperimentsCommand.sourceEntityIds.nullable'
        'blank sourceEntityIds'                            | '   '          | IdType.ADID | false | 'associateExperimentsCommand.sourceEntityIds.blank'
        "non existing sourceEntityIds with ${IdType.ADID}" | "2000"         | IdType.ADID | false | 'associateExperimentsCommand.assayDefinition.id.not.found'
        "non existing sourceEntityIds with ${IdType.AID}"  | "2000"         | IdType.AID  | false | 'associateExperimentsCommand.experiment.aid.not.found'
        "non existing sourceEntityIds with ${IdType.EID}"  | "2000"         | IdType.EID  | false | 'associateExperimentsCommand.experiment.id.not.found'
    }


    void "test source Entity Ids experiment retired #desc"() {
        given:
        String field = "sourceEntityIds"
        final Experiment experiment = Experiment.build(ownerRole: role, experimentStatus: Status.RETIRED)

        def domainInstance = new AssociateExperimentsCommand(
                mergeAssayDefinitionService: this.mergeAssayDefinitionService,
                projectService: this.projectService,
                fromAddPage: true,
                idType: idType,
                projectId: Project.build(ownerRole: role).id)
        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        domainInstance.mergeAssayDefinitionService.convertIdToEntity(_, _) >> { experiment }
        domainInstance.projectService.isExperimentAssociatedWithProject(_, _) >> { return false }


        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)


        where:
        desc                                               | valueUnderTest | idType      | valid | errorCode
        "non existing sourceEntityIds with ${IdType.ADID}" | "2000"         | IdType.ADID | false | 'associateExperimentsCommand.experiment.eid.retired'
        "non existing sourceEntityIds with ${IdType.AID}"  | "2000"         | IdType.AID  | false | 'associateExperimentsCommand.experiment.aid.retired'
        "non existing sourceEntityIds with ${IdType.EID}"  | "2000"         | IdType.EID  | false | 'associateExperimentsCommand.experiment.eid.retired'
    }

    void "test source Entity Ids experiment already associated to Project #desc"() {
        given:
        String field = "sourceEntityIds"
        final Experiment experiment = Experiment.build(ownerRole: role)

        def domainInstance = new AssociateExperimentsCommand(
                mergeAssayDefinitionService: this.mergeAssayDefinitionService,
                projectService: this.projectService,
                fromAddPage: true,
                idType: idType,
                projectId: Project.build(ownerRole: role).id)
        when: 'a value is set for the field under test'
        domainInstance[(field)] = valueUnderTest
        domainInstance.validate()

        then: 'verify valid or invalid for expected reason'
        domainInstance.mergeAssayDefinitionService.convertIdToEntity(_, _) >> { experiment }
        domainInstance.projectService.isExperimentAssociatedWithProject(_, _) >> { return true }


        assertFieldValidationExpectations(domainInstance, field, valid, errorCode)


        where:
        desc                                               | valueUnderTest | idType      | valid | errorCode
        "non existing sourceEntityIds with ${IdType.ADID}" | "2000"         | IdType.ADID | false | 'associateExperimentsCommand.experiment.eid.already.part.project'
        "non existing sourceEntityIds with ${IdType.AID}"  | "2000"         | IdType.AID  | false | 'associateExperimentsCommand.experiment.aid.already.part.project'
        "non existing sourceEntityIds with ${IdType.EID}"  | "2000"         | IdType.EID  | false | 'associateExperimentsCommand.experiment.eid.already.part.project'
    }

    void "test getExperiments idType EID/AID #desc"() {
        given:
        final Experiment experiment = Experiment.build(ownerRole: role)

        def domainInstance = new AssociateExperimentsCommand(
                mergeAssayDefinitionService: this.mergeAssayDefinitionService,
                projectService: this.projectService,
                fromAddPage: true,
                idType: idType,
                projectId: Project.build(ownerRole: role).id,
                sourceEntityIds: experiment.id.toString()
        )
        when:
        List<Experiment> experiments = domainInstance.getExperiments()
        then:
        domainInstance.mergeAssayDefinitionService.convertIdToEntity(_, _) >> { experiment }
        assert experiments
        assert experiments.size() == 1

        where:
        desc       | idType
        "With EID" | IdType.EID
        "With AID" | IdType.AID

    }

    void "test getExperiments idType ADID #desc"() {
        given:
        final Experiment experiment = Experiment.build(ownerRole: role)
        def domainInstance = new AssociateExperimentsCommand(
                mergeAssayDefinitionService: this.mergeAssayDefinitionService,
                projectService: this.projectService,
                fromAddPage: true,
                idType: idType,
                projectId: Project.build(ownerRole: role).id,
                experimentIds: [experiment.id]
        )
        when:
        List<Experiment> experiments = domainInstance.getExperiments()
        then:
        assert experiments
        assert experiments.size() == 1

        where:
        desc        | idType
        "With ADID" | IdType.ADID

    }
}
