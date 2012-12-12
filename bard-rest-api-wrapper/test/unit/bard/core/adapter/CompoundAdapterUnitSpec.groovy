package bard.core.adapter

import bard.core.rest.spring.compounds.Compound
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Shared
import com.fasterxml.jackson.databind.ObjectMapper
import bard.core.rest.spring.util.NameDescription
import bard.core.rest.spring.compounds.CompoundAnnotations

@Unroll
class CompoundAdapterUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    static final String COMPOUND_EXPANDED = '''
   {
       "cid": 2722,
       "probeId":null,
       "url": "http://pubchem.org",
       "smiles": "OC1=C(Cl)C=C(Cl)C2=C1N=CC=C2",
       "name": "Chloroxine",
       "iupacName": "5,7-dichloroquinolin-8-ol",
       "mwt": 214.048,
       "tpsa": 33.1,
       "exactMass": 212.975,
       "xlogp": 3.5,
       "complexity": 191,
       "rotatable": 0,
       "hbondAcceptor": 2,
       "hbondDonor": 1,
       "compoundClass": "Drug",
       "numAssay": 500,
       "numActiveAssay": 60,
       "highlight": null,
       "resourcePath": "/compounds/2722"
   }
'''
    static final String COMPOUND_ANNOTATIONS = '''
    {
       "anno_key":[
          "CompoundSpectra",
          "DOCUMENTS",
          "DOCUMENTS",
          "CompoundUNII",
          "CompoundCAS",
          "CompoundCAS",
          "CompoundCAS",
          "CompoundCAS",
          "CompoundIndication",
          "CompoundIndication",
          "CompoundIndication",
          "CompoundDrugLabelRx",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "COLLECTION",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "Synonyms",
          "CompoundMOA",
          "CompoundMOA"
       ],
       "anno_val":[
          "http://tripod.nih.gov/npc/spectra/NCGC00095264.png",
          "1820340",
          "7513790",
          "2I8BD50I8B",
          "773-76-2",
          "8067-69-4",
          "8021-96-3",
          "81117-07-9",
          "Dermatologic",
          "Antiseptic",
          "Used in the treatment of dandruff and mild to moderately severe seborrheic dermatitis of the scalp.",
          "0072-6850;CAPITROL;shampoo;chloroxine;20 mg in 1 g;2I8BD50I8B",
          "NPC informatics|NPC-7244033|ChemDiv, Inc:3406-0528|Microsource:MS-1503202",
          "HTS amenable drugs",
          "Approved drugs",
          "FDA orange book",
          "FDA drugs@FDA",
          "FDA NDC",
          "KEGG",
          "Human approved drugs",
          "FDA approved",
          "FDA maximum daily dose",
          "FDA human approved",
          "DrugBank v3.0|DB01243",
          "FDA DailyMed",
          "NPC screening|NCGC00095264",
          "QC spectra",
          "Quixalin",
          "Capitrol",
          "Dichloroquine",
          "5,7-Dichloro-8-quinolinol",
          "Dichlorohydroxyquinoline",
          "Chloroxine",
          "Chlorhydroxyquinoline",
          "Chloroxine hydrofluoride",
          "Chlorquinol",
          "Halquinols",
          "Halquinol",
          "Chlofucid",
          "Clofuzid",
          "Endiaron",
          "Quesyl",
          "Quinolor",
          "CHQ",
          "Chloroxyquinoline",
          "Dichloroquinolinol",
          "Dichloroxin",
          "Dikhloroskin",
          "5,7-dichloroquinolin-8-ol",
          "Antiseborrheic Agents",
          "Although the mechanism of action is not understood, chloroxine may slow down mitotic activity in the epidermis, thereby reducing excessive scaling associated with dandruff or seborrheic dermatitis of the scalp. Chloroxine induces SOS-DNA repair in E. coli, so chloroxine may be genotoxic to bacteria."
       ]
    }
    '''



    void "test compound Adapter"() {
        given:
        final Compound compound = objectMapper.readValue(COMPOUND_EXPANDED, Compound.class)
        compound.compoundAnnotations = new CompoundAnnotations()

        when:
        final CompoundAdapter compoundAdapter = new CompoundAdapter(compound, new Double("2"), new NameDescription(name: "name", description: "description"))
        then:
        assertCompounds(compoundAdapter)
        assert !compoundAdapter.getSynonyms()
        assert !compoundAdapter.getRegistryNumbers()
        assert !compoundAdapter.getUniqueIngredientIdentifier()
        assert !compoundAdapter.getMechanismOfAction()
        assert !compoundAdapter.getTherapeuticIndication()
        assert !compoundAdapter.getPrescriptionDrugLabel()
        assert !compoundAdapter.getOtherAnnotationValue("key")

    }

    void "test compound Adapter with no highlight field"() {
        when:
        final CompoundAdapter compoundAdapter = new CompoundAdapter(new Compound())
        then:
        assert !compoundAdapter.highlight
        assert !compoundAdapter.getOtherAnnotationValue("SomeValues")

    }

    void "test Compound Adapter with Annotations"() {
        given:
        final Compound compound = objectMapper.readValue(COMPOUND_EXPANDED, Compound.class)
        final CompoundAnnotations compoundAnnotations = objectMapper.readValue(COMPOUND_ANNOTATIONS, CompoundAnnotations.class)
        compound.compoundAnnotations = compoundAnnotations
        when:
        final CompoundAdapter compoundAdapter = new CompoundAdapter(compound, new Double("2"), new NameDescription(name: "name", description: "description"))
        then:
        assertCompounds(compoundAdapter)
        assertAnnotations(compoundAdapter)


    }

    void "test get Synonyms"() {
        when:
        final CompoundAdapter compoundAdapter =
            new CompoundAdapter(new Compound(), new Double("2"), new NameDescription(name: "name", description: "description"))
        then:
        compoundAdapter.getSynonyms().isEmpty()
        !compoundAdapter.getRegistryNumbers()
        !compoundAdapter.getUniqueIngredientIdentifier()
        !compoundAdapter.getMechanismOfAction()
        !compoundAdapter.getTherapeuticIndication()
        !compoundAdapter.getPrescriptionDrugLabel()
    }

    void assertCompounds(final CompoundAdapter compoundAdapter) {
        assert compoundAdapter.isDrug()
        assert !compoundAdapter.getProbeId()
        assert !compoundAdapter.isProbe()
        assert compoundAdapter.getId() == 2722
        assert compoundAdapter.getPubChemCID() == 2722
        assert compoundAdapter.getStructureSMILES() == "OC1=C(Cl)C=C(Cl)C2=C1N=CC=C2"
        assert !compoundAdapter.getStructureMOL()
        assert !compoundAdapter.formula()
        assert compoundAdapter.mwt() == 214.048
        assert compoundAdapter.exactMass() == 212.975
        assert compoundAdapter.hbondDonor() == 1
        assert compoundAdapter.hbondAcceptor() == 2
        assert compoundAdapter.rotatable() == 0
        assert !compoundAdapter.definedStereo()
        assert !compoundAdapter.stereocenters()

        assert compoundAdapter.TPSA() == 33.1
        assert compoundAdapter.logP() == 3.5
        assert compoundAdapter.name == "Chloroxine"
        assert compoundAdapter.getIupacName() == "5,7-dichloroquinolin-8-ol"
        assert compoundAdapter.url == "http://pubchem.org"
        assert compoundAdapter.getComplexity() == 191
        assert compoundAdapter.getCompoundClass() == "Drug"
        assert compoundAdapter.getNumberOfAssays() == 500
        assert compoundAdapter.getNumberOfActiveAssays() == 60
        assert compoundAdapter.resourcePath() == "/compounds/2722"
        assert !compoundAdapter.getStructureNative()
        assert !compoundAdapter.formula()
        assert compoundAdapter.matchingField.name == "name"
        assert compoundAdapter.matchingField.description == "description"
        assert compoundAdapter.score == 2
        assert compoundAdapter.highlight == "Score: 2.0 Matched Field: name"
    }

    void assertAnnotations(CompoundAdapter compoundAdapter) {


        assert compoundAdapter.getOtherAnnotationValue("COLLECTION").size() == 15
        assert compoundAdapter.getOtherAnnotationValue("DOCUMENTS").size() == 2
        assert compoundAdapter.getSynonyms().size() == 22
        assert compoundAdapter.getUniqueIngredientIdentifier() == "2I8BD50I8B"

        assert compoundAdapter.getRegistryNumbers().size() == 4

        assert compoundAdapter.getTherapeuticIndication().size() == 3

        assert compoundAdapter.getPrescriptionDrugLabel().size() == 1

        assert compoundAdapter.getMechanismOfAction().size() == 2

        assert compoundAdapter.getOtherAnnotationValue("CompoundSpectra").size() == 1

    }

}

