package mockServices

import bard.core.DataSource
import bard.core.Probe
import bard.core.Value
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.util.StructureSearchParams
import bardqueryapi.IQueryService
import bardqueryapi.QueryHelperService
import bardqueryapi.SearchFilter
import bard.core.interfaces.*
import bard.core.rest.spring.compounds.CompoundSummary
import spock.lang.Shared
import com.fasterxml.jackson.databind.ObjectMapper
import bard.core.rest.spring.assays.AssayAnnotation

class MockQueryService implements IQueryService {
    QueryHelperService queryHelperService
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String COMPOUND_SUMMARY = '''
    {
       "testedExptdata":
       [
           {
               "exptDataId": "3377.26757972",
               "eid": 492961,
               "cid": 16760208,
               "sid": 26757972,
               "bardExptId": 3377,
               "runset": "default",
               "outcome": 1,
               "score": 0,
               "classification": 1,
               "potency": null,
               "readouts":
               [
                   {
                       "name": "Activity",
                       "s0": null,
                       "sInf": null,
                       "hill": null,
                       "ac50": null,
                       "cr":
                       [
                           [
                               8.220000192522999e-9,
                               -0.2345
                           ],
                           [
                               4.10999990999699e-8,
                               2.4632
                           ],
                           [
                               2.06000000238419e-7,
                               -2.8264
                           ],
                           [
                               0.00000102999997138977,
                               -1.9348
                           ],
                           [
                               0.0000051399998664856,
                               -0.1097
                           ],
                           [
                               0.000024312499999999998,
                               0.29
                           ],
                           [
                               0.0000514000015258789,
                               -7.8803
                           ]
                       ],
                       "npoint": 7,
                       "concUnit": "M",
                       "responseUnit": null
                   }
               ],
               "resourcePath": "/exptdata/3377.26757972"
           },
           {
               "exptDataId": "1420.26757972",
               "eid": 624044,
               "cid": 16760208,
               "sid": 26757972,
               "bardExptId": 1420,
               "runset": "default",
               "outcome": 2,
               "score": 90,
               "classification": null,
               "potency": 8.9058,
               "readouts":
               [
                   {
                       "name": "W460-Activity",
                       "s0": -0.0583,
                       "sInf": -100.294,
                       "hill": 1.2221,
                       "ac50": 0.000008905799999999999,
                       "cr":
                       [
                           [
                               1.0999999940395401e-9,
                               -2.492
                           ],
                           [
                               1.0999999940395401e-9,
                               -0.1863
                           ],
                           [
                               1.0999999940395401e-9,
                               -4.46984
                           ],
                           [
                               5.47999981790781e-9,
                               -34.186
                           ],
                           [
                               5.47999981790781e-9,
                               -10.2118
                           ],
                           [
                               5.47999981790781e-9,
                               -21.2604
                           ],
                           [
                               2.74000000208616e-8,
                               -1.933
                           ],
                           [
                               2.74000000208616e-8,
                               -5.1577
                           ],
                           [
                               2.74000000208616e-8,
                               1.69217
                           ],
                           [
                               1.3699999451637302e-7,
                               0.6076
                           ],
                           [
                               1.3699999451637302e-7,
                               -6.6305
                           ],
                           [
                               1.3699999451637302e-7,
                               1.6709
                           ],
                           [
                               7.11000978946686e-7,
                               -3.7281
                           ],
                           [
                               7.11000978946686e-7,
                               0.0576
                           ],
                           [
                               7.11000978946686e-7,
                               4.04171
                           ],
                           [
                               0.00000350651001930237,
                               -36.3845
                           ],
                           [
                               0.00000350651001930237,
                               -19.7831
                           ],
                           [
                               0.00000350651001930237,
                               -13.6112
                           ],
                           [
                               0.00000765999984741211,
                               -64.4744
                           ],
                           [
                               0.00000765999984741211,
                               -39.2249
                           ],
                           [
                               0.00000765999984741211,
                               -17.0927
                           ],
                           [
                               0.0000171000003814697,
                               -72.572
                           ],
                           [
                               0.0000171000003814697,
                               -67.1737
                           ],
                           [
                               0.0000171000003814697,
                               6.38658
                           ],
                           [
                               0.00003829999923706049,
                               -94.9752
                           ],
                           [
                               0.00003829999923706049,
                               -84.0315
                           ],
                           [
                               0.00003829999923706049,
                               -0.119884
                           ]
                       ],
                       "npoint": 27,
                       "concUnit": "M",
                       "responseUnit": null
                   }
               ],
               "resourcePath": "/exptdata/1420.26757972"
           }
       ],
       "testedAssays":
       [
           "/assays/3377",
           "/assays/1420"
       ],
       "nhit": 1,
       "hitAssays":
       [
           "/assays/1420"
       ],
       "ntest": 2,
       "hitExptdata":
       [
           {
               "exptDataId": "1420.26757972",
               "eid": 624044,
               "cid": 16760208,
               "sid": 26757972,
               "bardExptId": 1420,
               "runset": "default",
               "outcome": 2,
               "score": 90,
               "classification": null,
               "potency": 8.9058,
               "readouts":
               [
                   {
                       "name": "W460-Activity",
                       "s0": -0.0583,
                       "sInf": -100.294,
                       "hill": 1.2221,
                       "ac50": 0.000008905799999999999,
                       "cr":
                       [
                           [
                               1.0999999940395401e-9,
                               -2.492
                           ],
                           [
                               1.0999999940395401e-9,
                               -0.1863
                           ],
                           [
                               1.0999999940395401e-9,
                               -4.46984
                           ],
                           [
                               5.47999981790781e-9,
                               -34.186
                           ],
                           [
                               5.47999981790781e-9,
                               -10.2118
                           ],
                           [
                               5.47999981790781e-9,
                               -21.2604
                           ],
                           [
                               2.74000000208616e-8,
                               -1.933
                           ],
                           [
                               2.74000000208616e-8,
                               -5.1577
                           ],
                           [
                               2.74000000208616e-8,
                               1.69217
                           ],
                           [
                               1.3699999451637302e-7,
                               0.6076
                           ],
                           [
                               1.3699999451637302e-7,
                               -6.6305
                           ],
                           [
                               1.3699999451637302e-7,
                               1.6709
                           ],
                           [
                               7.11000978946686e-7,
                               -3.7281
                           ],
                           [
                               7.11000978946686e-7,
                               0.0576
                           ],
                           [
                               7.11000978946686e-7,
                               4.04171
                           ],
                           [
                               0.00000350651001930237,
                               -36.3845
                           ],
                           [
                               0.00000350651001930237,
                               -19.7831
                           ],
                           [
                               0.00000350651001930237,
                               -13.6112
                           ],
                           [
                               0.00000765999984741211,
                               -64.4744
                           ],
                           [
                               0.00000765999984741211,
                               -39.2249
                           ],
                           [
                               0.00000765999984741211,
                               -17.0927
                           ],
                           [
                               0.0000171000003814697,
                               -72.572
                           ],
                           [
                               0.0000171000003814697,
                               -67.1737
                           ],
                           [
                               0.0000171000003814697,
                               6.38658
                           ],
                           [
                               0.00003829999923706049,
                               -94.9752
                           ],
                           [
                               0.00003829999923706049,
                               -84.0315
                           ],
                           [
                               0.00003829999923706049,
                               -0.119884
                           ]
                       ],
                       "npoint": 27,
                       "concUnit": "M",
                       "responseUnit": null
                   }
               ],
               "resourcePath": "/exptdata/1420.26757972"
           }
       ]
    }
    '''

    @Override
    CompoundSummary getSummaryForCompound(Long cid) {
        return objectMapper.readValue(COMPOUND_SUMMARY, CompoundSummary.class)
    }

    List<Long> findSubstancesByCid(Long cid) {
        return [1234, 5678, 91011]
    }

    final Map<Long, MockCompoundAdapter> mockCompoundAdapterMap = [:]

    final Map<Long, MockAssayAdapter> mockAssayAdapterMap = [:]

    final Map<Long, MockProjectAdapter> mockProjectAdapterMap = [:]

    final Map<Long, MockExperiment> mockExperimentMap = [:]

    public MockQueryService() {
        constructMockCompoundAdapter()
        constructMockAssayAdapter()
        constructMockProjectAdapter()
        constructMockExperiment()
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

        Set<Long> keySet = mockCompoundAdapterMap.keySet()
        for (Long key : keySet) {
            foundCompoundAdapters.add((MockCompoundAdapter) mockCompoundAdapterMap.get(key))
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

        Set<Long> keySet = mockAssayAdapterMap.keySet()
        for (Long key : keySet) {
            foundAssayAdapters.add(mockAssayAdapterMap.get(key))
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

        Set<Long> keySet = mockProjectAdapterMap.keySet()
        for (Long key : keySet) {
            foundProjectAdapters.add(mockProjectAdapterMap.get(key))
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
    Map structureSearch(String smiles, StructureSearchParams.Type structureSearchParamsType, List<SearchFilter> searchFilters = [], Integer top = 10, Integer skip = 0, Integer nhits = -1) {
        return findCompoundsByTextSearch("", 10, 0, searchFilters)
    }

    @Override
    Map structureSearch(Integer cid, StructureSearchParams.Type structureSearchParamsType, List<SearchFilter> searchFilters = [], Integer top = 10, Integer skip = 0, Integer nhits = -1) {
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

        return mockCompoundAdapterMap.get(compoundId)
    }

    /**
     * Given an assayId, get detailed Assay information from the REST API
     * @param assayId
     * @return AssayAdapter
     */
    Map showAssay(final Long assayId) {

        AssayAdapter assayAdapter = mockAssayAdapterMap.get(assayId)
        MockExperiment mockExperiment = mockExperimentMap.get(1904)
        return [assayAdapter: assayAdapter, experiments: [mockExperiment], projects: []]
    }
    /**
     * Given a projectId, get detailed Project information from the JDO
     * @param projectId
     * @return ProjectAdapter
     */
    Map showProject(final Long projectId) {
        return [projectAdapter: mockProjectAdapterMap.get(projectId),
                experiments: mockExperimentMap.values(),
                assays: [
                        mockAssayAdapterMap.get(588636 as Long),
                        mockAssayAdapterMap.get(449731 as Long),
                        mockAssayAdapterMap.get(588623 as Long)]
        ]
    }

    //==============================================Auto Complete ======
    /**
     *
     * @param term
     * @return the list of maps to use for auto suggest
     */
    public List<Map<String, String>> autoComplete(final String term) {

        //the number of items to retrieve per category
        final Map<String, List<String>> autoSuggestResponseFromJDO = [
                "gobp_term":
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

    private void constructMockAssayAdapter() {

        MockAssayAdapter mockAssayAdapter = new MockAssayAdapter()

        mockAssayAdapter.name = "qHTS for Inhibitors of Polymerase Eta: Summary"
        Long assayId = new Long(588636)
        mockAssayAdapter.assayId = assayId
        mockAssayAdapter.category = AssayCategory.MLPCN
        mockAssayAdapter.protocol = "Please see linked AIDs for a detailed description of each assay."
        mockAssayAdapter.comments = "This project is on-going and will be updated at a later point with our findings."
        mockAssayAdapter.description =
            mockAssayAdapterMap.put(assayId, mockAssayAdapter)




        mockAssayAdapter = new MockAssayAdapter()
        mockAssayAdapter.name = "Summary of the probe development effort to identify inhibitors of AddAB recombination protein complex"
        assayId = new Long(449731)
        mockAssayAdapter.assayId = assayId
        mockAssayAdapterMap.put(assayId, mockAssayAdapter)


        mockAssayAdapter = new MockAssayAdapter()
        assayId = new Long(588623)
        mockAssayAdapter.assayId = assayId
        mockAssayAdapterMap.put(assayId, mockAssayAdapter)

    }

    private void constructMockProjectAdapter() {
        MockProjectAdapter mockProjectAdapter = new MockProjectAdapter()
        Long projectId = new Long(2324)
        mockProjectAdapter.id = projectId
        mockProjectAdapter.description = mockProjectAdapter.annotations << ['First annotation': '''This is the 1st annotation for this project''']
        mockProjectAdapter.annotations << ['Second annotation': '''This is the 2nd annotation for this project''']
        mockProjectAdapter.annotations << ['Third annotation': '''This is the 3rd annotation for this project''']

        mockProjectAdapter.name = "Name 2"
        mockProjectAdapterMap.put(projectId, mockProjectAdapter)



        mockProjectAdapter = new MockProjectAdapter()
        projectId = new Long(449731)
        mockProjectAdapter.id = projectId

        mockProjectAdapter.description = '''
Assay Provider: David M. Wilson, III, National Institute on Aging, NIH Screening Center PI: Austin, C.P. Screening Center: NIH Chemical Genomics Center [NCGC]
The apurinic/apyrimidinic endonuclease APE1 is the primary mammalian enzyme responsible for the removal of abasic (or AP) sites in DNA and functions centrally
in the base excision DNA repair (BER) pathway. Recent studies suggested a link between an overexpression of APE1 in many cancers and resistance of these tumor
cells to radio- and chemotherapy. Thus, targeting APE1 could improve the efficacy of current treatment paradigms by promoting selective sensitization or
protection of diseased and normal cells, respectively. This assay will summarize the probe development efforts that are currently ongoing.
   '''
        mockProjectAdapter.name = "Summary of the probe development effort to identify inhibitors of AddAB recombination protein complex"


        mockProjectAdapter.annotations << ['First annotation': '''This is the 1st annotation for this project''']
        mockProjectAdapter.annotations << ['Second annotation': '''This is the 2nd annotation for this project''']
        mockProjectAdapter.annotations << ['Third annotation': '''This is the 3rd annotation for this project''']
        mockProjectAdapterMap.put(projectId, mockProjectAdapter)

        mockProjectAdapter = new MockProjectAdapter()
        projectId = new Long(2367)
        mockProjectAdapter.id = projectId
        mockProjectAdapter.name = "Probe Development Summary for Inhibitors of RecQ-Like Dna Helicase 1 (RECQ1)"
        mockProjectAdapter.description = '''
                                            Assay Provider: David M. Wilson, III, National Institute on Aging, NIH Screening Center PI: Austin, C.P. Screening Center: NIH Chemical Genomics Center [NCGC]
                                            The apurinic/apyrimidinic endonuclease APE1 is the primary mammalian enzyme responsible for the removal of abasic (or AP) sites in DNA and functions centrally
                                            in the base excision DNA repair (BER) pathway. Recent studies suggested a link between an overexpression of APE1 in many cancers and resistance of these tumor
                                            cells to radio- and chemotherapy. Thus, targeting APE1 could improve the efficacy of current treatment paradigms by promoting selective sensitization or
                                            protection of diseased and normal cells, respectively. This assay will summarize the probe development efforts that are currently ongoing.
                                            '''
        mockProjectAdapter.annotations << ['First annotation': '''This is the 1st annotation for this project''']
        mockProjectAdapter.annotations << ['Second annotation': '''This is the 2nd annotation for this project''']
        mockProjectAdapter.annotations << ['Third annotation': '''This is the 3rd annotation for this project''']
        mockProjectAdapterMap.put(projectId, mockProjectAdapter)


    }

    private void constructMockCompoundAdapter() {
        MockCompoundAdapter compoundAdapter = new MockCompoundAdapter()
        compoundAdapter.pubChemCID = 2722
        compoundAdapter.structureSMILES = "OC1=C(Cl)C=C(Cl)C2=C1N=CC=C2"
        compoundAdapter.name = "5,7-dichloroquinolin-8-ol"
        mockCompoundAdapterMap.put(compoundAdapter.pubChemCID, compoundAdapter)


        compoundAdapter = new MockCompoundAdapter()
        compoundAdapter.pubChemCID = 16760208
        compoundAdapter.name = "(2R,3S,5R)-5-(6-amino-2-chloropurin-9-yl)-4-fluoro-2-(hydroxymethyl)oxolan-3-ol"
        compoundAdapter.structureSMILES = "NC1=NC(Cl)=NC2=C1N=CN2[C@@H]1O[C@H](CO)[C@H](O)C1F"
        mockCompoundAdapterMap.put(compoundAdapter.pubChemCID, compoundAdapter)



        compoundAdapter = new MockCompoundAdapter()
        compoundAdapter.pubChemCID = 354624
        compoundAdapter.structureSMILES = "NC1=NC(Cl)=NC2=C1N=CN2C1OC(CO)C(O)C1F"
        compoundAdapter.name = "5-(6-amino-2-chloropurin-9-yl)-4-fluoro-2-(hydroxymethyl)oxolan-3-ol"
        mockCompoundAdapterMap.put(compoundAdapter.pubChemCID, compoundAdapter)
    }

    private void constructMockExperiment() {
        MockExperiment mockedExperiment = new MockExperiment()
        mockedExperiment.exptId = 1904
        mockedExperiment.type = 2
        mockedExperiment.classification = 1
        mockedExperiment.category = 0
        mockedExperiment.name = 'qHTS Assay for Inhibitors of Bloom\'s syndrome helicase (BLM)'
        mockedExperiment.description = '''Survival of cells and the faithful propagation of the genome depend on elaborate mechanisms of detecting and repairing DNA damage. Treatment of advanced cancer relies on radiation therapy or chemotherapy, which kill cancer cells by causing extensive DNA damage. It is often found, that cancer cells develop resistance to therapy through enhanced activity of DNA repair functions; this has led to an increased interest in developing drugs that interfere with DNA repair, which could sensitize cancer cells to conventional therapy.
This validation qHTS assay pertains to human BLM, which is important in resolving abnormal DNA structures formed during replication or homologous recombination. Shutting down the expression of BLM leads to chromosomal instability and higher radiation sensitivity in cultured cells.
The validation assay was a fluorescence quenching based kinetic qHTS for BLM Helicase DNA unwinding. The activity was measured as ATP-dependent separation of a 20-bp DNA duplex extended by 30-nt single-stranded tails (forked duplex). The forked DNA substrate was tagged with rhodamine fluorophore (carboxytetramethyl rhodamine, TAMRA) and BHQ-2 (Black Hole Quencher 2) dark quencher. Strand separation results in an increase in the fluorescence of TAMRA (excitation 525 nm, emission 598 nm). This substrate construct operates in a red-shifted region where very few compound library members have been noted to fluoresce (PubChem AIDs 593 and 594). An additional feature of the assay is the inclusion of 2.5 ug/ml poly (dIdC), to reduce interference by compounds such as DNA intercalators, a major source of false inhibitors.
Assay Providers:\n
  Ian Hickson, University of Oxford\n
  Opher Gileadi, Structural Genomics Consortium, University of Oxford\n
Screening Center PI: Austin, C.P.\n
Screening Center: NIH Chemical Genomics Center [NCGC]'''
        mockedExperiment.pubchemAid = 2528
        mockExperimentMap.put(mockedExperiment.id, mockedExperiment)


        mockedExperiment = new MockExperiment()
        mockedExperiment.exptId = 2757
        mockedExperiment.type = ExperimentType.Summary.ordinal()
        mockedExperiment.classification = ExperimentRole.Primary.ordinal()
        mockedExperiment.category = ExperimentCategory.MLPCN.ordinal()
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
        mockedExperiment.pubchemAid = 2386
        mockExperimentMap.put(mockedExperiment.id, mockedExperiment)


        mockedExperiment = new MockExperiment()
        mockedExperiment.exptId = 3470
        mockedExperiment.assayId = 37
        mockedExperiment.type = ExperimentType.Confirmatory.ordinal()
        mockedExperiment.classification = 0
        mockedExperiment.category = 1
        mockedExperiment.name = 'Counterscreen for BLMA Inhibitors: ADP Fluorescence Polarization Displacement Assay'
        mockedExperiment.description = '''In order to gain further insight into the mode of action of the BLMAscreening hits, we have profiled them in a set of miniaturized fluorescence polarization assays designed to report on compounds which competitively displace either co-substrate (ATP or DNA). The appropriate fluorescently-labeled probe was used: BODIPY Texas Red-labeled ADP was expected to be competed off by ATP-competitive inhibitors, while single-TAMRA labeled forked-duplex or short single-stranded DNA molecules served as probes for DNA-competitive compounds.
Assay Providers:\n
  Ian Hickson, University of Oxford\n
  Opher Gileadi, Structural Genomics Consortium, University of Oxford\n
Screening Center PI: Austin, C.P.\n
Screening Center: NIH Chemical Genomics Center [NCGC]'''
        mockedExperiment.pubchemAid = 2712
        mockExperimentMap.put(mockedExperiment.id, mockedExperiment)
    }

    @Override
    Map showProbeList() {
        final List<CompoundAdapter> foundCompoundAdapters = []

        Set<Long> keySet = mockCompoundAdapterMap.keySet()
        for (Long key : keySet) {
            foundCompoundAdapters.add((MockCompoundAdapter) mockCompoundAdapterMap.get(key))
        }
        return [
                compoundAdapters: foundCompoundAdapters,
                facets: [],
                nhits: foundCompoundAdapters.size(),
                appliedFilters: [:]
        ]
    }

    @Override
    Map findExperimentDataById(Long experimentId, Integer top, Integer skip) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
}
class MockAssayAdapter implements AssayAdapterInterface {
    String name
    Long assayId
    AssayCategory category
    String protocol
    String comments
    String description
    AssayType assayType
    AssayRole assayRole
    String source
    Long aid

    @Override
    String getHighlight() {
        return "Score: 2.0, Matched Field: Name"
    }

    MockAssayAdapter() {
        super()
    }

    public List<AssayAnnotation> getAnnotations() {
        final List<AssayAnnotation> annos = new ArrayList<AssayAnnotation>();
        AssayAnnotation annotation = new AssayAnnotation()
        annotation.key = "target"
        annotation.value = "WEE1"
        annos.add(annotation)
        return annos
    }

    @Override
    List<String> getKeggDiseaseNames() {
        return ["Neurodegenerative disease", "Neurodegenerative disease", "Neurodegenerative disease"]
    }

    @Override
    List<String> getKeggDiseaseCategories() {
        return ["Amyotrophic lateral sclerosis (ALS)", "Lou Gehrig's disease", "Progressive supranuclear palsy (PSP)"]
    }

    @Override
    Map<String, List<String>> getKeggAnnotations() {
        Map<String, List<String>> annos = new HashMap<String, List<String>>()
        annos.put(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource, ["Amyotrophic lateral sclerosis (ALS)", "Lou Gehrig's disease", "Progressive supranuclear palsy (PSP)"])
        annos.put(EntityNamedSources.KEGGDiseaseNameAnnotationSource, ["Neurodegenerative disease", "Neurodegenerative disease", "Neurodegenerative disease"])
        return annos;
    }

    @Override
    Long getCapAssayId() {
        return this.assayId ?: 233  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    AssayType getType() {
        return this.assayType ?: AssayType.Confirmatory  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    AssayRole getRole() {
        return assayRole ?: AssayRole.SecondaryConfirmation  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    AssayCategory getCategory() {
        return category ?: AssayCategory.MLPCN
    }

    @Override
    String getDescription() {
        return this.description ?: '''
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
    }

    @Override
    Long getId() {
        return assayId ?: 22
    }

    @Override
    String getProtocol() {
        return this.protocol ?: "Protocol"
    }

    @Override
    String getComments() {
        return this.comments ?: "Comments"
    }

    @Override
    Long getAid() {
        return this.aid ?: 244
    }

    @Override
    String getSource() {
        return this.source ?: "Source"  //To change body of implemented methods use File | Settings | File Templates.
    }
}
class MockProjectAdapter implements ProjectAdapterInterface {
    Long id
    String name
    String description
    String grantNumber
    String labName
    Integer numExperiments
    Collection<Value> annotations = []

    @Override
    String getHighlight() {
        return "Score: 2.0, Matched Field: Name"
    }

    @Override
    Long getId() {
        return id ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getName() {
        return name ?: "Project Name" //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getDescription() {
        return description ?: '''
                    Assay Provider: David M. Wilson, III, National Institute on Aging, NIH Screening Center PI: Austin, C.P. Screening Center: NIH Chemical Genomics Center [NCGC]
                    The apurinic/apyrimidinic endonuclease APE1 is the primary mammalian enzyme responsible for the removal of abasic (or AP) sites in DNA and functions centrally
                    in the base excision DNA repair (BER) pathway. Recent studies suggested a link between an overexpression of APE1 in many cancers and resistance of these tumor
                    cells to radio- and chemotherapy. Thus, targeting APE1 could improve the efficacy of current treatment paradigms by promoting selective sensitization or
                    protection of diseased and normal cells, respectively. This assay will summarize the probe development efforts that are currently ongoing.
                 '''
    }

    @Override
    String getGrantNumber() {
        return grantNumber ?: "GI2"  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getLaboratoryName() {
        return labName ?: "Broad" //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    List<Probe> getProbes() {
        return [new Probe("2", "ML18", "http://bard.org", "CCC"), new Probe("28", "ML20", "http://bard.org", "CCCC")]  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Integer getNumberOfExperiments() {
        return numExperiments ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Collection<Value> getAnnotations() {
        if (!annotations) {
            this.annotations = new ArrayList<Value>();
            final Map<String, String> terms = getDictionaryTerms()
            for (String key : terms.keySet()) {
                Value value = new bard.core.StringValue(DataSource.DEFAULT, key, terms.get(key))
                this.annotations.add(value)
            }
        }
        return annotations//To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Map<String, String> getDictionaryTerms() {
        return ["grant number": "X01 MH083262-01", "laboratory name": "NCGC", "protein": "gi|92096784|gb|AAI14949.1|Microtubule-associated protein tau [Homo sapiens]"]
    }

    @Override
    Map<String, List<String>> getKeggAnnotations() {
        Map<String, List<String>> annos = new HashMap<String, List<String>>()
        annos.put(EntityNamedSources.KEGGDiseaseCategoryAnnotationSource, ["Amyotrophic lateral sclerosis (ALS)", "Lou Gehrig's disease", "Progressive supranuclear palsy (PSP)"])
        annos.put(EntityNamedSources.KEGGDiseaseNameAnnotationSource, ["Neurodegenerative disease", "Neurodegenerative disease", "Neurodegenerative disease"])
        return annos;

    }
}
class MockExperiment extends ExperimentSearch {

}

class MockCompoundAdapter implements CompoundAdapterInterface {

    Long pubChemCID = 354624
    String structureSMILES = "NC1=NC(Cl)=NC2=C1N=CN2C1OC(CO)C(O)C1F"
    String name = "5-(6-amino-2-chloropurin-9-yl)-4-fluoro-2-(hydroxymethyl)oxolan-3-ol"
    Double mwt
    String formula
    Double exactMass
    Integer hbondDonor
    Integer hbondAcceptor
    Integer rotatable
    Double tpsa
    String iupacName
    String compoundClass
    Integer numAssays
    Integer numActiveAssays
    Long id
    /*
    * MolecularData interface
    */

    public String formula() { return formula ?: "C9H5Cl2NO" }

    public Double mwt() { return this.mwt ?: new Double(214.048) }

    public Double exactMass() { return this.exactMass ?: new Double(212.975) }

    @Override
    Integer hbondDonor() {
        return this.hbondDonor ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Integer hbondAcceptor() {
        return this.hbondAcceptor ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Integer rotatable() {
        return this.rotatable ?: 1  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Double TPSA() {
        return this.tpsa ?: new Double(33.1);
    }

    public Double logP() {
        return new Double(0.00);
    }

    @Override
    String getIupacName() {
        return this.iupacName ?: "propan-2-ol"  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getUrl() {
        return "http://www.compound.com"  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Integer getComplexity() {
        return 1  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getCompoundClass() {
        return this.compoundClass ?: "Drug"  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    int getNumberOfAssays() {
        return numAssays ?: 10  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    int getNumberOfActiveAssays() {
        return numActiveAssays ?: 5  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String resourcePath() {
        return "/compound/223"  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    boolean isDrug() {
        return getCompoundClass() == "Drug"  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getProbeId() {
        return 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    boolean isProbe() {
        return true  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Long getId() {
        return this.id ?: pubChemCID ?: 2  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    String getHighlight() {
        return "Score: 2.0, Matched Field: Name"
    }
}