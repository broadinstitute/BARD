package bardwebquery

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestMixin(GrailsUnitTestMixin)
class TextBlockTagLibSpec {

    void setUp() {
        // Setup logic here
    }

    void tearDown() {
        // Tear down logic here
    }

    @Test
    void testReplaceOfLineBreaks() {
        String text = "test\ntest";
        String results = new TextBlockTagLib().textBlock(null, text);
        assert results == "<p>test</p><p>test</p>"
    }

    @Test
    void testLinkify() {
        String text = "http://google.com";
        String results = new TextBlockTagLib().textBlock(null, text);
        assert results == "<a href=\"http://google.com\">http://google.com</a>"
    }

}
