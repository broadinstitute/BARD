package bardqueryapi

import chemaxon.formats.MolExporter
import chemaxon.struc.Molecule
import chemaxon.formats.MolImporter

class ChemAxonController {

    ChemAxonService chemAxonService
//
//    def index() {
//        session.putValue('smiles', params.smiles)
//    }

    def generateStructureImage() {
        byte[] bytes = []
        Integer width = (params.width ?: 300) as Integer
        Integer height = (params.height ?: 300) as Integer

        if (params.smiles) {
            bytes = chemAxonService.generateStructurePNG(params.smiles, width, height)
        }

        response.contentType = 'image/png'
        response.outputStream.setBytes(bytes)
    }

    def marvinSketch() {}
}
