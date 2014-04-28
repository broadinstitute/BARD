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

package bardqueryapi

import bard.core.adapter.CompoundAdapter
import bard.core.rest.spring.compounds.Compound
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.awt.image.BufferedImage

/**
 * Created with IntelliJ IDEA.
 * User: jasiedu
 * Date: 10/7/12
 * Time: 10:35 PM
 * To change this template use File | Settings | File Templates.
 */
@TestMixin(GrailsUnitTestMixin)
@TestFor(ChemAxonController)
@Unroll
class ChemAxonControllerUnitSpec extends Specification {
    ChemAxonService chemAxonService
    QueryService queryService
    BardUtilitiesService bardUtilitiesService
    @Shared
    CompoundAdapter compoundAdapter = buildCompoundAdapter(1234, 'C1=CC2=C(C=C1)C=CC=C2')

    void setup() {
        controller.metaClass.mixin(InetAddressUtil)
        bardUtilitiesService = Mock(BardUtilitiesService)
        controller.bardUtilitiesService = bardUtilitiesService
        chemAxonService = Mock(ChemAxonService)
        controller.chemAxonService = this.chemAxonService
        queryService = Mock(QueryService)
        controller.queryService = queryService
    }
    /**
     * {@link ChemAxonController#generateStructureImageFromSmiles}
     */
    void "test generateStructureImage empty smiles - Should return default image"() {
        when:
        controller.generateStructureImageFromSmiles(smiles, width, height)
        final String type = response.contentType

        then:
        chemAxonService.getDefaultImage(width, height) >> { new BufferedImage(width, height, 5) }
        0 * chemAxonService.generateStructurePNG(_, _, _) >> { bytesArra }
        assert type == "image/png"
        assert response.contentAsByteArray
        //assert returnedImage.size() == expectedByteArraySize

        where:
        label             | smiles | width          | height         | bytesArra
        "smiles is empty" | ''     | 200 as Integer | 200 as Integer | [1, 2, 3] as byte[]
        "smiles is null"  | null   | 200 as Integer | 200 as Integer | [1, 2, 3] as byte[]
    }
    /**
     * {@link ChemAxonController#generateStructureImageFromSmiles}
     */
    void "test generateStructureImageFromSmiles #label"() {
        when:
        controller.generateStructureImageFromSmiles(smiles, width, height)
        final byte[] returnedImage = response.contentAsByteArray

        then:
        chemAxonService.generateStructurePNG(_, _, _) >> { bytesArra }
        assert returnedImage.size() == expectedByteArraySize

        where:
        label                          | smiles                  | width          | height         | bytesArra           | expectedByteArraySize
        "get back a byte array"        | 'C1=CC2=C(C=C1)C=CC=C2' | 300 as Integer | 300 as Integer | [1, 2, 3] as byte[] | 3
        "default image size"           | 'C1=CC2=C(C=C1)C=CC=C2' | null           | null           | [1, 2, 3] as byte[] | 3
        "default image size"           | 'C1=CC2=C(C=C1)C=CC=C2' | 0 as Integer   | 0 as Integer   | [1, 2, 3] as byte[] | 3
        "get back an empty byte array" | 'C1=CC2=C(C=C1)C=CC=C2' | 200 as Integer | 200 as Integer | [] as byte[]        | 0
        "smiles is null"               | ''                      | 200 as Integer | 200 as Integer | [1, 2, 3] as byte[] | 0
    }
    /**
     * {@link ChemAxonController#generateStructureImageFromSmiles}
     */
    void "test generateStructureImageFromSmiles - throws Exception"() {
        when:
        controller.generateStructureImageFromSmiles("CC", 10, 10)
        final byte[] returnedImage = response.contentAsByteArray

        then:
        chemAxonService.generateStructurePNG(_, _, _) >> { new Exception() }
        assert returnedImage.size() == 0
    }
    /**
     * {@link ChemAxonController#generateStructureImageFromCID}
     */
    void "test generateStructureImage from CID #label"() {
        when:
        controller.generateStructureImageFromCID(cid, width, height)
        final byte[] returnedImage = response.contentAsByteArray

        then:
        queryService.showCompound(_) >> { compoundAdptr }
        chemAxonService.generateStructurePNG(_, _, _) >> { bytesArra }
        assert returnedImage.size() == expectedByteArraySize

        where:
        label                          | smiles                  | width          | height         | bytesArra           | cid          | expectedByteArraySize | compoundAdptr
        "get back a byte array"        | 'C1=CC2=C(C=C1)C=CC=C2' | 300 as Integer | 300 as Integer | [1, 2, 3] as byte[] | 1234 as Long | 3                     | compoundAdapter
        "no compoundAdapters"          | 'C1=CC2=C(C=C1)C=CC=C2' | 300 as Integer | 300 as Integer | [1, 2, 3] as byte[] | 1234 as Long | 0                     | null
        "get back an empty byte array" | 'C1=CC2=C(C=C1)C=CC=C2' | 200 as Integer | 200 as Integer | [] as byte[]        | 1234 as Long | 0                     | compoundAdapter
        "cid is null"                  | 'C1=CC2=C(C=C1)C=CC=C2' | 300 as Integer | 300 as Integer | [1, 2, 3] as byte[] | null         | 0                     | compoundAdapter
    }

    /**
     * {@link ChemAxonController#generateStructureImageFromCID}
     */
    void "test generateStructureImage from CID - throws Exception"() {
        when:
        controller.generateStructureImageFromCID(200, 200, 200)
        final byte[] returnedImage = response.contentAsByteArray

        then:
        queryService.showCompound(_) >> { new Exception() }
        assert returnedImage.size() == 0
    }

    CompoundAdapter buildCompoundAdapter(final int cid, final String smiles) {
        final Compound compound = new Compound()
        compound.setSmiles(smiles)
        compound.setCid(cid);
        return compoundAdapter = new CompoundAdapter(compound)
    }

    /**
     * {@link ActivityOutcome#getLabel()}
     */
    void "test marvinSketch()"() {
        when:
        controller.marvinSketch()

        then:
        assert controller.response.contentAsString == ''
    }

    /**
     * {@link ChemAxonController#generateStructureImageFromCID}
     */
    void "test converting SMILES structure to Mol format #label"() {
        when:
        controller.convertSmilesToMolFormat(smiles)

        then:
        chemAxonService.convertSmilesToAnotherFormat(smiles, 'mol') >> { molString }
        assert response.contentAsString == expectedMolString
        assert response.status == expectedResponseStatus

        where:
        label                  | smiles                  | molString | expectedMolString | expectedResponseStatus
        "a good smiles string" | 'C1=CC2=C(C=C1)C=CC=C2' | 'mol'     | 'mol'             | 200
        "a bad smiles string"  | 'bad smiles'            | null      | ''                | 400
        "a null smiles string" | null                    | null      | ''                | 400
    }
}
