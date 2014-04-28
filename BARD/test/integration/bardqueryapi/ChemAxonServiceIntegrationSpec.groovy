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

import grails.plugin.spock.IntegrationSpec
import org.junit.After
import org.junit.Before
import spock.lang.Unroll
import java.awt.image.BufferedImage

@Unroll
class ChemAxonServiceIntegrationSpec extends IntegrationSpec {

    ChemAxonService chemAxonService
    void "test getDefaultImage"(){
        when:
        BufferedImage image = chemAxonService.getDefaultImage(20, 20)
        then:
        assert image
    }
    /**
     * {@link ChemAxonService#generateImageBytes(String, JChemBinFormat)}
     */
    void "test generateImageBytes with valid smiles"() {
        given:
        JChemBinFormat jchemBinFormat = new JChemBinFormat(width: 10, height: 10, imageFormat: 'png', transparencyBackground: true);

        when:
        final byte[] bytes = chemAxonService.generateImageBytes("CC", jchemBinFormat)
        then:
        assert bytes.length
    }
    /**
     * {@link ChemAxonService#generateImageBytes(String, JChemBinFormat)}
     */
    void "test generateImageBytes with empty smiles"() {
        given:
        JChemBinFormat jchemBinFormat = new JChemBinFormat(width: 10, height: 10, imageFormat: 'png', transparencyBackground: true);

        when:
        chemAxonService.generateImageBytes("", jchemBinFormat)
        then:
        Exception ee = thrown()
        assert ee
    }
    /**
     * {@link ChemAxonService#generateStructurePNG(String, Integer, Integer)}
     */
    void "test generateStructurePNG"() {
        when:
        final byte[] bytes = chemAxonService.generateStructurePNG("CC", 10, 10)
        then:
        assert bytes.length
    }

}
