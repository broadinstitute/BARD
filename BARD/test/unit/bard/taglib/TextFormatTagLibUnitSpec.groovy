package bard.taglib

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jlev
 * Date: 6/10/13
 * Time: 3:31 PM
 * To change this template use File | Settings | File Templates.
 */
/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(TextFormatTagLib)
@Unroll
class TextFormatTagLibUnitSpec extends Specification {
    void setup() {
        // Setup logic here
    }

    void cleanup() {
        // Tear down logic here
    }

    def "test renderWithBreaks #label"() {
        given:
        Map documentContent = ["text": content] as Map

        when:
        String result = new TextFormatTagLib().renderWithBreaks(documentContent, null)

        then:
        assert result == expectedResult

        where:
        label                     | content                   | expectedResult
        "text with no line break" | "text with no line break" | "<br/>text with no line break"
        "text with line break"    | "text with\nline break"   | "<br/>text with<br/>line break"
        "null content"            | null                      | ""
        "empty string"            | ""                        | "<br/>"
    }
}
