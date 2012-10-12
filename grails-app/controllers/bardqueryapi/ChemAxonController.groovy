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

    def generateStructureImage() {
        byte[] bytes = []
        Integer width = (params.width ?: 300) as Integer
        Integer height = (params.height ?: 300) as Integer

        if (params.smiles) {
            bytes = chemAxonService.generateStructurePNG(params.smiles, width, height)
        }
        else if (params.cid && params.cid?.isLong()) {
            Long cid = new Long(params.cid)
            CompoundAdapter compoundAdapter = this.queryService.showCompound(cid)
            if (compoundAdapter) {
                String smiles = compoundAdapter.structureSMILES
                bytes = chemAxonService.generateStructurePNG(smiles, width, height)
                //TODO: It seems that we should separate this into 2 different methods
            }
        }

        response.contentType = 'image/png'
        response.outputStream.setBytes(bytes)
    }

    def marvinSketch() {}
}
