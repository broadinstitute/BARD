package bard.core.rest.spring.experiment

import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ResponseClassEnumUnitSpec extends Specification {


    void "test response class enum"() {
        when:
        ResponseClassEnum responseClassEnum = ResponseClassEnum.SP
        then:
        assert responseClassEnum.isIsMapped()
        assert ResponseClassEnum.toEnum("SP") == ResponseClassEnum.SP
    }

}

