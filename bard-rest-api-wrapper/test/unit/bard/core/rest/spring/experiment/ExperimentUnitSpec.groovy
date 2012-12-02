package bard.core.rest.spring.experiment

import bard.core.rest.spring.experiment.ExperimentSearch
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ExperimentUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    final String EXPERIMENT_JSON_FROM_MULTIPLE_IDS = '''
[
    {
        "exptId": 2722,
        "assayId": 2722,
        "pubchemAid": 449740,
        "category": 2,
        "type": 2,
        "summary": 0,
        "assays": 0,
        "classification": 2,
        "substances": 6,
        "compounds": 6,
        "name": "Salmonella typhi: Polymyxin resistance assay (3)",
        "description": "Southern Research's Specialized Biocontainment Screening Center (SRSBSC)\\nSouthern Research Institute (Birmingham, Alabama)\\nNIH Molecular Libraries Probe Centers Network (MLPCN)\\nAssay Provider: Dr. Jason Harris, Massachusetts General Hospital \\n\\nAssay Rationale and Summary: Salmonella is a genus of rod-shaped Gram-negative enterobacteria that causes typhoid fever, paratyphoid fever, and foodborne illness. Salmonella enterica, serovar Typhimurium, is a leading cause of human gastroenteritis (acute intestinal inflammation), and is used as a mouse model of human typhoid fever. Serovar Typhi is the cause of the human disease typhoid fever. Every year, approximately 40,000 cases of salmonellosis are reported in the United States. Because many milder cases are not diagnosed or reported, the actual number of infections may be thirty or more times greater. The most recent outbreak in the US occurred in April, 2008. Young children, the elderly, and the immunocompromised are the most likely to develop severe infections. The rate of diagnosed infections in children less than five years old is about five times higher than the rate in all other persons. The incidence of non-typhoid salmonellosis is increasing worldwide, causing millions of infections and many deaths in the human population each year. S. typhi infections occur worldwide but primarily in developing nations where sanitary conditions are poor. Early antibiotic therapy has transformed a previously life-threatening illness of several weeks' duration with an overall mortality rate approaching 20% into a short-term febrile illness with negligible mortality. However, case fatality rates of 10-50% have been reported from endemic countries when diagnosis is delayed. Typhoid fevers are endemic in Asia, Africa, Latin America, and the Caribbean, affects 13-17 million people yearly, and kills an estimated 600,000.\\n\\nThe PhoP regulon is a major regulator of virulence in Salmonella that also controls the adaptation to Mg2+-limiting environments. The PhoP system enables Salmonella to determine its presence in an intracellular or extracellular environment, and to promote the expression of genes required for survival within or entry into host cells, respectively. The DNA sequence for the PhoP locus indicates that it is composed of two genes present in an operon, termed phoP and phoQ. The gene products are members of bacterial two-component transcriptional regulators that respond to environmental stimuli, and regulate the expression of genes involved in virulence and macrophage survival of S. enterica. \\n\\nDuring the past year the assay provider's group has constructed and evaluated multiple PhoP-activated promoter-reporter fusions and compared these results with the relative abundance of gene transcripts in wild-type serovar Typhi. They also evaluated PhoP-independent as well as PhoP-repressed promoter-reporter constructs in PhoP-non-inducing versus inducing conditions for each of the promoters. He has used these reporters in Salmonella serovar Typhi to create a high-throughput screening (HTS) assay for identifying compounds that specifically inhibit PhoP induction. To this end, our team has developed a 1536-well high-throughput screen suitable for the discovery of small molecule inhibitors of the PhoP virulence regulon of S. enterica which may lead to the identification of novel strategies to inhibit the intracellular persistence of bacterial infection. The proposed primary assays, counter screens, and secondary assays, which utilize quantitative, established techniques, will allow identification of hits, confirmation of target specificity, and the prioritization of assay chemical probes. \\n\\nThis secondary assay in Salmonella typhi was used to determine PhoP specificity by measuring how the test compounds changed the susceptibility of wild-type and PhoP(-) S.typhi to polymyxin, in PhoP inducing (10 uM Mg2+) conditions.  Matrix plates were dosed in the x-axis (columns 1-8) with polymyxin 50 to 0 ug/ml), and separate test compounds in the y-axis (rows 1-8) (150-0 uM).  After 24 hr of bacterial growth, the IC50 of polymyxin was determined in the presence of different doses of the test compounds. Controls included polymyxin alone to simulate compounds that had no effect on PhoP, and ciprofloxin, to simulate compounds that had an effect but were PhoP independent.",
        "source": null,
        "grantNo": null,
        "deposited": null,
        "updated": null,
        "hasProbe": false,
        "projectIdList": [
            103
        ],
        "resourcePath": "/experiments/2722"
    },
    {
        "exptId": 644,
        "assayId": 644,
        "pubchemAid": 1783,
        "category": 2,
        "type": 1,
        "summary": 0,
        "assays": 0,
        "classification": 1,
        "substances": 1658,
        "compounds": 1658,
        "name": "Counter screen for biotinylated proteins binding to avidin beads",
        "description": "University of New Mexico Assay Overview:\\nAssay Support: NIH R21NS057014\\nHTS to identify small molecule regulators of RGS family protein interactions \\nPI: Richard Neubig, Ph.D.\\nAssay Implementation: Yang Wu Ph.D., Mark Haynes Ph.D., Anna Waller Ph.D., Mark Carter M.S.\\nTarget Team Leader for the Center: Larry Sklar, Ph.D., (lsklar@salud.unm.edu)\\n\\nAssay Background and Significance:\\n\\nThe multiplex screen of RGS (AID:1415, 1423, 1439, 1440, 1441) is a bead-based assay with biotinylated proteins pre-incubated with avidin beads, prior to binding measurements of  fluorescent alpha-o subunit of guanine-nucleotide binding protein. Potential false positives from the above mentioned assay would be compounds that interrupt the binding of biotin to avidin. Evaluation of potential blocking compounds were made with fluorescein biotin binding to the same avidin beads used in the multiplex assay. ",
        "source": null,
        "grantNo": null,
        "deposited": null,
        "updated": null,
        "hasProbe": false,
        "projectIdList": [
            22
        ],
        "resourcePath": "/experiments/644"
    }
]
'''

    void setup() {

    }

    void tearDown() {
        // Tear down logic here
    }

    void "test experiment"() {
        when:
        List<ExperimentSearch> experiments = objectMapper.readValue(EXPERIMENT_JSON_FROM_MULTIPLE_IDS, List.class)
        then:
        assert experiments
        assert experiments.size() == 2
    }

}

