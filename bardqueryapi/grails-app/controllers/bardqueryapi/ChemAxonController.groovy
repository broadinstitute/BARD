package bardqueryapi

import chemaxon.formats.MolExporter
import chemaxon.struc.Molecule
import chemaxon.formats.MolImporter

class ChemAxonController {

    ChemAxonService chemAxonService

    def index() {
        session.putValue('smiles', params.smiles)
    }

    byte[] generateStructureImage() {
        byte[] bytes = []
        if (session.smiles) {
            bytes = chemAxonService.generateStructurePNG(session.smiles, 300, 300)
        }
        response.contentType = 'image/png'
        response.outputStream << bytes
    }
}
