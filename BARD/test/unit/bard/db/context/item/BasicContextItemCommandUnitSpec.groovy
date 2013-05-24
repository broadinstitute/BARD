package bard.db.context.item

import spock.lang.Specification
import spock.lang.Unroll


@Unroll
class BasicContextItemCommandUnitSpec extends Specification {
	
	BasicContextItemCommand cmd;

	def setup() {		
		cmd = new BasicContextItemCommand()
	}

	def cleanup() {
	}

	void "test convertValues"() {
		
	}
}