package bard.db.util

import bard.db.people.Role
import grails.buildtestdata.mixin.Build
import grails.plugins.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification
import spock.lang.Unroll
import util.BardUser

/**
 * See the API for {@link grails.test.mixin.web.ControllerUnitTestMixin} for usage instructions
 */
@TestFor(DownTimeSchedulerController)
@Unroll
@Build([DownTimeScheduler])
@Mock([DownTimeScheduler])
@TestMixin(GrailsUnitTestMixin)
class DownTimeSchedulerControllerUnitSpec extends Specification {
    DownTimeSchedulerCommand downTimeSchedulerCommand
    SpringSecurityService springSecurityService
    String createdBy
    Role adminRole
    String displayValue
    String downTimeAsString
    Date downTime

    def setup() {
        this.springSecurityService = Mock(SpringSecurityService)
        this.controller.springSecurityService = this.springSecurityService
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

    void "test Index"() {
        when:
        controller.index()
        then:
        assert controller.response.redirectedUrl == "/downTimeScheduler/list"
    }

    void "test show"() {
        given:
        DownTimeScheduler downTimeScheduler = DownTimeScheduler.build()
        when:
        def model = controller.show(downTimeScheduler.id)

        then:
        assert model.downTimeScheduler
        assert downTimeScheduler == model.downTimeScheduler
    }

    void "test list"() {
        given:
        DownTimeScheduler downTimeScheduler = DownTimeScheduler.build()
        when:
        def model = controller.list()

        then:
        assert model.downTimeSchedulerList
        assert downTimeScheduler == model.downTimeSchedulerList.get(0)
    }

    void "test empty list"() {
        when:
        def model = controller.list()

        then:
        assert !model.downTimeSchedulerList

    }

    void "test create with no user name"() {
        when:
        def model = controller.create(new DownTimeSchedulerCommand())
        then:
        1 * controller.springSecurityService.getPrincipal() >> { new BardUser(username: this.createdBy, authorities: [this.adminRole]) }
        assert model.downTimeSchedulerCommand.createdBy == this.createdBy

    }

    void "test create with user name"() {
        when:
        def model = controller.create(new DownTimeSchedulerCommand(createdBy: this.createdBy))
        then:
        0 * controller.springSecurityService.principal.username >> { "some value" }
        assert model.downTimeSchedulerCommand.createdBy == this.createdBy

    }

    void "test current Shut Down Info"() {
        given:
        def today = new Date() //represents the date and time when it is created
        def tomorrow = today + 1
        DownTimeScheduler.build(downTime: tomorrow, displayValue: this.displayValue)
        when:
        controller.currentDownTimeInfo()
        then:
        assert response.text.contains(displayValue)
    }

    void "test save with errors"() {

        given:
        final DownTimeSchedulerCommand command = new DownTimeSchedulerCommand()
        command.validate()
        when:
        controller.save(command)
        then:
        assert controller.response.redirectedUrl == "/downTimeScheduler/create"
    }

    void "test save"() {
        when:
        controller.save(this.downTimeSchedulerCommand)
        then:
        assert view == "/downTimeScheduler/show"
    }

}
