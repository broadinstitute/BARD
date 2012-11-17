package bard.core.impl

import bard.core.Format
import chemaxon.formats.MolImporter
import chemaxon.struc.Molecule
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class MolecularDataJChemImplUnitSpec extends Specification {

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test parsePropertiesJson(final String content)"() {
        given:
        MolecularDataJChemImpl molecularDataJChem = new MolecularDataJChemImpl()
        when:
        molecularDataJChem.parsePropertiesJson("{\"a\":\"b\"}")
        then:
        assert !molecularDataJChem.toImage(1, 1)
        assert !molecularDataJChem.ruleOf5()
        assert !molecularDataJChem.mwt()
        assert !molecularDataJChem.exactMass()
        assert !molecularDataJChem.hbondDonor()
        assert !molecularDataJChem.hbondAcceptor()
        assert !molecularDataJChem.TPSA()
        assert !molecularDataJChem.logP()
        assert !molecularDataJChem.rotatable()


    }

    void "test parsePropertiesJson(final JsonNode node) "() {
        given:
        ObjectMapper mapper = new ObjectMapper()
        JsonNode jsonNode = mapper.createObjectNode()
        MolecularDataJChemImpl molecularDataJChem = new MolecularDataJChemImpl()
        when:
        molecularDataJChem.parsePropertiesJson(jsonNode)
        then:
        assert !molecularDataJChem.ruleOf5()
        assert !molecularDataJChem.mwt()
        assert !molecularDataJChem.exactMass()
        assert !molecularDataJChem.hbondDonor()
        assert !molecularDataJChem.hbondAcceptor()
        assert !molecularDataJChem.TPSA()
        assert !molecularDataJChem.logP()
        assert !molecularDataJChem.rotatable()

    }
    //TODO: Missing Chemaxon library
//    void "test fingerprint"() {
//        given:
//        MolecularDataJChemImpl molecularDataJChem = new MolecularDataJChemImpl()
//        molecularDataJChem.setMolecule("CCC")
//        when:
//        final def fingerprint = molecularDataJChem.fingerprint()
//        then:
//        assert fingerprint
//    }

    void "test toFormat #label"() {
        given:
        MolecularDataJChemImpl molecularDataJChem = new MolecularDataJChemImpl()
        molecularDataJChem.setMolecule("CCC")
        when:
        final Object resultFormat = molecularDataJChem.toFormat(format)
        then:

        assert resultFormat
        where:
        label           | format
        "Mol Format"    | Format.MOL
        "SDF Format"    | Format.SDF
        "SMILES Format" | Format.SMILES
        "SMI Format"    | Format.SMI
        "SMARTS Format" | Format.SMARTS
        "Native Format" | Format.NATIVE
    }


    void "test set Molecule #label"() {
        given:
        MolecularDataJChemImpl molecularDataJChem = new MolecularDataJChemImpl()
        Molecule molecule = MolImporter.importMol("CCC")
        molecule.setDim(0)

        when:
        molecularDataJChem.setMolecule(molecule)
        then:
        assert molecularDataJChem.molecule
        assert molecularDataJChem.getStructure()
        assert molecularDataJChem.formula()
        assert molecularDataJChem.mwt()
        assert molecularDataJChem.exactMass()
        assert molecularDataJChem.stereocenters() == 0
        assert molecularDataJChem.definedStereo() == 0

    }

    void "test set Molecule with bytes #label"() {
        given:
        final MolecularDataJChemImpl molecularDataJChem = new MolecularDataJChemImpl()
        final String smiles = "CCC"
        when:
        molecularDataJChem.setMolecule(smiles.getBytes())
        then:
        assert molecularDataJChem.molecule
        assert molecularDataJChem.getStructure()
        assert molecularDataJChem.formula()
        assert molecularDataJChem.mwt()
        assert molecularDataJChem.exactMass()
        assert molecularDataJChem.stereocenters() == 0
        assert molecularDataJChem.definedStereo() == 0
    }
}

