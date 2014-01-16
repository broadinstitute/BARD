package bard.validation.extext

import bard.validation.ext.ExternalItem
import grails.test.mixin.TestFor
import grails.test.mixin.support.GrailsUnitTestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import org.codehaus.groovy.grails.plugins.codecs.URLCodec
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created with IntelliJ IDEA.
 * User: ddurkin
 * Date: 1/8/14
 * Time: 10:53 AM
 * To change this template use File | Settings | File Templates.
 */
@Unroll
@Mixin(GrailsUnitTestMixin)
class BardDelegatingExternalOntologyUnitSpec extends Specification {


    BardDelegatingExternalOntology bdeo

    RestTemplate restTemplate = Mock(RestTemplate)

    void setupSpec() {
        mockCodec(URLCodec.class)
    }

    void setup() {
        bdeo = new BardDelegatingExternalOntology("externalOntolgoyProxyUrlBase", 'externalUrl', restTemplate)
    }

    void "test buildUrl #desc "() {

        when:
        final String actualUrl = bdeo.buildUrl(baseUrl, action, params)

        then:
        actualUrl == expectedUrl

        where:
        desc              | baseUrl | action | params                                                                                                     | expectedUrl
        "no url encoding" | 'foo'   | 'bar'  | [p1: 'a', p2: 'b']                                                                                         | "foo/bar?p1=a&p2=b"
        "url encoding"    | 'foo'   | 'bar'  | [p1: 'a b', p2: 'c']                                                                                       | "foo/bar?p1=a+b&p2=c"
        "url encoding"    | 'foo'   | 'bar'  | [externalUrl: 'http://amigo.geneontology.org/cgi-bin/amigo/term_details?subtree=cellular_component&term='] | "foo/bar?externalUrl=http%3A%2F%2Famigo.geneontology.org%2Fcgi-bin%2Famigo%2Fterm_details%3Fsubtree%3Dcellular_component%26term%3D"
    }

}
