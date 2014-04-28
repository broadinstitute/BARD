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
import chemaxon.formats.MolFormatException
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
@TestFor(ChemAxonService)
@Unroll
class ChemAxonServiceUnitSpec extends Specification {
    final static String MOL_STRING = '-0.7145   -1.2375    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0'

    final static SDF_STRING = '10 11  0  0  0  0            999 V2000'

    void setup() {
    }

    /**
     * {@link ChemAxonController#generateStructureImageFromCID}
     */
    void "test converting SMILES structure to another format #label"() {
        when:
        String newFormatString = service.convertSmilesToAnotherFormat(smiles, format)

        then:
        if (newFormatString) {
            assert newFormatString.contains(expectedResponseString)
        } else {
            assert newFormatString == expectedResponseString
        }

        where:
        label                                        | smiles                  | format   | expectedResponseString
        "a good smiles string to MOL format"         | 'C1=CC2=C(C=C1)C=CC=C2' | 'mol'    | MOL_STRING
        "a good smiles string back to smiles format" | 'C1=CC2=C(C=C1)C=CC=C2' | 'smiles' | 'C1=CC2=C(C=C1)C=CC=C2'
        "a good smiles string to SDF format"         | 'C1=CC2=C(C=C1)C=CC=C2' | 'sdf'    | SDF_STRING
        "a null smiles string"                       | null                    | 'mol'    | null
        "a null format string"                       | 'C1=CC2=C(C=C1)C=CC=C2' | ' '      | null
    }


    void "test converting SMILES structure to another format with exception #label"() {
        when:
        service.convertSmilesToAnotherFormat(smiles, format)

        then:
        thrown(MolFormatException)

        where:
        label                 | smiles       | format
        "a bad smiles string" | 'bad smiles' | 'mol'
    }
}
