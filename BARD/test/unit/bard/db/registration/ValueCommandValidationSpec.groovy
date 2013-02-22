package bard.db.registration

import grails.test.mixin.*
import grails.test.mixin.support.*

import org.junit.*

import spock.lang.Specification
import bard.db.registration.additemwizard.CopyFixedValueCommand

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class ValueCommandValidationSpec extends Specification {
	
	void 'test command object'() {
		when:
			CopyFixedValueCommand cmd;
			cmd = new CopyFixedValueCommand()
			mockForConstraintsTests CopyFixedValueCommand, [cmd]
	
		then:
			cmd.validate()
	}
	
}
