package bard.db.guidance

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by ddurkin on 1/29/14.
 */
@Unroll
class ContextUtilsUnitSpec extends Specification {


    void "test all rules called #desc"() {
        final List<GuidanceRule> rules = []
        numOfRules.times {
            rules.add(Mock(GuidanceRule))
        }

        when:
        final List<Guidance> guidanceMessages = GuidanceUtils.getGuidance(rules)

        then:
        numOfRules * _.getGuidance() >>> returnMessages // NOTE: >>>  see http://code.google.com/p/spock/wiki/Interactions iterator returns different values for each call
        0 * _._
        guidanceMessages.message == expectedMessages

        where:
        desc                 | numOfRules | expectedMessages           | returnMessages
        '1 rule 2 messages'  | 1          | ['message 1', 'message 2'] | [[new DefaultGuidanceImpl('message 1'), new DefaultGuidanceImpl('message 2')]]
        '2 rules 1 msg each' | 2          | ['message 1', 'message 2'] | [[new DefaultGuidanceImpl('message 1')], [new DefaultGuidanceImpl('message 2')]]
    }
}
