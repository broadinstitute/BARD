package mockServices

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.interfaces.AssayCategory
import bard.core.interfaces.ExperimentValues
import bardqueryapi.IQueryService
import bardqueryapi.QueryHelperService
import bardqueryapi.SearchFilter
import bard.core.*
import bard.core.interfaces.ExperimentCategory
import bard.core.interfaces.ExperimentRole
import bard.core.interfaces.ExperimentType

class MockQueryService implements IQueryService {
    QueryHelperService queryHelperService

    static final Map<Long, MockCompoundAdapter> mockCompoundAdapterMap = [:]

    static final Map<Long, MockAssayAdapter> mockAssayAdapterMap = [:]

    static final Map<Long, MockProjectAdapter> mockProjectAdapterMap = [:]

    static final Map<Long, MockExperiment> mockExperimentMap = [:]

    static {
        constructMockCompoundAdapter()
        constructMockAssayAdapter()
        constructMockProjectAdapter()
        constructMockExperiment()
    }

    /**
     *
     * @param compound
     * @param activeOnly - true if we want only the active compounds
     * @return int the number of tested assays
     */
    public int getNumberTestedAssays(Long cid,
                                     boolean activeOnly) {
        return 2;

    }

    Map findPromiscuityScoreForCID(final Long cid) {
        return [scores: [20, 30], status: 200, message: "Success"]
    }

    //========================================================== Free Text Searches ================================
    /**
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters
     * @return
     */
    Map findCompoundsByTextSearch(final String searchString, final Integer top = 10, final Integer skip = 0, final List<SearchFilter> searchFilters = []) {
        final List<CompoundAdapter> foundCompoundAdapters = []

        Set<Long> keySet = MockQueryService.mockCompoundAdapterMap.keySet()
        for (Long key : keySet) {
            foundCompoundAdapters.add(MockQueryService.mockCompoundAdapterMap.get(key))
        }
        Collection<Value> facets = []
        return [compoundAdapters: foundCompoundAdapters, facets: facets, nHits: 3]
    }

    /**
     * We can use a trick to get more than 10 records
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters
     * @return Map
     */
    Map findAssaysByTextSearch(final String searchString, final Integer top = 10, final Integer skip = 0, final List<SearchFilter> searchFilters = []) {
        final List<AssayAdapter> foundAssayAdapters = []
        Collection<Value> facets = []

        Set<Long> keySet = MockQueryService.mockAssayAdapterMap.keySet()
        for (Long key : keySet) {
            foundAssayAdapters.add(MockQueryService.mockAssayAdapterMap.get(key))
        }
        int nhits = foundAssayAdapters.size()
        return [assayAdapters: foundAssayAdapters, facets: facets, nHits: nhits]
    }

    /**
     * We are not quite ready to use this method yet
     * @param searchString
     * @param top
     * @param skip
     * @param searchFilters
     * @return Map
     */
    Map findProjectsByTextSearch(final String searchString, final Integer top = 10, final Integer skip = 0, final List<SearchFilter> searchFilters = []) {
        List<ProjectAdapter> foundProjectAdapters = []
        Collection<Value> facets = []

        Set<Long> keySet = MockQueryService.mockProjectAdapterMap.keySet()
        for (Long key : keySet) {
            foundProjectAdapters.add(MockQueryService.mockProjectAdapterMap.get(key))
        }
        int nhits = foundProjectAdapters.size()
        return [projectAdapters: foundProjectAdapters, facets: facets, nHits: nhits]
    }

    //====================================== Structure Searches ========================================
    /**
     * @param smiles
     * @param structureSearchParamsType
     * @param top
     * @param skip
     * @return of compounds
     */
    Map structureSearch(final String smiles, final StructureSearchParams.Type structureSearchParamsType, final List<SearchFilter> searchFilters = [], final Integer top = 50, final Integer skip = 0) {
        return findCompoundsByTextSearch("", 10, 0, searchFilters)
    }

    //===================== Find Resources given a list of IDs ================================
    /**
     * Given a list of Compound Ids return all the compounds that were found
     * @param compoundIds
     * @return list
     */
    Map findCompoundsByCIDs(final List<Long> compoundIds, List<SearchFilter> filters = []) {
        return findCompoundsByTextSearch("", 10, 0, filters)
    }

    /**
     * Given a list of Assay Ids return all the assays that were found
     * @param assayIds
     * @return list
     */
    Map findAssaysByADIDs(final List<Long> assayIds, List<SearchFilter> filters = []) {
        return findAssaysByTextSearch("", 10, 0, filters)
    }

    /**
     *
     * Given a list of Project Ids return all the projects that were found
     * @param projectIds
     * @return list
     */
    Map findProjectsByPIDs(final List<Long> projectIds, List<SearchFilter> filters = []) {
        findProjectsByTextSearch("", 10, 0, filters)
    }
    //=============== Show Resources Given a Single ID ================

    /**
     * Given a CID, get detailed compound information from REST API
     * @param compoundId
     * @return CompoundAdapter
     */
    CompoundAdapter showCompound(final Long compoundId) {

        return MockQueryService.mockCompoundAdapterMap.get(compoundId)
    }

    /**
     * Given an assayId, get detailed Assay information from the REST API
     * @param assayId
     * @return AssayAdapter
     */
    Map showAssay(final Long assayId) {

        return MockQueryService.mockAssayAdapterMap.get(assayId)
    }
    /**
     * Given a projectId, get detailed Project information from the JDO
     * @param projectId
     * @return ProjectAdapter
     */
    Map showProject(final Long projectId) {
        return [projectAdapter: MockQueryService.mockProjectAdapterMap.get(projectId), experiments: mockExperimentMap.values(), assays: [MockQueryService.mockAssayAdapterMap.get(588636 as Long).assay,
                MockQueryService.mockAssayAdapterMap.get(449731 as Long).assay,
                MockQueryService.mockAssayAdapterMap.get(588623 as Long).assay]]
    }

    //==============================================Auto Complete ======
    /**
     *
     * @param term
     * @return the list of maps to use for auto suggest
     */
    public List<Map<String, String>> autoComplete(final String term) {

        //the number of items to retrieve per category
        final Map<String, List<String>> autoSuggestResponseFromJDO = ["gobp_term":
                [
                        "DNA repair",
                        "DNA fragmentation involved in apoptotic nuclear change",
                        "DNA damage response, signal transduction by p53 class mediator resulting in cell cycle arrest"
                ],
                "target_name":
                        [
                                "DNA dC->dU-editing enzyme APOBEC-3G",
                                "DNA (cytosine-5)-methyltransferase 1",
                                "DNA polymerase beta"
                        ],
                "gomf_term":
                        [
                                "DNA binding",
                                "DNA strand annealing activity",
                                "DNA-dependent protein kinase activity"
                        ]
        ]
        final List<Map<String, String>> autoSuggestTerms = this.queryHelperService.autoComplete(term, autoSuggestResponseFromJDO)
        return autoSuggestTerms
    }
    /**
     * Extract filters from the search string if any
     * @return list of filters from search String
     */
    public void findFiltersInSearchBox(final List<SearchFilter> searchFilters, final String searchString) {
        queryHelperService.findFiltersInSearchBox(searchFilters, searchString)
    }

    private static void constructMockAssayAdapter() {

        MockAssayAdapter mockAssayAdapter = new MockAssayAdapter()
        mockAssayAdapter.searchHighlight = '''
        The Y-family of DNA polymerases, such as Pol eta, are specifically involved in <b>DNA repair</b>.
        Pol eta copies undamaged DNA with a lower fidelity than other DNA-directed polymerases.
        However, it accurately replicates UV-damaged DNA; when thymine dimers are present,
        this polymerase inserts
        '''
        mockAssayAdapter.name = "qHTS for Inhibitors of Polymerase Eta: Summary"
        Assay assay = new Assay()
        Long assayId = new Long(588636)
        assay.setId(assayId)
        mockAssayAdapter.assay = assay
        mockAssayAdapter.assay.category = AssayCategory.MLPCN
        mockAssayAdapter.assay.protocol = "Please see linked AIDs for a detailed description of each assay."
        mockAssayAdapter.assay.comments = "This project is on-going and will be updated at a later point with our findings."
        mockAssayAdapter.assay.description = '''
The Y-family of DNA polymerases, such as Pol eta, are specifically involved in DNA repair.
Pol eta copies undamaged DNA with a lower fidelity than other DNA-directed polymerases.
However, it accurately replicates UV-damaged DNA; when thymine dimers are present, this polymerase inserts the
complementary nucleotides in the newly synthesized DNA, thereby bypassing the lesion and suppressing the mutagenic
effect of UV-induced DNA damage. Pol eta has the ability to bypass cisplatinated DNA adducts in vitro, and it
has been suggested that pol eta-dependent bypass of the cisplatin lesion in vivo leads to increased tumor resistance.
Thus, while pol eta's (and most likely iota's) normal function is to protect humans against the deleterious consequences
of DNA damage, under certain conditions they can have deleterious effects on human health.
As a consequence, we propose to utilize a high throughput replication assay to
identify small molecule inhibitors of pol eta.

In a collaboration between the National Institute of Child Health & Human Development (NICHD) and NIH Chemical
Genomics Center, a high-throughput, fluorescent screen was developed to screen the NIH Molecular Libraries Small
Molecule Repository (MLSMR). This screen is used to identify inhibitors of pol eta.

NIH Chemical Genomics Center [NCGC]
NIH Molecular Libraries Probe Centers Network [MLPCN]

MLPCN Grant: MH090825
Assay Submitter (PI): Roger Woodgate, NICHD)
'''
        mockAssayAdapterMap.put(assayId, mockAssayAdapter)

        mockAssayAdapter = new MockAssayAdapter()
        mockAssayAdapter.searchHighlight = '''
for the bacteria to persist. The H. pylori AddAB helicase-exonuclease is required for <b>DNA repair</b>
and efficient stomach colonization (3), and inhibitors of this enzyme may be useful antibacterial
drugs for treating these infections. The AddAB class of enzymes is closely related to the RecBCD
class of helicase
'''
        mockAssayAdapter.name = "Summary of the probe development effort to identify inhibitors of AddAB recombination protein complex"
        assay = new Assay()
        assayId = new Long(449731)
        assay.setId(assayId)
        mockAssayAdapter.assay = assay
        mockAssayAdapterMap.put(assayId, mockAssayAdapter)


        mockAssayAdapter = new MockAssayAdapter()
        mockAssayAdapter.searchHighlight = '''
DNA polymerases, such as Pol iota, are specifically involved in <b>DNA repair</b>.
Pol iota specifically plays an important role in translesion synthesis, where
the normal high-fidelity DNA polymerases cannot proceed and DNA synthesis stalls.
It has been reported that human pol iota may be upregulated'''
        mockAssayAdapter.name = "qHTS for Inhibitors of Polymerase Iota: Summary"

        assay = new Assay()
        assayId = new Long(588623)

        assay.setId(assayId)
        mockAssayAdapter.assay = assay
        mockAssayAdapterMap.put(assayId, mockAssayAdapter)

    }

    private static void constructMockProjectAdapter() {
        MockProjectAdapter mockProjectAdapter = new MockProjectAdapter()
        Long projectId = new Long(2324)
        Project project = new Project()
        project.setId(projectId)
        project.description = '''
Assay Provider: David M. Wilson, III, National Institute on Aging, NIH Screening Center PI: Austin, C.P. Screening Center: NIH Chemical Genomics Center [NCGC]
The apurinic/apyrimidinic endonuclease APE1 is the primary mammalian enzyme responsible for the removal of abasic (or AP) sites in DNA and functions centrally
in the base excision DNA repair (BER) pathway. Recent studies suggested a link between an overexpression of APE1 in many cancers and resistance of these tumor
cells to radio- and chemotherapy. Thus, targeting APE1 could improve the efficacy of current treatment paradigms by promoting selective sensitization or
protection of diseased and normal cells, respectively. This assay will summarize the probe development efforts that are currently ongoing.
   '''
        mockProjectAdapter.project = project
        project.name = "Probe Development Summary of Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)"
        mockProjectAdapter.name = "Probe Development Summary of Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)"
        mockProjectAdapter.searchHighlight = '''and functions centrally in the base excision DNA repair (BER) pathway.
        Recent studies suggested a link between an overexpression
        of APE1 in many cancers and resistance of these tumor cells to radio- and chemotherapy.
        Thus, targeting APE1 could improve the efficacy of current treatment paradigms by promoting
        '''
        mockProjectAdapter.annotations << ['First annotation': '''This is the 1st annotation for this project''']
        mockProjectAdapter.annotations << ['Second annotation': '''This is the 2nd annotation for this project''']
        mockProjectAdapter.annotations << ['Third annotation': '''This is the 3rd annotation for this project''']
        mockProjectAdapterMap.put(projectId, mockProjectAdapter)



        mockProjectAdapter = new MockProjectAdapter()
        project = new Project()
        projectId = new Long(449731)
        project.name = "Summary of the probe development effort to identify inhibitors of AddAB recombination protein complex"
        project.setId(projectId)

        project.description = '''
Assay Provider: David M. Wilson, III, National Institute on Aging, NIH Screening Center PI: Austin, C.P. Screening Center: NIH Chemical Genomics Center [NCGC]
The apurinic/apyrimidinic endonuclease APE1 is the primary mammalian enzyme responsible for the removal of abasic (or AP) sites in DNA and functions centrally
in the base excision DNA repair (BER) pathway. Recent studies suggested a link between an overexpression of APE1 in many cancers and resistance of these tumor
cells to radio- and chemotherapy. Thus, targeting APE1 could improve the efficacy of current treatment paradigms by promoting selective sensitization or
protection of diseased and normal cells, respectively. This assay will summarize the probe development efforts that are currently ongoing.
   '''
        mockProjectAdapter.project = project
        mockProjectAdapter.name = "Summary of the probe development effort to identify inhibitors of AddAB recombination protein complex"


        mockProjectAdapter.searchHighlight = '''for the bacteria to persist. The H. pylori AddAB helicase-exonuclease is required for DNA repair
and efficient stomach colonization (3), and inhibitors of this enzyme may be useful antibacterial drugs for treating these infections.
The AddAB class of enzymes is closely related to the RecBCD class of helicase
'''
        mockProjectAdapter.annotations << ['First annotation': '''This is the 1st annotation for this project''']
        mockProjectAdapter.annotations << ['Second annotation': '''This is the 2nd annotation for this project''']
        mockProjectAdapter.annotations << ['Third annotation': '''This is the 3rd annotation for this project''']
        mockProjectAdapterMap.put(projectId, mockProjectAdapter)

        mockProjectAdapter = new MockProjectAdapter()
        project = new Project()
        projectId = new Long(2367)
        project.setId(projectId)
        project.name = "Probe Development Summary for Inhibitors of RecQ-Like Dna Helicase 1 (RECQ1)"
        project.description = '''
Assay Provider: David M. Wilson, III, National Institute on Aging, NIH Screening Center PI: Austin, C.P. Screening Center: NIH Chemical Genomics Center [NCGC]
The apurinic/apyrimidinic endonuclease APE1 is the primary mammalian enzyme responsible for the removal of abasic (or AP) sites in DNA and functions centrally
in the base excision DNA repair (BER) pathway. Recent studies suggested a link between an overexpression of APE1 in many cancers and resistance of these tumor
cells to radio- and chemotherapy. Thus, targeting APE1 could improve the efficacy of current treatment paradigms by promoting selective sensitization or
protection of diseased and normal cells, respectively. This assay will summarize the probe development efforts that are currently ongoing.
   '''
        mockProjectAdapter.project = project
        mockProjectAdapter.name = "Probe Development Summary for Inhibitors of RecQ-Like Dna Helicase 1 (RECQ1)"

        mockProjectAdapter.searchHighlight = '''
       develop resistance to therapy through enhanced activity of DNA repair functions; this has led
       to an increased interest in developing drugs that interfere with DNA repair, which could sensitise cancer cells to conventional
       therapy. This summary assay pertains to human RECQ1, which is important
       '''
        mockProjectAdapter.annotations << ['First annotation': '''This is the 1st annotation for this project''']
        mockProjectAdapter.annotations << ['Second annotation': '''This is the 2nd annotation for this project''']
        mockProjectAdapter.annotations << ['Third annotation': '''This is the 3rd annotation for this project''']
        mockProjectAdapterMap.put(projectId, mockProjectAdapter)


    }

    private static void constructMockCompoundAdapter() {
        MockCompoundAdapter compoundAdapter = new MockCompoundAdapter()
        compoundAdapter.searchHighlight = '''
Although the mechanism of action is not understood, chloroxine may slow down mitotic activity in the epidermis,
thereby reducing excessive scaling associated with dandruff or seborrheic dermatitis of the scalp.
Chloroxine induces SOS-<b>DNA repair</b> in E. coli, so chloroxine may be genotoxic to bacteria
'''
        compoundAdapter.pubChemCID = 2722
        compoundAdapter.structureSMILES = "OC1=C(Cl)C=C(Cl)C2=C1N=CC=C2"
        compoundAdapter.name = "5,7-dichloroquinolin-8-ol"

        mockCompoundAdapterMap.put(compoundAdapter.pubChemCID, compoundAdapter)


        compoundAdapter = new MockCompoundAdapter()
        compoundAdapter.searchHighlight = '''
      inhibition. The affinity of clofarabine triphosphate for these enzymes is similar to or greater than that of
      deoxyadenosine triphosphate. In preclinical models, clofarabine has demonstrated the ability to inhibit <b>DNA repair</b>
      by incorporation into the DNA chain during the repair process. Clofarabine 5
'''
        compoundAdapter.pubChemCID = 16760208
        compoundAdapter.name = "(2R,3S,5R)-5-(6-amino-2-chloropurin-9-yl)-4-fluoro-2-(hydroxymethyl)oxolan-3-ol"
        compoundAdapter.structureSMILES = "NC1=NC(Cl)=NC2=C1N=CN2[C@@H]1O[C@H](CO)[C@H](O)C1F"

        mockCompoundAdapterMap.put(compoundAdapter.pubChemCID, compoundAdapter)



        compoundAdapter = new MockCompoundAdapter()
        compoundAdapter.searchHighlight = '''
    alkyltransferase, which is the <b>DNA repair</b> protein that specifically removes alkyl groups at the O6 position of guanine.
    Cells lines that have lower levels of AGT are more sensitive to the cytotoxicity of temozolomide.
    It is also suggested that cytotoxic mechanism of temozolomide is related to the failure
'''
        compoundAdapter.pubChemCID = 354624
        compoundAdapter.structureSMILES = "NC1=NC(Cl)=NC2=C1N=CN2C1OC(CO)C(O)C1F"
        compoundAdapter.name = "5-(6-amino-2-chloropurin-9-yl)-4-fluoro-2-(hydroxymethyl)oxolan-3-ol"

        mockCompoundAdapterMap.put(compoundAdapter.pubChemCID, compoundAdapter)
    }

    private static void constructMockExperiment() {
        MockExperiment mockedExperiment = new MockExperiment()
        mockedExperiment.id = 1904
        mockedExperiment.assay = new Assay()
        mockedExperiment.type = ExperimentType.Confirmatory
        mockedExperiment.role = ExperimentRole.Primary
        mockedExperiment.category = ExperimentCategory.MLPCN
        mockedExperiment.name = 'qHTS Assay for Inhibitors of Bloom\'s syndrome helicase (BLM)'
        mockedExperiment.description = '''Survival of cells and the faithful propagation of the genome depend on elaborate mechanisms of detecting and repairing DNA damage. Treatment of advanced cancer relies on radiation therapy or chemotherapy, which kill cancer cells by causing extensive DNA damage. It is often found, that cancer cells develop resistance to therapy through enhanced activity of DNA repair functions; this has led to an increased interest in developing drugs that interfere with DNA repair, which could sensitize cancer cells to conventional therapy.
This validation qHTS assay pertains to human BLM, which is important in resolving abnormal DNA structures formed during replication or homologous recombination. Shutting down the expression of BLM leads to chromosomal instability and higher radiation sensitivity in cultured cells.
The validation assay was a fluorescence quenching based kinetic qHTS for BLM Helicase DNA unwinding. The activity was measured as ATP-dependent separation of a 20-bp DNA duplex extended by 30-nt single-stranded tails (forked duplex). The forked DNA substrate was tagged with rhodamine fluorophore (carboxytetramethyl rhodamine, TAMRA) and BHQ-2 (Black Hole Quencher 2) dark quencher. Strand separation results in an increase in the fluorescence of TAMRA (excitation 525 nm, emission 598 nm). This substrate construct operates in a red-shifted region where very few compound library members have been noted to fluoresce (PubChem AIDs 593 and 594). An additional feature of the assay is the inclusion of 2.5 ug/ml poly (dIdC), to reduce interference by compounds such as DNA intercalators, a major source of false inhibitors.
Assay Providers:\n
  Ian Hickson, University of Oxford\n
  Opher Gileadi, Structural Genomics Consortium, University of Oxford\n
Screening Center PI: Austin, C.P.\n
Screening Center: NIH Chemical Genomics Center [NCGC]'''
        mockedExperiment.addExperimentCompoundCountValue(347941)
        mockedExperiment.addExperimentSubstanceCountValue(354860)
        mockedExperiment.pubchemAid = 2528
        mockExperimentMap.put(mockedExperiment.id, mockedExperiment)


        mockedExperiment = new MockExperiment()
        mockedExperiment.id = 2757
        mockedExperiment.assay = new Assay()
        mockedExperiment.type = ExperimentType.Summary
        mockedExperiment.role = ExperimentRole.Primary
        mockedExperiment.category = ExperimentCategory.MLPCN
        mockedExperiment.name = 'Probe Development Summary for Inhibitors of Bloom\'s syndrome helicase (BLM)'
        mockedExperiment.description = '''Survival of cells and the faithful propagation of the genome depend on elaborate mechanisms of detecting and repairing DNA damage. Treatment of advanced cancer relies on radiation therapy or chemotherapy, which kill cancer cells by causing extensive DNA damage. It is often found, that cancer cells develop resistance to therapy through enhanced activity of DNA repair functions; this has led to an increased interest in developing drugs that interfere with DNA repair, which could sensitize cancer cells to conventional therapy.
This summary assay pertains to the Bloom syndrome helicase (BLM), which is important in resolving abnormal DNA structures formed during replication or homologous recombination. Shutting down the expression of BLM leads to chromosomal instability and higher radiation sensitivity in cultured cells.
The goal of this project is to develop inhibitors of BLM activity, which can be used in cell and animal models to examine the consequences of inhibition on the survival of cancer cells. Initial candidates will be identified by a quantitative high-throughput screen (qHTS) of the MLSMR compound library, using a fluorescence-based in vitro biochemical assay that reveals inhibitors of BLM DNA unwinding activity. The resulting compounds will then be subject to orthogonal, secondary biochemical assays, to triage the initial hits, to classify compounds based on mode of action, and to derive structure#activity relationships (SARs) of candidate effectors. SAR and protein structural information will be used in further chemical development to improve the potency and selectivity of the compounds. Cell-based assays will then be applied as the first step in utilizations of the verified inhibitors, examining their effects on cancer cell survival and sensitivity to radiation and chemotherapeutics.
This assay will summarize the probe development efforts that are currently ongoing.
Assay Providers:\n
  Ian Hickson, University of Oxford\n
  Opher Gileadi, Structural Genomics Consortium, University of Oxford\n
  Alessandro Vindigni, International Center for Biotechnology and Genetic Engineering\n
Screening Center PI: Austin, C.P.\n
Screening Center: NIH Chemical Genomics Center [NCGC]'''
        mockedExperiment.addExperimentCompoundCountValue(0)
        mockedExperiment.addExperimentSubstanceCountValue(0)
        mockedExperiment.pubchemAid = 2386
        mockExperimentMap.put(mockedExperiment.id, mockedExperiment)


        mockedExperiment = new MockExperiment()
        mockedExperiment.id = 3470
        mockedExperiment.assay = new Assay()
        mockedExperiment.type = ExperimentType.Confirmatory
        mockedExperiment.role = ExperimentRole.Counterscreen
        mockedExperiment.category = ExperimentCategory.MLPCN
        mockedExperiment.name = 'Counterscreen for BLMA Inhibitors: ADP Fluorescence Polarization Displacement Assay'
        mockedExperiment.description = '''In order to gain further insight into the mode of action of the BLMAscreening hits, we have profiled them in a set of miniaturized fluorescence polarization assays designed to report on compounds which competitively displace either co-substrate (ATP or DNA). The appropriate fluorescently-labeled probe was used: BODIPY Texas Red-labeled ADP was expected to be competed off by ATP-competitive inhibitors, while single-TAMRA labeled forked-duplex or short single-stranded DNA molecules served as probes for DNA-competitive compounds.
Assay Providers:\n
  Ian Hickson, University of Oxford\n
  Opher Gileadi, Structural Genomics Consortium, University of Oxford\n
Screening Center PI: Austin, C.P.\n
Screening Center: NIH Chemical Genomics Center [NCGC]'''
        mockedExperiment.addExperimentCompoundCountValue(529)
        mockedExperiment.addExperimentSubstanceCountValue(534)
        mockedExperiment.pubchemAid = 2712
        mockExperimentMap.put(mockedExperiment.id, mockedExperiment)
    }
}
class MockAssayAdapter extends AssayAdapter {
    String searchHighlight
    String name

    public Collection<Value> getAnnotations() {
        return [];
    }
}
class MockProjectAdapter extends ProjectAdapter {
    String searchHighlight
    String name
    Integer numberOfExperiments = 3


    public Collection<Value> getAnnotations() {
        return [];
    }
}
class MockExperiment extends Experiment {
//Implemented in super
//    String name
//    Long id
//    ExperimentRole role
//    String description
//    Assay assay

    void addExperimentCompoundCountValue(Integer compoundNum) {
        final DataSource dataSource = new DataSource("stuff", "v1")
        this.add(new IntValue(dataSource, ExperimentValues.ExperimentCompoundCountValue, compoundNum))
    }

    void addExperimentSubstanceCountValue(Integer substanceNum) {
        final DataSource dataSource = new DataSource("stuff", "v1")
        this.add(new IntValue(dataSource, ExperimentValues.ExperimentSubstanceCountValue, substanceNum))
    }
}

class MockCompoundAdapter extends CompoundAdapter {
    Long[] sids = [70319, 609991, 866273, 3132781]
    Long pubChemCID
    String structureSMILES
    String searchHighlight
    String name



    public Compound getCompound() {
        return new Compound() {
            String getPreferredName() {
                return name
            }
        }
    }

    /*
    * MolecularData interface
    */

    public String formula() { return "C9H5Cl2NO" }

    public Double mwt() { return new Double(214.048) }

    public Double exactMass() { return new Double(212.975) }

    public Double TPSA() {
        return new Double(33.1);
    }

    public Double logP() {
        return new Double(0.00);
    }

    public List<Long> getPubChemSIDs() {
        return sids;
    }
}