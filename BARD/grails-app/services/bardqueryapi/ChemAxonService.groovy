package bardqueryapi

import chemaxon.formats.MolExporter
import chemaxon.formats.MolFormatException
import chemaxon.formats.MolImporter
import chemaxon.struc.Molecule
import org.apache.commons.lang3.StringUtils

import java.awt.image.BufferedImage
import java.awt.Graphics2D
import java.awt.AlphaComposite
import java.awt.RenderingHints
import javax.imageio.ImageIO

class ChemAxonService {
    final static String NO_SMILES_RESOURCE = "/resources/no_smiles_2.png"
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
            log.error(e,e)
            throw e;
        }
    }

    public BufferedImage getDefaultImage(final int width, final int height) {

        URL resource = this.getClass().getResource(NO_SMILES_RESOURCE)
        BufferedImage originalImage = ImageIO.read(resource);
        BufferedImage resizedImage = new BufferedImage(width, height, originalImage.type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * Converts a SMILES structure-string into a different string-format, specified by the fmt string.
     *
     * @param smiles
     * @param fmt Standard structure format such as: "mol", "sdf", "smiles", etc.
     * @return
     */
    public String convertSmilesToAnotherFormat(String smiles, String format) {
        Molecule mol;
        String smls = StringUtils.trimToNull(smiles)
        String frmt = StringUtils.trimToNull(format)
        if (smls && frmt) {
            try {
                mol = MolImporter.importMol(smls);
                return MolExporter.exportToFormat(mol, frmt);
            }
            catch (MolFormatException e) {
                log.error(e,e)
                throw e;
            }
        }
        return null
    }
}


