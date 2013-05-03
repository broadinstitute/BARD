package bard.db.dictionary

import spock.lang.Specification
import grails.buildtestdata.mixin.Build

/**
 * Created with IntelliJ IDEA.
 * User: dlahr
 * Date: 4/24/13
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
@Build([Element, ElementHierarchy])
class ElementAndFullPathUnitSpec extends Specification {
    private static final String delimeter = "/"

    void "simple test splitPathIntoTokens"() {
        setup:
        String pathString = """
      /a / b/c /d /    """

        when:
        List<String> tokenList = ElementAndFullPath.splitPathIntoTokens(pathString, delimeter)

        then:
        tokenList.size() == 4
        tokenList.get(0) == "a"
        tokenList.get(1) == "b"
        tokenList.get(2) == "c"
        tokenList.get(3) == "d"
    }

    void "empty test splitPathIntoTokens"() {
        setup:
        String pathString = "/"

        when:
        List<String> tokenList = ElementAndFullPath.splitPathIntoTokens(pathString, delimeter)

        then:
        tokenList.size() == 0
    }

    void "invalid path test entries before first delimeter"() {
        setup:
        String pathString = "abcd/efghij/"

        when:
        ElementAndFullPath.splitPathIntoTokens(pathString, delimeter)

        then:
        thrown(InvalidElementPathStringException)
    }

    void "invalid path test entries after last delimeter"() {
        setup:
        String pathString = "/abcd/efghij"

        when:
        ElementAndFullPath.splitPathIntoTokens(pathString, delimeter)

        then:
        thrown(InvalidElementPathStringException)
    }

    void "no delimeters just text"() {
        setup:
        String pathString = "abcd"

        when:
        ElementAndFullPath.splitPathIntoTokens(pathString, delimeter)

        then:
        thrown(InvalidElementPathStringException)
    }

    void "test pathContains"() {
        setup:
        ElementHierarchy eh0 = ElementHierarchy.build()
        ElementHierarchy eh1 = ElementHierarchy.build()
        Element element = Element.build()

        ElementAndFullPath elementAndFullPath = new ElementAndFullPath()
        elementAndFullPath.path.add(eh0)
        elementAndFullPath.path.add(eh1)

        when:
        boolean expectTrue = elementAndFullPath.pathContainsElement(eh0.childElement)
        boolean expectFalse = elementAndFullPath.pathContainsElement(element)

        then:
        true == expectTrue
        false == expectFalse
    }
}
