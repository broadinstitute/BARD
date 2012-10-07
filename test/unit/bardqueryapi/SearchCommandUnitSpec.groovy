package bardqueryapi

import spock.lang.Specification
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.mixin.TestFor

/**
 * Created with IntelliJ IDEA.
 * User: sbrudz
 * Date: 9/2/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(BardWebInterfaceController)
class SearchCommandUnitSpec extends Specification {
    /**
     * {@link SearchCommand#setSearchString(String)}
     */
    void "test setSearchString"() {
        given:
        SearchCommand searchCommand = new SearchCommand()
        when:
        searchCommand.setSearchString(null)

        then:
        assert !searchCommand.searchString
        assert !searchCommand.validate()
        assert !searchCommand.getAppliedFilters()
    }
    /**
     * {@link SearchCommand#getAppliedFilters()}
     */
    void "test getAppliedFilters"() {
        given:
        final String filterName = "a"
        final String filterValue = "b"
       final String searchString = "test"
        List<SearchFilter> searchFilters = [new SearchFilter(filterName: filterName, filterValue: filterValue),
                new SearchFilter(filterName: filterName, filterValue: "")]
        when:
        SearchCommand searchCommand = new SearchCommand(searchString: searchString, filters: searchFilters)

        then:
        assert searchCommand.getAppliedFilters().size() == 1
        assert searchCommand.searchString ==searchString
        assert searchCommand.getAppliedFilters().get(0).filterName == filterName
        assert searchCommand.getAppliedFilters().get(0).filterValue == filterValue
        assert searchCommand.validate()
    }
}
