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

import org.apache.commons.lang3.StringUtils

/**
 *
 * Encapsulate the creation of the formating string that the jchem takes to
 * generate and image see <a href=
 * "http://www.chemaxon.com/marvin/help/formats/images-doc.html#options">the
 * docs</a>
 *
 * example "png:w300,h300,chiral_all,H_hetero"
 *
 * @author <a href="http://www.broad.mit.edu/chembio/">Broad Institute,
 *         Chemical Biology Software Development Group</a>.
 * @version $Revision$.
 */
class JChemBinFormat {
    /**
     *
     */
    private static final String FORMAT_SEPARATOR = ":";

    private final static String WIDTH_PREFIX = "w";
    private final static String HEIGTH_PREFIX = "h";
    /**
     * Show R/S for any molecule.
     */
    private final static String DEFAULT_CHIRAL_SELECTION = "chiral_all";
    /**
     * Shoe E/Z for double bonds
     */
    private final static String SHOW_EZ = "ez";
    /**
     * Implicit Hydrogen labels on heteroatoms only.
     */
    private static final String DEFAULT_IMPLICIT_HYDROGEN = "H_hetero";
    private static final String DEFAULT_IMAGE_FORMAT = "png";
    private static final String DEFAULT_TRANSPARENCY_BACKGROUND = "transbg";
    private static final Integer DEFAULT_WIDTH = 100;
    private static final Integer DEFAULT_HEIGHT = 100;

    Integer width = DEFAULT_WIDTH
    Integer height = DEFAULT_HEIGHT
    String imageFormat = DEFAULT_IMAGE_FORMAT
    Boolean transparencyBackground = true

    /**
     * format needed for {@link chemaxon.struc.Molecule#toBinFormat(String)}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String exportOptionsAsString = this.exportOptions
        return "${this.imageFormat}${FORMAT_SEPARATOR}${exportOptionsAsString}"
    }

    /**
     * @return String of options to use for JChem
     */
    public String getExportOptions() {
        List<String> options = [];
        options.add("${WIDTH_PREFIX}${this.width}");
        options.add("${HEIGTH_PREFIX}${this.height}");
        options.add("${DEFAULT_IMPLICIT_HYDROGEN}");
        options.add("${DEFAULT_CHIRAL_SELECTION}");
        options.add("${SHOW_EZ}");
        if (this.transparencyBackground) {
            options.add(DEFAULT_TRANSPARENCY_BACKGROUND)
        }
        return StringUtils.join(options,",");
    }
}
