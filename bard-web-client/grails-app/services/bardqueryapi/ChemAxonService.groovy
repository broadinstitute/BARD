package bardqueryapi

import chemaxon.formats.MolExporter
import chemaxon.formats.MolFormatException
import chemaxon.formats.MolImporter
import chemaxon.struc.Molecule

class ChemAxonService {
    /**
     *
     * @param smiles
     * @param width
     * @param height
     * @return a byte array representation of the Structure
     */
    public byte[] generateStructurePNG(final String smiles, final Integer width, final Integer height) {
        JChemBinFormat jchemBinFormat = new JChemBinFormat(width: width, height: height, imageFormat: 'png', transparencyBackground: true);
        return generateImageBytes(smiles, jchemBinFormat);
    }
    /**
     *
     * @param smiles
     * @param jchemBinFormat {@link JChemBinFormat}
     * @return a byte array representation of the Structure
     */
    public byte[] generateImageBytes(final String smiles, final JChemBinFormat jchemBinFormat) {
        Molecule mol;
        try {
            mol = MolImporter.importMol(smiles);
            // Calculate the coordinates if needed (for example
            mol.clean(2, "d");
            return MolExporter.exportToBinFormat(mol, jchemBinFormat.toString());
        }
        catch (MolFormatException e) {
            log.error(e)
            throw e;
        }
    }
}


