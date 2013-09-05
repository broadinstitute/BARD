package molspreadsheet

import org.apache.log4j.Level
import org.apache.log4j.Logger

/**
 * Created with IntelliJ IDEA.
 * User: balexand
 * Date: 6/27/13
 * Time: 3:03 PM
 * To change this template use File | Settings | File Templates.
 */

public class LinkedVisHierData {
    private final String comma = ','
    private final String openGroup = '['
    private final String closeGroup = ']'
    private final String openObject = '{'
    private final String closeObject = '}'
    private final String colonUnit = ' : '
    private final String endOfLine = '\n'
    private final String tinySpaceUnit = ' '
    private final String shortSpaceUnit = '     '
    private final String mediumSpaceUnit = '      '
    private final String longSpaceUnit = '       '

    private String section1  = null
    private String section2  = null
    private String section3  = null
    private String section4  = null

    public String  externallyProvidedBiologicalProcessTree =  null
    public String externallyProvidedAssayFormatTree =  null
    public String externallyProvidedProteinTargetTree =  null
    public String externallyProvidedAssayTypeTree =  null

    private final String addQuotes( String incomingString )  {
        return  "\"${incomingString}\""
    }



    static Logger log
    static {
        this.log = Logger.getLogger(RingManagerService.class)
        log.setLevel(Level.ERROR)
    }



    private final void  indexUniquenessCheck (int proposedNewIndex,List accumulatingIndex, String sectionName) {
        if  (accumulatingIndex.contains(proposedNewIndex) ) {
            log.error("Catastrophic error.  proposedNewIndex=${proposedNewIndex}, accumulatingIndex=${accumulatingIndex}, sectionName=${sectionName}")
            throw Exception (' Stopped the show. Duplicated index = ${} section ${sectionName}')
        }
    }

    private final String  proteinTargetTree = """
 [{"name":"/", "children": [
        {"name":"signaling molecule", "assays": [0] },
        {"name":"enzyme modulator", "assays": [2,4], "children": [
            {"name":"G-protein modulator", "assays": [7]},
            {"name":"G-protein", "assays": [5], "children": [
                {"name":"heterotrimeric G-protein", "assays": [1]},
                {"name":"small GTPase", "assays": [3,6]}
            ]}
        ]},
        {"name":"transporter", "assays": [], "children": [
            {"name":"ATP-binding cassette", "assays": [12,13]},
            {"name":"ion channel", "assays": [11], "children": [
                {"name":"anion channel", "assays": [10,14]},
                {"name":"voltage-gated ion channel", "assays": [9], "children": [
                    {"name":"voltage-gated potassium channel", "assays": [8,15]}
                ]}
            ]}
        ]}
]}]"""



    private final String  assayFormatTree = """
 [{"name":"/", "children": [
        {"name":"cell-based format", "assays": [0,2,4,6,10, 11, 12, 13, 14] },
        {"name":"cell-free format", "assays": [], "children": [
            {"name":"whole-cell lysate format", "assays": [3]}
        ]},
        {"name":"biochemical format", "assays": [5, 7, 9], "children": [
            {"name":"protein format", "assays": [], "children": [
                {"name":"single protein format", "assays": [1, 8, 15]}
            ]}
        ]}
]}]"""





    public class CategorySection {
        List accumulatingIndex = []

        private final String individualCategorySection(int categoryIndex,
                                                       String categoryName,
                                                       String categoryDescription,
                                                       String categoryIdentifier) {
            indexUniquenessCheck (categoryIndex,accumulatingIndex,'Category1')
            StringBuilder stringBuilder = new StringBuilder()
            stringBuilder << openObject
            stringBuilder << shortSpaceUnit << addQuotes('CatIdx') << colonUnit << categoryIndex << comma << endOfLine
            stringBuilder << mediumSpaceUnit << addQuotes('CatName') << colonUnit << addQuotes(categoryName) << comma  << endOfLine
            stringBuilder << mediumSpaceUnit << addQuotes('CatDescr') << colonUnit << addQuotes(categoryDescription) << comma  << endOfLine
            stringBuilder << mediumSpaceUnit << addQuotes('CatIdent') << colonUnit << addQuotes(categoryIdentifier)  << endOfLine
            stringBuilder << closeObject
            return  stringBuilder.toString()
        }


        public final String writeCategorySection() {
            StringBuilder stringBuilder = new StringBuilder()
            stringBuilder << openGroup << endOfLine
            stringBuilder <<  individualCategorySection (0,'Biological process','GO Biological process','GO_biological_process_term') <<
                    comma << endOfLine
            stringBuilder <<  individualCategorySection (1,'Assay format','Bard assay format','assay_format') <<
                    comma << endOfLine
            stringBuilder <<  individualCategorySection (2,'Protein target','Panther protein target','assay_type')  <<
                    comma <<  endOfLine
            stringBuilder <<  individualCategorySection (3,'Assay type','Bard assay format','protein_target')
            stringBuilder << closeGroup
            return stringBuilder.toString()
        }

    }

    public class HierarchySection {

        private final String individualHierarchySection(int categoryReference,
                                                        String hierarchyType,
                                                        String structure) {
            StringBuilder stringBuilder = new StringBuilder()
            stringBuilder << openObject
            stringBuilder << shortSpaceUnit << addQuotes('CatRef') << colonUnit << categoryReference << comma << endOfLine
            stringBuilder << mediumSpaceUnit << addQuotes('HierType') << colonUnit << addQuotes(hierarchyType) << comma << endOfLine
            stringBuilder << mediumSpaceUnit << addQuotes('Structure') << colonUnit << structure << endOfLine
            stringBuilder << closeObject
            return stringBuilder.toString()
        }




        public final String writeHierarchySection() {
            StringBuilder stringBuilder = new StringBuilder()
            stringBuilder << openGroup << endOfLine
            if (externallyProvidedBiologicalProcessTree != null) {
                stringBuilder << individualHierarchySection(0, 'Graph', '{'+addQuotes('struct')+colonUnit+externallyProvidedBiologicalProcessTree.toString() +'}') <<
                        comma << endOfLine
            } else {
                stringBuilder << individualHierarchySection(0, 'Graph', '{}') <<
                        comma << endOfLine
            }
            if (externallyProvidedAssayFormatTree != null) {
                stringBuilder << individualHierarchySection(1, 'Tree', '{'+addQuotes('struct')+colonUnit+externallyProvidedAssayFormatTree.toString() +'}') <<
                        comma << endOfLine
            } else {
                stringBuilder << individualHierarchySection(1, 'Tree', '{}') <<
                        comma << endOfLine
                // We don't need these test data right now
//                stringBuilder << individualHierarchySection(1, 'Tree', '{'+addQuotes('struct')+colonUnit+assayFormatTree.toString() +'}') <<
//                        comma << endOfLine
            }
            if (externallyProvidedProteinTargetTree != null) {
                stringBuilder << individualHierarchySection(2, 'Tree', '{'+addQuotes('struct')+colonUnit+externallyProvidedProteinTargetTree.toString() +'}') <<
                        comma << endOfLine
            } else {
                stringBuilder << individualHierarchySection(2, 'Tree', '{'+addQuotes('struct')+colonUnit+proteinTargetTree.toString() +'}') <<
                        comma << endOfLine
            }
            if (externallyProvidedAssayTypeTree != null) {
                stringBuilder << individualHierarchySection(3, 'Tree', '{'+addQuotes('struct')+colonUnit+externallyProvidedAssayTypeTree.toString() +'}')
            } else {
                stringBuilder << individualHierarchySection(3, 'Tree', '{}')
            }

            stringBuilder << closeGroup
            return stringBuilder.toString()
        }
    }





    public class AssaysSection {
        List accumulatingIndex1 = []
        List accumulatingIndex2 = []

        private final String individualAssaySection( int assayIndex,
                                                     String assayName,
                                                     int assayActives,
                                                     int assayInactives,
                                                     int assayId ) {
            indexUniquenessCheck (assayIndex,accumulatingIndex1,'Assay index 1')
            indexUniquenessCheck (assayIndex,accumulatingIndex2,'Assay ID')

            StringBuilder stringBuilder = new StringBuilder()
            stringBuilder << openObject
            stringBuilder << shortSpaceUnit << addQuotes('AssayIdx') << colonUnit << assayIndex << comma << endOfLine
            stringBuilder << mediumSpaceUnit << addQuotes('AssayName') << colonUnit << addQuotes(assayName) << comma << endOfLine
            stringBuilder << mediumSpaceUnit << addQuotes('AssayAc') << colonUnit << assayActives << comma << endOfLine
            stringBuilder << mediumSpaceUnit << addQuotes('AssayIn') << colonUnit << assayInactives << comma << endOfLine
            stringBuilder << mediumSpaceUnit << addQuotes('AssayId') << colonUnit << assayId << endOfLine
            stringBuilder << closeObject
            return stringBuilder.toString()
        }




        public final String writeAssaySection() {
            StringBuilder stringBuilder = new StringBuilder()
            stringBuilder << openGroup << endOfLine
            stringBuilder << individualAssaySection(0, 'Radiotracer Incision Assay (RIA) for Inhibitors of Human Apurinic/apyrimidinic Endonuclease 1 (APE1)', 1, 0, 1017) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(1, 'Inhibitors of Bloom\'s syndrome helicase: Efflux Ratio Profiling Assay', 0, 1, 1730) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(2, 'Inhibitors of Bloom\'s syndrome helicase: Aqueous Profiling Assay', 1, 0, 1732) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(3, 'Inhibitors of Bloom\'s syndrome helicase: Metabolic Stability Profiling', 1, 0, 1733) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(4, 'Inhibitors of APE1: Caco-2 Cell Permeability Profiling', 0, 1, 1735) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(5, 'Inhibitors of APE1: Mouse Plasma Stability Profiling', 0, 1, 1612) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(6, 'Inhibitors of APE1: Metabolic Stability Profiling', 0, 1, 1651) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(7, 'Inhibitors of APE1: Aqueous Solubility Profiling', 1, 0, 1604) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(8, 'qHTS Assay for Inhibitors of Bloom\'s syndrome helicase (BLM)', 0, 1, 2483) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(9, 'qHTS Assay for Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)', 0, 1, 2472) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(10, 'qHTS FP-Based Assay for Inhibitors of the Human Apurinic/apyrimidinic Endonuclease 1 (APE1)', 0, 1, 2623) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(11, 'qHTS Assay for Inhibitors of BRCT-Phosphoprotein Interaction (Green Fluorophore)', 0, 1, 3402) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(12, 'qHTS Assay for Inhibitors of BRCT-Phosphoprotein Interaction (Red Fluorophore)', 0, 1, 3418) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(13, 'Homologous recombination_Rad 51_dose response_2', 0, 1, 3594) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(14, 'Homologous recombination - Rad 51', 0, 1, 3874) <<
                    comma << endOfLine
            stringBuilder << individualAssaySection(15, 'Late stage assay provider results from the probe development effort to identify inhibitors of Wee1 degradation: luminescence-based cell-based assay to identify inhibitors of Wee1 degradation', 0, 1, 537)

            stringBuilder << closeGroup
            return stringBuilder.toString()
        }
    }




    public class AssayCross {

        private final String individualAssayCross(int categoryIndex,
                                                  String biologicalProcess,
                                                  String assayFormat,
                                                  String assayType,
                                                  String proteinTarget ) {

            StringBuilder stringBuilder = new StringBuilder()
            stringBuilder << openObject
            stringBuilder << shortSpaceUnit << addQuotes('AssayRef') << colonUnit << categoryIndex << comma << endOfLine
            stringBuilder << mediumSpaceUnit << addQuotes('data') << colonUnit << openObject << endOfLine
            stringBuilder << longSpaceUnit << addQuotes('0') << colonUnit << addQuotes(biologicalProcess) << comma  << endOfLine
            stringBuilder << longSpaceUnit << addQuotes('1') << colonUnit << addQuotes(assayFormat) << comma  << endOfLine
            stringBuilder << longSpaceUnit << addQuotes('2') << colonUnit << addQuotes(assayType) << comma  << endOfLine
            stringBuilder << longSpaceUnit << addQuotes('3') << colonUnit << addQuotes(proteinTarget)  << endOfLine
            stringBuilder << mediumSpaceUnit << closeObject << endOfLine
            stringBuilder << closeObject
            return  stringBuilder.toString()
        }


        public final String writeAssayCrossSection() {
            StringBuilder stringBuilder = new StringBuilder()
            stringBuilder << openGroup << endOfLine
            stringBuilder <<  individualAssayCross (0,'none','cell-based format','protein-small molecule interaction assay','signaling molecule') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (2,'regulation of gene expression','cell-based format','protein expression assay','enzyme modulator') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (15,'bontoxilysin activity','single protein format','direct enzyme activity assay','transporter') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (1,'plectin-1','single protein format','protein-protein interaction assay','enzyme modulator') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (3,'none','whole-cell lysate format','none','enzyme modulator') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (4,'regulation of gene expression','cell-based format','protein expression assay','enzyme modulator') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (6,'bontoxilysin activity','cell-based format','functional assay','enzyme modulator') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (5,'none','biochemical format','fluorescence interference assay','enzyme modulator') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (7,'plectin-1','biochemical format','protein-protein interaction assay','enzyme modulator') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (8,'plectin-1','single protein format','protein-protein interaction assay','transporter') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (9,'bontoxilysin activity','biochemical format','direct enzyme activity assay','transporter') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (10,'kinase activity','cell-based format','protein-small molecule interaction assay','transporter') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (11,'none','cell-based format','reporter-gene assay','transporter') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (12,'cell death','cell-based format','transporter assay','transporter') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (13,'regulation of gene expression','cell-based format','protein expression assay','transporter') <<
                    comma << endOfLine
            stringBuilder <<  individualAssayCross (14,'immunological synapse formation','cell-based format','gene-expression assay','transporter')
            stringBuilder << closeGroup
            return stringBuilder.toString()
        }

    }





    public String createCategorySection(){
        CategorySection categorySection = new CategorySection()
        categorySection.writeCategorySection()
    }

    public String createHierarchySection(){
        HierarchySection hierarchySection = new HierarchySection()
        hierarchySection.writeHierarchySection()
    }


    public String createAssaysSection(){
        AssaysSection assaysSection = new AssaysSection()
        assaysSection.writeAssaySection()
    }



    public String createAssayCrossSection(){
        AssayCross assayCross = new AssayCross()
        assayCross.writeAssayCrossSection()
    }



    public final String createCombinedListing() {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder << openObject << endOfLine
        stringBuilder << addQuotes('Category') << colonUnit << (section1 ?: createCategorySection()) << comma << endOfLine
        stringBuilder << addQuotes('Hierarchy') << colonUnit << (section2 ?:  createHierarchySection()) << comma << endOfLine
        stringBuilder << addQuotes('Assays') << colonUnit <<  (section3 ?: createAssaysSection()) << comma << endOfLine
        stringBuilder << addQuotes('AssayCross') << colonUnit <<  (section4 ?: createAssayCrossSection()) << endOfLine
        stringBuilder << closeObject
        return stringBuilder.toString()
    }



    public LinkedVisHierData(String section1,String section2,String section3,String section4) {
        this.section1 = section1
        this.section2 = section2
        this.section3 = section3
        this.section4 = section4
    }
    public LinkedVisHierData() {
        this.section1 = null
        this.section2 = null
        this.section3 = null
        this.section4 = null
    }

}
