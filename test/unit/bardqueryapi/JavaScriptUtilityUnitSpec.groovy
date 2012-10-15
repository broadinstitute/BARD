package bardqueryapi

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 10/7/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
class JavaScriptUtilityUnitSpec extends Specification {
    /**
     * {@link bardqueryapi.JavaScriptUtility.cleanup ( String )}
     */
    void "test cleanup with String arg"() {
        when:
        String cleanedUp = bardqueryapi.JavaScriptUtility.cleanup(cleanUpString)
        then:
        assert cleanedUp == expectedLabel
        where:
        label                        | cleanUpString | expectedLabel
        "Empty String"               | ""            | ""
        "Null String"                | null          | ""
        "String with no appostrophe" | "Stuff"       | "Stuff"
        "String with appostrophe"    | "Stuff's"     | "Stuff\\'s"
    }
    /**
     * {@link bardqueryapi.JavaScriptUtility.cleanup ( Long )}
     */
    void "test cleanup with Null Long arg"() {
        given:
        final Long nullLong = null
        when:
        String cleanedUp = bardqueryapi.JavaScriptUtility.cleanup(nullLong)
        then:
        assert cleanedUp == ""
    }
    /**
     * {@link bardqueryapi.JavaScriptUtility.cleanup ( Long )}
     */
    void "test cleanup with Long arg #label"() {
        when:
        String cleanedUp = bardqueryapi.JavaScriptUtility.cleanup(cleanedUpLong)
        then:
        assert cleanedUp == expectedLabel
        where:
        label    | cleanedUpLong | expectedLabel
        "Null"   | null as Long  | ""
        "Zero"   | 0 as Long     | ""
        "A Long" | 20            | "20"
    }

}
