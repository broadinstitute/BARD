package bard.db.util

import bard.db.people.Role
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.TestFor
import org.springframework.validation.Errors
import spock.lang.Specification
import spock.lang.Unroll
import util.BardUser

import static test.TestUtils.assertFieldValidationExpectations
import static test.TestUtils.createString

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(DownTimeSchedulerController)
@Unroll
class DownTimeSchedulerCommandUnitSpec extends Specification {
    DownTimeSchedulerCommand downTimeSchedulerCommand
    SpringSecurityService springSecurityService
    String createdBy
    Role adminRole
    String displayValue
    String downTimeAsString
    Date downTime

    def setup() {
        this.springSecurityService = Mock(SpringSecurityService)
        this.createdBy = "Me"
        this.adminRole = new Role(authority: "ROLE_BARD_ADMINISTRATOR")
        def today = new Date()
        this.downTime = today + 1

        this.downTimeAsString = DownTimeScheduler.dateFormat.format(this.downTime)
        this.displayValue = "Some display"
        downTimeSchedulerCommand = new DownTimeSchedulerCommand()
        downTimeSchedulerCommand.displayValue = displayValue
        downTimeSchedulerCommand.springSecurityService = springSecurityService
        downTimeSchedulerCommand.createdBy = createdBy
        downTimeSchedulerCommand.downTimeAsString = downTimeAsString
    }
    /****
     *
     * Test Command object validations
     *
     */
    void "testInvalidCommandObjectState downTimeAsString for save #desc"() {
        given:
        final String field = 'downTimeAsString'

        when:
        downTimeSchedulerCommand[(field)] = valueUnderTest
        downTimeSchedulerCommand.validate()
        then: 'verify valid or invalid for expected reason'
        2 * downTimeSchedulerCommand.springSecurityService.getPrincipal() >> { new BardUser(username: this.createdBy, authorities: [this.adminRole]) }
        assertFieldValidationExpectations(downTimeSchedulerCommand, field, valid, errorCode)

        and: 'verify domain can be persisted to the db'
        if (valid) {
            assert createdBy == downTimeSchedulerCommand.createdBy
            assert downTimeSchedulerCommand.displayValue
            assert downTimeAsString == downTimeSchedulerCommand.downTimeAsString
        }

        where:
        desc                      | valueUnderTest | valid | errorCode
        'display value is null '  | null           | false | 'nullable'
        'display value is blank ' | '   '          | false | 'blank'
    }

    void "testInvalidCommandObjectState downTimeAsString fail for save #desc"() {
        given:
        final String field = 'downTimeAsString'
        def today = new Date()
        def yesterday = today - 1
        String expectedErrorCode = "downtimescheduler.bard.downtime.valid"
        when:
        downTimeSchedulerCommand[(field)] = DownTimeScheduler.dateFormat.format(yesterday)
        downTimeSchedulerCommand.validate()
        then: 'verify valid or invalid for expected reason'
        2 * downTimeSchedulerCommand.springSecurityService.getPrincipal() >> { new BardUser(username: this.createdBy, authorities: [this.adminRole]) }
        assert downTimeSchedulerCommand.hasErrors()
        final Errors errors = downTimeSchedulerCommand.errors
        assert errors.errorCount == 1
        String foundErrorCode = ""
        errors.allErrors.each {
            foundErrorCode = it?.codes?.find { it == expectedErrorCode }
        }
        assert expectedErrorCode == foundErrorCode


    }

    void "testInvalidCommandObjectState displayvalue for save #desc"() {
        given:
        final String field = 'displayValue'

        when:
        downTimeSchedulerCommand[(field)] = valueUnderTest
        downTimeSchedulerCommand.validate()
        then: 'verify valid or invalid for expected reason'
        2 * downTimeSchedulerCommand.springSecurityService.getPrincipal() >> { new BardUser(username: this.createdBy, authorities: [this.adminRole]) }
        assertFieldValidationExpectations(downTimeSchedulerCommand, field, valid, errorCode)

        and: 'verify domain can be persisted to the db'
        if (valid) {
            assert createdBy == downTimeSchedulerCommand.createdBy
            assert downTimeSchedulerCommand.displayValue
            assert downTimeAsString == downTimeSchedulerCommand.downTimeAsString
        }

        where:
        desc                             | valueUnderTest                                               | valid | errorCode
        'display value is null '         | null                                                         | false | 'nullable'
        'display value is blank '        | '   '                                                        | false | 'blank'
        'display value too long'         | createString(DownTimeScheduler.DISPLAY_VALUE_MAX_SIZE + 1) | false | 'maxSize.exceeded'
        'display value exactly at limit' | createString(DownTimeScheduler.DISPLAY_VALUE_MAX_SIZE)     | true  | null

    }

    void "testInvalidCommandObjectState modified by for save #desc"() {
        given:
        final String field = 'createdBy'

        when:
        downTimeSchedulerCommand[(field)] = valueUnderTest
        downTimeSchedulerCommand.validate()
        then: 'verify valid or invalid for expected reason'
        numberOfGetPrincipalInvocations * downTimeSchedulerCommand.springSecurityService.getPrincipal() >> { new BardUser(username: username, authorities: [userRole]) }
        assertFieldValidationExpectations(downTimeSchedulerCommand, field, valid, errorCode)

        and: 'verify domain can be persisted to the db'
        if (valid) {
            assert valueUnderTest == downTimeSchedulerCommand.createdBy
            assert downTimeSchedulerCommand.displayValue
            assert downTimeAsString == downTimeSchedulerCommand.downTimeAsString
        }

        where:
        desc                          | valueUnderTest                                            | username                                                  | userRole                                       | valid | errorCode          | numberOfGetPrincipalInvocations
        'created by is null '         | null                                                      | null                                                      | new Role(authority: "ROLE_BARD_ADMINISTRATOR") | false | 'nullable'         | 0
        'created by is blank '        | '  '                                                      | '  '                                                      | new Role(authority: "ROLE_BARD_ADMINISTRATOR") | false | 'blank'            | 0
        'created by too long'         | createString(DownTimeScheduler.CREATED_BY_MAX_SIZE + 1) | createString(DownTimeScheduler.CREATED_BY_MAX_SIZE + 1) | new Role(authority: "ROLE_BARD_ADMINISTRATOR") | false | 'maxSize.exceeded' | 2
        'created by exactly at limit' | createString(DownTimeScheduler.CREATED_BY_MAX_SIZE)     | createString(DownTimeScheduler.CREATED_BY_MAX_SIZE)     | new Role(authority: "ROLE_BARD_ADMINISTRATOR") | true  | null               | 2
    }


    void "testInvalidCommandObjectState created by user not found for save #desc"() {
        given:
        final String field = 'createdBy'

        when:
        downTimeSchedulerCommand[(field)] = valueUnderTest
        downTimeSchedulerCommand.validate()
        then: 'verify valid or invalid for expected reason'
        numberOfGetPrincipalInvocations * downTimeSchedulerCommand.springSecurityService.getPrincipal() >> { null }
        assert downTimeSchedulerCommand.hasErrors()
        final Errors errors = downTimeSchedulerCommand.errors
        assert errors.errorCount == 1
        String foundErrorCode = ""
        errors.allErrors.each {
            foundErrorCode = it?.codes?.find { it == errorCode }
        }
        assert errorCode == foundErrorCode


        where:
        desc                                | valueUnderTest                                        | username    | userRole                                       | valid | errorCode                                | numberOfGetPrincipalInvocations
        'created by mismatch in user names' | createString(DownTimeScheduler.CREATED_BY_MAX_SIZE) | "Some Name" | new Role(authority: "ROLE_BARD_ADMINISTRATOR") | false | 'downtimeschedulerCommand.username.not.found' | 1

    }

    void "testInvalidCommandObjectState created by other validations for save #desc"() {
        given:
        final String field = 'createdBy'

        when:
        downTimeSchedulerCommand[(field)] = valueUnderTest
        downTimeSchedulerCommand.validate()
        then: 'verify valid or invalid for expected reason'
        numberOfGetPrincipalInvocations * downTimeSchedulerCommand.springSecurityService.getPrincipal() >> { new BardUser(username: username, authorities: [userRole]) }
        assert downTimeSchedulerCommand.hasErrors()
        final Errors errors = downTimeSchedulerCommand.errors
        assert errors.errorCount == 1
        String foundErrorCode = ""
        errors.allErrors.each {
            foundErrorCode = it?.codes?.find { it == errorCode }
        }
        assert errorCode == foundErrorCode


        where:
        desc                                 | valueUnderTest                                        | username                                              | userRole                                       | valid | errorCode                                         | numberOfGetPrincipalInvocations
        'modified by mismatch in user names' | createString(DownTimeScheduler.CREATED_BY_MAX_SIZE) | "Some Name"                                           | new Role(authority: "ROLE_BARD_ADMINISTRATOR") | false | 'downtimeschedulerCommand.username.mismatch'           | 1
        'modified by user not an admin'      | createString(DownTimeScheduler.CREATED_BY_MAX_SIZE) | createString(DownTimeScheduler.CREATED_BY_MAX_SIZE) | new Role(authority: "ROLE_BARD_CURATOR")       | false | 'downtimeschedulerCommand.bard.administrator.required' | 2

    }

}
