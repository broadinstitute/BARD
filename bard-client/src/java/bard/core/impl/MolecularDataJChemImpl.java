package bard.core.impl;

import bard.core.Format;
import bard.core.interfaces.MolecularData;
import chemaxon.calculations.clean.Cleaner;
import chemaxon.calculations.hydrogenize.Hydrogenize;
import chemaxon.formats.MolExporter;
import chemaxon.formats.MolFormatException;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import chemaxon.util.MolHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.io.IOException;


/**
 * Default implementation of MolecularData based on JChem
 */
public class MolecularDataJChemImpl implements MolecularData {
    final ObjectMapper mapper = new ObjectMapper();

    private static final long serialVersionUID = 0x2ce64ddd6b96665al;
    private static final Logger log = Logger.getLogger(MolecularDataJChemImpl.class);
    protected String formula;
    protected Double mwt;
    protected Double exactMass;
    protected Integer hbondDonor;
    protected Integer hbondAcceptor;
    protected Integer rotatable;
    protected Integer definedStereo;
    protected Integer stereocenters;
    protected Double tpsa;
    protected Double logP;
    protected Boolean ruleOf5;
    protected int[] fingerprint;
    protected byte[] image;

    /*
     * a persistent string representation of a molecule stored as
     * a mol/sd format at the moment
     */
    protected String structure;

    /*
     * a transient internal representation of the molecule
     */
    transient protected Molecule molecule;

    public MolecularDataJChemImpl() {
    }

    /*
     * MolecularData interface
     */
    public String formula() {
        return this.formula;
    }

    public Double mwt() { // molecular weight
        return this.mwt;
    }

    public Double exactMass() {
        return this.exactMass;
    }

    public Integer hbondDonor() { // count of H-bond donors
        return this.hbondDonor;
    }

    public Integer hbondAcceptor() { // count of H-bond acceptors
        return this.hbondAcceptor;
    }

    public Integer rotatable() { // count of rotatable bonds
        return this.rotatable;
    }

    public Integer definedStereo() { // count of defined stereo centers
        return this.definedStereo;
    }

    // count of stereo centers in the molecule
    public Integer stereocenters() {
        return this.stereocenters;
    }

    public Double TPSA() { // topological polar surface area
        return this.tpsa;
    }

    public Double logP() { // logP
        return this.logP;
    }

    public Boolean ruleOf5() {
        return this.ruleOf5;
    }
    //TODO: We need to add a ChemAxon library to do this. But do we even need this? I think not.
    public int[] fingerprint() { // bit fingerprint for indexing/searching
        if (fingerprint == null && this.molecule != null) { // lazy evaluation
            MolHandler mh = new MolHandler(this.molecule.cloneMolecule());
            mh.aromatize();
            this.fingerprint = mh.generateFingerprintInInts(16, 2, 6);
        }
        return this.fingerprint;
    }

    /*
     * output formats
     */
    public Object toFormat(final Format format) {
        try {
            switch (format) {
                case SMI:
                case SMILES:
                    return MolExporter.exportToFormat(this.molecule, "smiles:q");

                case SMARTS:
                    return MolExporter.exportToFormat(this.molecule, "smarts:q");
                case MOL:
                case SDF:
                    return MolExporter.exportToFormat(this.molecule, "mol");
                default:
                    return this.molecule;
            }
        } catch (IOException ee) {
            log.error(ee);
            throw new RuntimeException(ee.getMessage());
        }

    }

    public byte[] toImage(final int size, final int background) {
        return image;
    }

    public void setMolecule(final Object input) {
        try {
            //logger.info("Input: "+input+" "+input.getClass());

            if (input instanceof Molecule) {
                parsePropertiesMol((Molecule) input);
            } else if (input instanceof byte[]) {
                parsePropertiesMol((byte[]) input);
            } else if (input instanceof JsonNode) {
                parsePropertiesJson((JsonNode) input);
            } else { // treat as string
                // last resort... try parsing it as the molecule
                parsePropertiesMol(input.toString());
            }
        } catch (MolFormatException ex) {
            log.error(ex);
            throw new IllegalArgumentException
                    ("Not a molecular format " + ex.getMessage());
        }
    }

    protected void parsePropertiesMol(final Molecule molecule) {
        try {
            Hydrogenize.removeHAtoms(molecule);
            //molecule.hydrogenize(false);
            molecule.dearomatize();
            if (molecule.getDim() < 2) {
                Cleaner.clean(molecule, 2, null);
            }
            structure = MolExporter.exportToFormat(molecule, "sdf");
            //structure = molecule.toFormat("sdf");

            formula = molecule.getFormula();
            mwt = molecule.getMass();
            exactMass = molecule.getExactMass();
            calcStereos(molecule);
            fingerprint = null;
            this.molecule = molecule;
        } catch (IOException ee) {
            log.error(ee);
        }
    }
    protected String getStructure(){
        return this.structure;
    }
    protected void parsePropertiesMol(final String molstr)
            throws MolFormatException {
        parsePropertiesMol(MolImporter.importMol(molstr));
    }

    protected void parsePropertiesMol(final byte[] molecule)
            throws MolFormatException {
        parsePropertiesMol(MolImporter.importMol(molecule));
    }

    protected void calcStereos(final Molecule molecule) {
        int total = 0, defined = 0;
        for (int i = 0; i < molecule.getAtomCount(); ++i) {
            int flag = molecule.getChirality(i);
            if (flag == Molecule.CHIRALITY_R
                    || flag == Molecule.CHIRALITY_S) {
                ++defined;
            }
            if (flag != 0) {
                ++total;
            }
        }
        stereocenters = total;
        definedStereo = defined;
    }

    protected void parsePropertiesJson(final String content)
            throws JsonProcessingException, IOException, MolFormatException {
        parsePropertiesJson(mapper.readTree(content));
    }

    public boolean isNotNull(JsonNode jsonNode) {
        return jsonNode != null && !jsonNode.isNull();
    }

    protected void parsePropertiesJson(final JsonNode node)
            throws MolFormatException {
        final JsonNode smilesNode = node.get("smiles");
        if (isNotNull(smilesNode)) {
            String smiles = smilesNode.asText();
            parsePropertiesMol(smiles);
        }
        // now override these values...

        final JsonNode molecularWeightNode = node.get("mwt");
        if (isNotNull(molecularWeightNode)) {
            this.mwt = molecularWeightNode.asDouble();
        }

        final JsonNode exactMassNode = node.get("exactMass");
        if (isNotNull(exactMassNode)) {
            this.exactMass = exactMassNode.asDouble();
        }

        final JsonNode hbondDonorNode = node.get("hbondDonor");
        if (isNotNull(hbondDonorNode)) {
            this.hbondDonor = hbondDonorNode.asInt();
        }

        final JsonNode hbondAcceptorNode = node.get("hbondAcceptor");
        if (isNotNull(hbondAcceptorNode)) {
            this.hbondAcceptor = hbondAcceptorNode.asInt();
        }

        final JsonNode tpsaNode = node.get("tpsa");
        if (isNotNull(tpsaNode)) {
            this.tpsa = tpsaNode.asDouble();
        }

        final JsonNode xlogpNode = node.get("xlogp");
        if (isNotNull(xlogpNode)) {
            this.logP = xlogpNode.asDouble();
        }

        final JsonNode rotatableNode = node.get("rotatable");
        if (isNotNull(rotatableNode)) {
            this.rotatable = rotatableNode.asInt();
        }
    }
}
