/* Copyright (c) 2014, The Broad Institute
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of The Broad Institute nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL The Broad Institute BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
