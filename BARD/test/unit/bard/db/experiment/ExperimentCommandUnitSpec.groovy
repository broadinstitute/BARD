package bard.db.experiment

import bard.db.people.Role
import bard.db.project.ExperimentCommand
import bard.db.project.ExperimentController
import bard.db.registration.Assay
import bard.db.util.DownTimeSchedulerController
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils
import spock.lang.Specification
import spock.lang.Unroll
import util.BardUser

import static java.util.Calendar.YEAR
import static test.TestUtils.assertFieldValidationExpectations

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(ExperimentController)
@Build([Assay, Role])
@Unroll
class ExperimentCommandUnitSpec extends Specification {
    SpringSecurityService springSecurityService


    def setup() {
        this.springSecurityService = Mock(SpringSecurityService)

        SpringSecurityUtils.metaClass.'static'.ifAnyGranted = { String role ->
            return true
        }
    }


    void "test validate assayId #desc"() {
        given:
        Role role = Role.build(authority: "ROLE_TEAM_A")
        ExperimentCommand experimentCommand = new ExperimentCommand(experimentName: "Some Name", ownerRole: role.authority)
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [role]
        }
        when:
        experimentCommand[(field)] = valueUnderTest.call()?.id
        experimentCommand.validate()
        then:
        assertFieldValidationExpectations(experimentCommand, field, valid, errorCode)
        where:
        desc                | valueUnderTest    | valid | errorCode  | field
        'assay id is null'  | { null }          | false | 'nullable' | "assayId"
        'assay Id is valid' | { Assay.build() } | true  | null       | "assayId"
    }

    void "test validate ownerRole #desc"() {
        given:
        String role = null
        ExperimentCommand experimentCommand = new ExperimentCommand(experimentName: "Some Name", assayId: Assay.build().id)
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [role]
        }
        when:
        experimentCommand[(field)] = role
        experimentCommand.validate()
        then:
        assertFieldValidationExpectations(experimentCommand, field, valid, errorCode)
        where:
        desc                 | valid | errorCode  | field
        'owner Role is null' | false | 'nullable' | "ownerRole"
    }

    void "test validate ownerRole - success - #desc"() {
        given:
        Role role = Role.build(authority: "ROLE_TEAM_A")
        ExperimentCommand experimentCommand = new ExperimentCommand(experimentName: "Some Name", assayId: Assay.build().id)
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [role]
        }
        when:
        experimentCommand[(field)] = role.authority
        experimentCommand.validate()
        then:
        assertFieldValidationExpectations(experimentCommand, field, valid, errorCode)
        where:
        desc                  | valid | errorCode | field
        'owner Role is valid' | true  | null      | "ownerRole"
    }

    void "test validate experiment name and description #desc"() {
        given:
        Role role = Role.build(authority: "ROLE_TEAM_A")
        Assay assay = Assay.build()
        ExperimentCommand experimentCommand = new ExperimentCommand(experimentName: "Some Name", ownerRole: role.authority, assayId: assay.id)
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [role]
        }
        when:
        experimentCommand[(field)] = valueUnderTest
        experimentCommand.validate()
        then:
        assertFieldValidationExpectations(experimentCommand, field, valid, errorCode)
        where:
        desc                       | valueUnderTest     | valid | errorCode  | field
        'Experiment Name is null'  | null               | false | 'nullable' | "experimentName"
        'Experiment Name is blank' | '   '              | false | 'blank'    | "experimentName"
        'Experiment Name is valid' | 'Some Name'        | true  | null       | "experimentName"

        'Description is null'      | null               | true  | null       | "description"
        'Description is blank'     | '   '              | false | 'blank'    | "description"
        'Description is valid'     | 'Some Description' | true  | null       | "description"
    }

    void "test validate date fields #desc"() {
        given:
        Role role = Role.build(authority: "ROLE_TEAM_A")
        Assay assay = Assay.build()
        ExperimentCommand experimentCommand = new ExperimentCommand(experimentName: "Some Name", ownerRole: role.authority, assayId: assay.id)
        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
            return [role]
        }
        when:
        experimentCommand[(field)] = valueUnderTest
        experimentCommand.validate()
        then:
        assertFieldValidationExpectations(experimentCommand, field, valid, errorCode)
        where:
        desc                       | valueUnderTest | valid | errorCode                        | field
        'Run Date From is null'    | null           | true  | null                             | "runDateFrom"
        'Run Date From is blank'   | '   '          | true  | null                             | "runDateFrom"
        'Run Date From is invalid' | "My Date"      | false | 'experimentCommand.date.invalid' | "runDateFrom"
        'Run Date From is valid'   | '04/25/2012'   | true  | null                             | "runDateFrom"

        'Run Date To is null'      | null           | true  | null                             | "runDateTo"
        'Run Date To is blank'     | '   '          | true  | null                             | "runDateTo"
        'Run Date To is invalid'   | "My Date"      | false | 'experimentCommand.date.invalid' | "runDateTo"
        'Run Date To is valid'     | '04/25/2012'   | true  | null                             | "runDateTo"

        // 'HUD is null'              | null           | true  | null                             | "holdUntilDate"
        //'HUD is blank'             | '   '          | true  | null                             | "holdUntilDate"
    }

//    void "test validate HUD #desc"() {
//        given:
//        Role role = Role.build(authority: "ROLE_TEAM_A")
//        Assay assay = Assay.build()
//        ExperimentCommand experimentCommand = new ExperimentCommand(experimentName: "Some Name", ownerRole: role, assayId: assay.id)
//        SpringSecurityUtils.metaClass.'static'.SpringSecurityUtils.getPrincipalAuthorities = {
//            return [role]
//        }
//
//        Date dateToTest = new Date()
//        def nextYear = dateToTest[YEAR] + yearsToAdd
//        dateToTest.set(year: nextYear)
//
//        when:
//        experimentCommand[(field)] = Experiment.dateFormat.format(dateToTest)
//        experimentCommand.validate()
//        then:
//        assertFieldValidationExpectations(experimentCommand, field, valid, errorCode)
//        where:
//        desc                                        | yearsToAdd | valid | errorCode                    | field
//        ' is less than one year from today'         | 0          | true  | null                         | "holdUntilDate"
//        ' is more is exactly one year from today'   | 1          | true  | null                         | "holdUntilDate"
//        ' is more is more than one year from today' | 2          | false | 'holdUntilDate.max.one.year' | "holdUntilDate"
//    }

    void "test copyFromCmdToDomain"() {
        given:
        Date dateToTest = new Date()
        def nextYear = dateToTest[YEAR] + 1
        dateToTest.set(year: nextYear)

        Role role = Role.build(authority: "ROLE_TEAM_A")
        Assay assay = Assay.build()
        String experimentName = "Name"
        String description = "description"
        String runDateFrom = Experiment.dateFormat.format(new Date())
        String runDateTo = Experiment.dateFormat.format(new Date())
        ExperimentCommand experimentCommand =
            new ExperimentCommand(experimentName: experimentName, ownerRole: role.authority,
                    assayId: assay.id, description: description, runDateFrom: runDateFrom, runDateTo: runDateTo)
        experimentCommand.springSecurityService = this.springSecurityService
        Experiment experiment = new Experiment()
        when:
        experimentCommand.copyFromCmdToDomain(experiment)

        then:

        1 * experimentCommand.springSecurityService.getPrincipal() >> { new BardUser(username: "Stuff", authorities: [role]) }

        assert experiment.assay == experimentCommand.assay
        assert experiment.ownerRole.authority == experimentCommand.ownerRole
        assert experiment.description == experimentCommand.description
        assert experiment.experimentName == experimentCommand.experimentName
        assert Experiment.dateFormat.format(experiment.runDateFrom) == experimentCommand.runDateFrom
        assert Experiment.dateFormat.format(experiment.runDateTo) == experimentCommand.runDateTo

    }

}
