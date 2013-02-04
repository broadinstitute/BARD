package bardqueryapi

import bard.core.adapter.CompoundAdapter
import grails.plugins.springsecurity.Secured
import grails.plugins.springsecurity.SpringSecurityService

import java.awt.image.BufferedImage
import javax.imageio.ImageIO


@Secured(['isFullyAuthenticated()'])
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
                String smiles = compoundAdapter.structureSMILES
                generateStructureImageFromSmiles(smiles, width, height)
            }
        } catch (Exception ee) {
            log.error("Could not generate structure for cid : ${cid}" + getUserIpAddress(bardUtilitiesService.username), ee)
        }
    }

    def marvinSketch() {}


}
