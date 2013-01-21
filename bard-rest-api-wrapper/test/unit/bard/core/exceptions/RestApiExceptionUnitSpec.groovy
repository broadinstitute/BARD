package bard.core.exceptions

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class RestApiExceptionUnitSpec extends Specification {

    @Shared
    String message = "message"
    @Shared
    Throwable throwable = new Exception(message)


    void "test Constructors #label"() {
        when:
        restApiException.toString()
        then:
        assert restApiException.message == expectedMessage
        assert restApiException.getCause() == expectedThrowable
        where:
        label                              | restApiException                         | expectedMessage                | expectedThrowable
        "empty arg constructor"            | new RestApiException()                   | null                           | null
        "2 arg constructor"                | new RestApiException(message, throwable) | message                        | throwable
        "1 arg constructor with throwable" | new RestApiException(throwable)          | "java.lang.Exception: message" | throwable
        "1 arg constructor with message"   | new RestApiException(message)            | message                        | null
    }


}

