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
        if (params.smiles) {
            bytes = chemAxonService.generateStructurePNG(params.smiles, 300, 300)
        }
        response.contentType = 'image/png'
        response.outputStream << bytes
    }
}
