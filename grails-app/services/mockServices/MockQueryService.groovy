package mockServices

import bard.core.adapter.AssayAdapter
import bard.core.adapter.CompoundAdapter
import bard.core.adapter.ProjectAdapter
import bard.core.*
import bardqueryapi.IQueryService
import bardqueryapi.QueryHelperService
import bardqueryapi.SearchFilter

class MockQueryService implements IQueryService {
    QueryHelperService queryHelperService

    static final Map<Long, MockCompoundAdapter> mockCompoundAdapterMap =[:]

    static final Map<Long, MockAssayAdapter> mockAssayAdapterMap = [:]

    static final Map<Long, MockProjectAdapter> mockProjectAdapterMap = [:]

    static {
        constructMockCompoundAdapter()
        constructMockAssayAdapter()
        constructMockProjectAdapter()
    }
    Map findPromiscuityScoreForCID(final Long cid){
        return [scores: [20,30], status: 200, message: "Success"]

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
    Map structureSearch(final String smiles, final StructureSearchParams.Type structureSearchParamsType, final List<SearchFilter> searchFilters = [], final int top = 50, final int skip = 0) {
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
        return [projectAdapter: MockQueryService.mockProjectAdapterMap.get(projectId), experiments:[]]
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
        mockAssayAdapter.assay.category = AssayValues.AssayCategory.MLPCN
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

    public Long[] getPubChemSIDs() {
        return sids;
    }
}