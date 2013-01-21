package bard.core.exceptions

import bard.core.DataSource
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class WebQueryExceptionUnitSpec extends Specification {

    @Shared
    String message = "message"
    @Shared
    Throwable throwable = new Exception(message)


    void "test Constructors #label"() {
        when:
        webQueryException.toString()
        then:
        assert webQueryException.message == expectedMessage
        assert webQueryException.getCause() == expectedThrowable
        where:
        label                              | webQueryException                         | expectedMessage                | expectedThrowable
        "empty arg constructor"            | new WebQueryException()                   | null                           | null
        "2 arg constructor"                | new WebQueryException(message, throwable) | message                        | throwable
        "1 arg constructor with throwable" | new WebQueryException(throwable)          | "java.lang.Exception: message" | throwable
        "1 arg constructor with message"   | new WebQueryException(message)            | message                        | null
    }


}

