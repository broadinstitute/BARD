package bardwebquery

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

@TestFor(CompoundOptionsTagLib)
@Unroll
class CompoundOptionsTagLibUnitSpec extends Specification {


    void "test compound Options #label"() {
        given:
        def template = '<g:compoundOptions cid="${cid}" smiles="${smiles}" sid="${sid}" imageWidth="${imageWidth}" imageHeight="${imageHeight}" name="${name}" numActive="${numActive}" numAssays="${numAssays}"/>'

        when:
        String actualResults = applyTemplate(template, [cid: cid, smiles: smiles, sid: sid, imageWidth: imageWidth, imageHeight: imageHeight, name: name, numActive: numActive, numAssays: numAssays]).toString()

        then:
        assert actualResults.contains("chemAxon/generateStructureImageFromSmiles?smiles=CC&width=1&height=1")
        assert actualResults.contains("Search For Analogs")
        assert actualResults.contains("/molSpreadSheet/showExperimentDetails?cid=1&amp;transpose=true")
        assert actualResults.contains("data-cart-name=\"${name}\"")
        assert actualResults.contains('data-cart-type="Compound"')
        assert actualResults.contains("data-cart-smiles=\"${smiles}\"")
        assert actualResults.contains("data-cart-numActive=\"${numActive}\"")
        assert actualResults.contains("data-cart-numAssays=\"${numAssays}\"")

        where:
        label                  | cid | smiles | sid | imageWidth | imageHeight | name      | numActive | numAssays
        "Template with Smiles" | 1   | 'CC'   | 1   | 1          | 1           | 'anyName' | 1         | 2
    }
}
