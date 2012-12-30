package bard.core.helper

import org.apache.commons.lang3.time.StopWatch
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class LoggerServiceUnitSpec extends Specification {

    LoggerService loggerService

    void setup() {
        this.loggerService = new LoggerService()
    }

    void tearDown() {
        // Tear down logic here
    }

    void "test startStopWatch"() {
        given:

        when:
        StopWatch sw = loggerService.startStopWatch()

        then:
        assert sw.nanoTime > 0
    }

    void "test stopStopWatch"() {
        given:
        StopWatch sw = new StopWatch()
        sw.start()

        when:
        loggerService.stopStopWatch(sw, "test string")

        then:
        assert true
    }
}

