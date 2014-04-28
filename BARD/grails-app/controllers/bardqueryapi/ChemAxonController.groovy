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
import grails.converters.JSON
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService

import javax.servlet.http.HttpServletResponse
import java.awt.image.BufferedImage
import javax.imageio.ImageIO


@Mixin(InetAddressUtil)
class ChemAxonController {
    ChemAxonService chemAxonService
    IQueryService queryService
    BardUtilitiesService bardUtilitiesService

    def generateStructureImageFromSmiles(String smiles, Integer width, Integer height) {
        try {
            Integer w = (width ?: 300) as Integer
            Integer h = (height ?: 300) as Integer

            if (!smiles) {
                final BufferedImage resizedImage = chemAxonService.getDefaultImage(w, h)
                response.contentType = 'image/png'
                ImageIO.write(resizedImage, "png", response.outputStream)
                return
            }


            byte[] bytes = chemAxonService.generateStructurePNG(smiles, w, h)


            response.contentType = 'image/png'
            response.outputStream.setBytes(bytes)
        } catch (Exception ee) {
            final String errorMessage = "Could not generate structure for smiles : ${smiles}"
            log.error(errorMessage + getUserIpAddress(bardUtilitiesService.username), ee)
        }
    }


    def generateStructureImageFromCID(Long cid, Integer width, Integer height) {
        try {
            CompoundAdapter compoundAdapter = cid ? this.queryService.showCompound(cid) : null
            if (compoundAdapter) {
                String smiles = compoundAdapter.smiles
                generateStructureImageFromSmiles(smiles, width, height)
            }
        } catch (Exception ee) {
            log.error("Could not generate structure for cid : ${cid}" + getUserIpAddress(bardUtilitiesService.username), ee)
        }
    }

    def marvinSketch() {}

    def convertSmilesToMolFormat(String smiles) {
        String molFormat = chemAxonService.convertSmilesToAnotherFormat(smiles, 'mol')
        if (molFormat) {
            render molFormat
            return
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Could not generate a Mol-format string for the specified SMILES: '${smiles}'")
            return
        }
    }
}
