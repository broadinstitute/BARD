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

package bardwebquery

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import querycart.QueryCartService
import querycart.QueryItem
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

@TestFor(CompoundOptionsTagLib)
@Unroll
@Mock([SaveToCartButtonTagLib, QueryItem, QueryCartService])
class CompoundOptionsTagLibUnitSpec extends Specification {


    void "test compound Options #label"() {
        given:
        def template = '<g:compoundOptions cid="${cid}" smiles="${smiles}" sid="${sid}" imageWidth="${imageWidth}" imageHeight="${imageHeight}" name="${name}"/>'

        when:
        String actualResults = applyTemplate(template, [cid: cid, smiles: smiles, sid: sid, imageWidth: imageWidth, imageHeight: imageHeight, name: name]).toString()

        then:
        assert actualResults.contains("chemAxon/generateStructureImageFromSmiles?smiles=CC&width=1&height=1")
        assert actualResults.contains("Search For Analogs")
        assert actualResults.contains("/molSpreadSheet/showExperimentDetails?cid=1&amp;transpose=true")
        assert actualResults.contains("data-cart-name=\"${name}\"")
        assert actualResults.contains('data-cart-type="Compound"')
        assert actualResults.contains("data-cart-smiles=\"${smiles}\"")

        where:
        label                  | cid | smiles | sid | imageWidth | imageHeight | name
        "Template with Smiles" | 1   | 'CC'   | 1   | 1          | 1           | 'anyName'
    }
}
