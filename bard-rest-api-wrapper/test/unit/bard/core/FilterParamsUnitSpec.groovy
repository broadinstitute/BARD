package bard.core

import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.util.FilterParams

@Unroll
class FilterParamsUnitSpec extends Specification {


    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test Constructor"() {
        given:
        String filter = "filter"
        String variables = "b"
        when:
        FilterParams filterParams = new FilterParams(filter, "a")
        filterParams.variables = variables
        then:
        filterParams.filter == filter
        filterParams.variables == variables
        (filterParams.arguments as List<String>).get(0) == "a"

    }

    void "test setFilter"() {
        given:
        String filter = "filter"
        String argument = "a"
        FilterParams filterParams = new FilterParams()
        when:
        filterParams.setFilter(filter, argument)
        then:
        filterParams.filter == filter
        (filterParams.arguments as List<String>).get(0) == argument

    }

}

