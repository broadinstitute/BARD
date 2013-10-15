package mockServices

import bard.core.SearchParams
import bard.core.Value
import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.compounds.Compound
import bard.core.rest.spring.compounds.CompoundSummary

import bard.core.rest.spring.compounds.Promiscuity
import bard.core.rest.spring.experiment.ExperimentSearch
import bard.core.rest.spring.project.Project
import bard.core.rest.spring.project.ProjectStep
import bard.core.rest.spring.substances.Substance
import bard.core.rest.spring.util.StructureSearchParams
import bard.core.util.FilterTypes
import bardqueryapi.GroupByTypes
import bardqueryapi.IQueryService
import bardqueryapi.QueryHelperService
import bardqueryapi.SearchFilter
import bardqueryapi.TableModel
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.lang3.tuple.Pair
import spock.lang.Shared
import bard.core.interfaces.*

class MockQueryService implements IQueryService {
    QueryHelperService queryHelperService

    Map<Long, Pair<Long, Long>> findActiveVsTestedForExperiments(final List<Long> capExperimentIds) {
        return [:]
    }

    String histogramDataByEID(long ncgcWarehouseId) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    long numberOfAssays() {
        return 0  //To change body of implemented methods use File | Settings | File Templates.
    }

    long numberOfProjects() {
        return 0  //To change body of implemented methods use File | Settings | File Templates.
    }

    long numberOfExperiments() {
        return 0  //To change body of implemented methods use File | Settings | File Templates.
    }

    long numberOfCompounds() {
        return 0  //To change body of implemented methods use File | Settings | File Templates.
    }

    long numberOfSubstances() {
        return 0  //To change body of implemented methods use File | Settings | File Templates.
    }

    int numberOfProbes() {
        return 0  //To change body of implemented methods use File | Settings | File Templates.
    }

    List<Compound> findRecentlyAddedProbes(int numberOfProbes) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    List<Assay> findRecentlyAddedAssays(int numberOfAssays) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    List<ExperimentSearch> findRecentlyAddedExperiments(int numberOfExperiments) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    List<Project> findRecentlyAddedProjects(int numberOfProjects) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    List<Substance> findRecentlyAddedSubstances(int numberOfSubstances) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

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
    public static final String PROJECT_STEP = '''
    {
        "prevBardExpt": {
            "bardExptId": 12666,
            "capExptId": 2617,
            "bardAssayId": 7540,
            "capAssayId": 2622,
            "pubchemAid": 2572,
            "category": -1,
            "type": -1,
            "summary": 0,
            "assays": 0,
            "classification": -1,
            "substances": 748,
            "compounds": 745,
            "name": "Confirmation qHTS Assay for Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)",
            "description": null,
            "source": null,
            "grantNo": null,
            "deposited": null,
            "updated": "2013-01-26",
            "hasProbe": false,
            "projectIdList": [
                1703
            ],
            "resourcePath": "/experiments/12666"
        },
        "nextBardExpt": {
            "bardExptId": 12661,
            "capExptId": 2610,
            "bardAssayId": 7535,
            "capAssayId": 2615,
            "pubchemAid": 2565,
            "category": -1,
            "type": -1,
            "summary": 0,
            "assays": 0,
            "classification": -1,
            "substances": 564,
            "compounds": 561,
            "name": "Counterscreen for APE1 Inhibitors: qHTS Assay for Inhibitors of Endonuclease IV",
            "description": null,
            "source": null,
            "grantNo": null,
            "deposited": null,
            "updated": "2013-01-26",
            "hasProbe": false,
            "projectIdList": [
                1703
            ],
            "resourcePath": "/experiments/12661"
        },
        "bardProjId": 1703,
        "stepId": 3803,
        "edgeName": "Linked by Compound set (Swamidass)",
        "annotations": [
            {
                "entityId": 3803,
                "entity": "project-step",
                "source": "cap-context",
                "id": 4056,
                "display": "561",
                "contextRef": "Compound Overlap",
                "key": "1242",
                "value": null,
                "extValueId": null,
                "url": null,
                "displayOrder": 0,
                "related": "1703"
            }
        ],
        "resourcePath": ""
    }
    '''

    @Override
    Map getProjectSteps(Long pid) {
        ProjectStep projectStep = objectMapper.readValue(PROJECT_STEP, ProjectStep.class)

        return [projectSteps: [projectStep]]
    }

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

    public static final String PROMISCUITY = '''
    {
        "hscafs":
        [
           {
               "wTested": 17064,
                "sActive": 51,
                "wActive": 1876,
                "aTested": 479,
                "sTested": 51,
                "pScore": 2445,
                "scafid": 46705,
                "aActive": 222,
                "inDrug": true,
                "smiles": "O=C1C=CC(=N)c2ccccc12"
            }
        ],
        "cid": 752424
     }
     '''
    /**
     *
     * @param cid
     * return Map
     * Success would return [status: 200, message: 'Success', promiscuityScore: promiscuityScore]
     * Failure would return [status: 404, message: "Error getting Promiscuity Score for ${CID}", promiscuityScore: null]
     */
    public Map findPromiscuityForCID(Long cid) {
        final Promiscuity promiscuity = objectMapper.readValue(PROMISCUITY, Promiscuity.class)
        if (promiscuity) {
            return [status: 200, message: 'Success', promiscuityScore: promiscuity]
        }
        return [status: 404, message: "Error getting Promiscuity Score for ${cid}", promiscuityScore: null]
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
    Map structureSearch(String smiles, StructureSearchParams.Type structureSearchParamsType, List<SearchFilter> searchFilters, Double threshold, Integer top, Integer skip, Integer nhits) {
        return findCompoundsByTextSearch("", 10, 0, searchFilters)
    }

    @Override
    Map structureSearch(Integer cid, StructureSearchParams.Type structureSearchParamsType, Double threshold, List<SearchFilter> searchFilters, Integer top, Integer skip, Integer nhits) {
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
        mockAssayAdapter.description = "This is a test description."
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
        compoundAdapter.id = 2722
        compoundAdapter.structureSMILES = "OC1=C(Cl)C=C(Cl)C2=C1N=CC=C2"
        compoundAdapter.name = "5,7-dichloroquinolin-8-ol"
        mockCompoundAdapterMap.put(compoundAdapter.pubChemCID, compoundAdapter)


        compoundAdapter = new MockCompoundAdapter()
        compoundAdapter.id = 16760208
        compoundAdapter.name = "(2R,3S,5R)-5-(6-amino-2-chloropurin-9-yl)-4-fluoro-2-(hydroxymethyl)oxolan-3-ol"
        compoundAdapter.structureSMILES = "NC1=NC(Cl)=NC2=C1N=CN2[C@@H]1O[C@H](CO)[C@H](O)C1F"
        mockCompoundAdapterMap.put(compoundAdapter.pubChemCID, compoundAdapter)



        compoundAdapter = new MockCompoundAdapter()
        compoundAdapter.id = 354624
        compoundAdapter.structureSMILES = "NC1=NC(Cl)=NC2=C1N=CN2C1OC(CO)C(O)C1F"
        compoundAdapter.name = "5-(6-amino-2-chloropurin-9-yl)-4-fluoro-2-(hydroxymethyl)oxolan-3-ol"
        mockCompoundAdapterMap.put(compoundAdapter.pubChemCID, compoundAdapter)
    }

    private void constructMockExperiment() {
        MockExperiment mockedExperiment = new MockExperiment()
        mockedExperiment.bardExptId = 1904
        mockedExperiment.capExptId = 1904
        mockedExperiment.bardAssayId = 1904
        mockedExperiment.capAssayId = 1904
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
        mockExperimentMap.put(mockedExperiment.bardExptId, mockedExperiment)


        mockedExperiment = new MockExperiment()
        mockedExperiment.bardExptId = 2757
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
        mockExperimentMap.put(mockedExperiment.bardExptId, mockedExperiment)


        mockedExperiment = new MockExperiment()
        mockedExperiment.bardExptId = 3470
        mockedExperiment.bardAssayId = 37
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
        mockExperimentMap.put(mockedExperiment.bardExptId, mockedExperiment)
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
    Map findExperimentDataById(Long experimentId, Integer top, Integer skip, List<FilterTypes> filterTypes) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Map findAssaysByCapIds(List<Long> capAssayIds, Integer top, Integer skip, List<SearchFilter> searchFilters) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Map findProjectsByCapIds(List<Long> capProjectIds, Integer top, Integer skip, List<SearchFilter> searchFilters) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Map searchCompoundsByCids(List<Long> cids, Integer top, Integer skip, List<SearchFilter> searchFilters) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    CompoundAdapter findProbe(String mlNumber) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    TableModel createCompoundBioActivitySummaryDataTable(Long compoundId,
                                                         GroupByTypes groupTypes,
                                                         List<FilterTypes> filterTypes,
                                                         List<SearchFilter> appliedSearchFilters,
                                                         SearchParams searchParams) { return null }

    @Override
    TableModel showExperimentalData(Long experimentId,
                                    GroupByTypes groupTypes,
                                    List<FilterTypes> filterTypes,
                                    SearchParams searchParams) {
        return null
    }

    @Override
    Map getPathsForAssayFormat(String endNode) {
        return null
    }

    @Override
    Map getPathsForAssayType(String endNode) {
        return null
    }
}



