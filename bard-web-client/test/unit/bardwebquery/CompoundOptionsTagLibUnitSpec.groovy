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
        def template = '<g:compoundOptions cid="${cid}" smiles="${smiles}" sid="${sid}" imageWidth="${imageWidth}" imageHeight="${imageHeight}"/>'
        when:
        String actualResults = applyTemplate(template, [cid: cid, smiles: smiles, sid: sid, imageWidth: imageWidth, imageHeight: imageHeight]).toString()
        then:
        assert actualResults.contains("chemAxon/generateStructureImageFromSmiles?smiles=CC&width=1&height=1")
        assert actualResults.contains("Search For Analogs")
        where:
        label                  | cid | smiles | sid | imageWidth | imageHeight
        "Template with Smiles" | 1   | 'CC'   | 1   | 1          | 1
    }
}
