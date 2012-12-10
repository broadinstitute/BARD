package bardqueryapi

import bard.core.adapter.CompoundAdapter
import grails.plugins.springsecurity.Secured

@Secured(['isFullyAuthenticated()'])
class ChemAxonController {

    ChemAxonService chemAxonService
    IQueryService queryService

    def index() {
        session.putValue('smiles', params.smiles)
    }

    def generateStructureImageFromSmiles(String smiles, Integer width, Integer height) {
        try {
            byte[] bytes = []
            Integer w = (width ?: 300) as Integer
            Integer h = (height ?: 300) as Integer

            if (smiles) {
                bytes = chemAxonService.generateStructurePNG(smiles, w, h)
            }

            response.contentType = 'image/png'
            response.outputStream.setBytes(bytes)
        } catch (Exception ee) {
            log.error("Could not generate structure for smiles : ${smiles}", ee)
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
            log.error("Could not generate structure for cid : ${cid}", ee)
        }
    }

    def marvinSketch() {}
}
