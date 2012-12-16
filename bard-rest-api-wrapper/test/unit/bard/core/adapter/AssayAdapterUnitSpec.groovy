package bard.core.adapter

import bard.core.interfaces.AssayCategory
import bard.core.interfaces.AssayRole
import bard.core.interfaces.AssayType
import bard.core.rest.spring.assays.Assay
import bard.core.rest.spring.util.NameDescription
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import bard.core.rest.spring.assays.ExpandedAssay

@Unroll
class AssayAdapterUnitSpec extends Specification {
    @Shared
    ObjectMapper objectMapper = new ObjectMapper()

    public static final String EXPANDED_ASSAY = '''
   {
       "aid":1749,
       "bardAssayId":600,
       "capAssayId":4098,
       "category":1,
       "type":0,
       "summary":0,
       "assays":0,
       "classification":0,
       "name":"SAR analysis of Antagonists of IAP-family anti-apoptotic proteins",
       "source":"Burnham Center for Chemical Genomics",
       "grantNo":null,
       "deposited":null,
       "updated":null,
       "documents":[
          {
             "title":"Cytochrome c: can't live with it--can't live without it.",
             "doi":"null",
             "abs":"null",
             "pubmedId":9393848,
             "resourcePath":"/documents/9393848"
          },
          {
             "title":"Small-molecule antagonists of apoptosis suppressor XIAP exhibit broad antitumor activity.",
             "doi":"null",
             "abs":"Apoptosis resistance commonly occurs in cancers, preventing activation of Caspase family cell death proteases. XIAP is an endogenous inhibitor of Caspases overexpressed in many cancers. We developed an enzyme derepression assay, based on overcoming XIAP-mediated suppression of Caspase-3, and screened mixture-based combinatorial chemical libraries for compounds that reversed XIAP-mediated inhibition of Caspase-3, identifying a class of polyphenylureas with XIAP-inhibitory activity. These compounds, but not inactive structural analogs, stimulated increases in Caspase activity, directly induced apoptosis of many types of tumor cell lines in culture, and sensitized cancer cells to chemotherapeutic drugs. Active compounds also suppressed growth of established tumors in xenograft models in mice, while displaying little toxicity to normal tissues. These findings validate IAPs as targets for cancer drug discovery.",
             "pubmedId":14749124,
             "resourcePath":"/documents/14749124"
          }
       ],
       "targets":[
          {
             "acc":"P98170",
             "name":"E3 ubiquitin-protein ligase XIAP",
             "description":null,
             "status":"Reviewed",
             "geneId":331,
             "taxId":9606,
             "resourcePath":"/targets/accession/P98170"
          }
       ],
       "kegg_disease_names":[
          "Other well-defined immunodeficiency syndromes, including the following eight diseases:Wiskott-Aldrich syndrome",
          "DiGeorge syndrome",
          "Hyper-IgE syndrome",
          "X-linked lymphoproliferative syndrome",
          "Lymphoproliferative syndrome, EBV-associated, autosomal, 1",
          "Immunodeficiency, Polyendocrinopathy, Enteropathy, X-linked Syndrome (IPEX)",
          "Cartilage-Hair Hypoplasia",
          "Autoimmune polyendocrinopathy-candidiasis-ectodermal dystrophy (APECED)"
       ],
       "kegg_disease_cat":[
          "Primary immunodeficiency"
       ],
       "resourcePath":"/assays/600",
       "description":"Data Source: Sanford-Burnham Center for Chemical Genomics (SBCCG)\\nSource Affiliation: Sanford-Burnham Medical Research Institute (SBMRI, San Diego, CA)\\nNetwork: NIH Molecular Libraries Screening Centers Network (MLSCN)\\nGrant Proposal Number: MH081277-01\\nAssay Provider: John C. Reed, Sanford-Burnham Medical Research Institute, San Diego, CA\\n\\nThis XIAP dose response assay is developed and performed to confirm hits originally identified in the XIAP HTS binding assay (AID 1018) and to study the structure-activity relationship on analogs of the confirmed hits. Compounds are either acquired from commercial sources or synthesized internally.  The assay was performed in the assay providers' laboratory.\\n\\nApoptosis plays an essential role in many aspects of normal development and physiology, becoming dysregulated in myriad diseases characterized by insufficient or excessive cell death. Caspases are intracellular proteases that are suppressed by Inhibitor of Apoptosis Proteins (IAPs), a family of evolutionarily conserved anti-apoptotic proteins. Proteins released from mitochondria (SMAC and HtrA2) can competitively displace IAPs from the Caspases, thus helping to drive apoptosis. It has been shown that only a few residues at the N-terminus of activated SMAC protein (4'mer) are sufficient to affect the release of IAPs from Caspases. Thus, it is plausible to identify chemical compounds that mimic the effect of SMAC in antagonizing IAPs by causing them to release Caspases. Non-peptidyl chemical inhibitors would have advantages over SMAC peptides, in terms of cell permeability, stability, and in vivo pharmacology. Thus, the goal of this project is to generate small-molecule chemical probe compounds that mimic the effects of SMAC peptides, inhibiting the function of IAPs.\\n\\nBasis of the assay is disruption of fluorescence polarization resulting from binding of a his-tagged-BIR1-BIR2 (bacoloviral IAP repeat, \\"Bir1/2\\") domain protein derived from two of the three conserved caspase binding \\"BIR\\" domains of XIAP to a rhodamine tagged 7-mer N-terminal SMAC peptide.",
       "protocol":"BIR1/2 assay materials:\\n1) Bir1/2 protein was provided by Prof. John C. Reed (Sanford-Burnham Medical Research Institute, San Diego, CA) and SMAC-rhodamine peptide (AVPIAQK-rhodamine) was provided by Richard Houghten (Torrey Pines Institute for Molecular Studies, San Diego, CA).\\n2) Assay buffer:  33.35 Hepes @ pH 7.5, 1.33 mM TCEP, 0.00667 % Tween 20, 1.334 uM BIR12 and 0.0267 uM SMAC-rhodamine.  \\n\\nBIR1/2 protocol:\\n1) 15 ul of assay buffer was added to a Molecular Devices black 96 well plate (number 42-000-0117) in columns 1 through 10.\\n2) Five ul of compounds were added to the plate in columns 1 through 10 with a 16 point curve generated by 2 fold dilutions of each compound.  Final compound concentration in the assay ranged from 100 uM to 0.00612 uM via two fold dilutions starting with 400 uM maximum concentration.\\n3) Positive controls without protein were in column 12.  Negative controls with protein were in column 11.\\n4) The AVPF peptide was present with the same concentration range as the compounds on each plate as an internal control.\\n5) Final concentrations of the components were 25 mM Hepes @ pH 7.5/ 1 mm TCEP/0.005% Tween 20/20 nM  rhodamine-SMAC/1 uM BIR1/2.  Compound was diluted in water starting at 400 uM and diluted serially by a factor of 2.  Final concentrations of compounds were one fourth the concentration of the initial compound curve.\\n6) Plates were then centrifuged at 1000 rpm for 1 minute before reading on an LJL Analyst using an excitation filter of 530 nm with emission at 580 nm and a dichroic mirror at 565 nm.\\n7) Resultant data was fit to a nonlinear curve in Prism and IC50 values and Hill coefficients were determined.  BIR1/2 assays generally had a Hill Coefficient around 1 for each compound. ",
       "comments":"Compounds with an IC50 < 100 uM are considered \\"active.\\"\\n\\nTo simplify the distinction between the inactives of the primary screen and of the confirmatory screening stage, the Tiered Activity Scoring System was developed and implemented. Its utilization for the Bir1/2 assay is described below. \\n\\nActivity Scoring\\nActivity scoring rules were devised to take into consideration compound efficacy, its potential interference with the assay and the screening stage that the data was obtained. Details of the Scoring System will be published elsewhere. Briefly, the outline of the scoring system utilized for the Bir1/2 assay is as follows: \\n\\n1) First tier (0-40 range) is reserved for primary screening data-and is not applicable in this assay.\\n\\n2) Second tier (41-80 range) is reserved for dose-response confirmation data and is not applicable in this assay.\\n\\n3) Third tier (81-100 range) is reserved for resynthesized true positives and their analogues\\na. Inactive compounds of the confirmatory stage are assigned a score value equal 81. \\nb. The score is linearly correlated with a compound's activatory potency and, in addition, provides a measure of the likelihood that the compound is not an artifact based on the available information.\\nc. The Hill coefficient is taken as a measure of compound behavior in the assay via an additional scaling factor QC: \\nQC = 2.6*[exp(-0.5*nH^2) - exp(-1.5*nH^2)]\\nThis empirical factor prorates the likelihood of target-specific compound effect vs. its non-specific behavior in the assay. This factor is based on expectation that a compound with a single mode of action that achieved equilibrium in the XIAP assay demonstrates the Hill coefficient value of 1. Compounds deviating from that behavior are penalized proportionally to the degree of their deviation.\\nd. Summary equation that takes into account the items discussed above is \\nScore = 84 + 3*(pIC50 - 3)*QC, \\nwhere pIC50 is a negative log(10) of the IC50 value expressed in mole/L concentration units. This equation results in the Score values above 90 for compounds that demonstrate high potency and predictable behavior. Compounds that are inactive in the assay or whose concentration-dependent behavior are likely to be an artifact of that assay will generally have lower Score values. \\n\\nA score of 84 is given to active compounds selected from plates:\\na) That do not have a Hill coefficient associated with them and have a qualifier of < or >.\\nor\\nb)  The value of + 3*(pIC50-3)*QC, is < 0.500\\n\\nActive compounds will have a score >= 84."
   }
   '''
    public static final String ASSAY = '''
    {
           "aid": 2162,
           "bardAssayId": 17,
           "capAssayId": 4406,
           "category": 1,
           "type": 0,
           "summary": 0,
           "assays": 0,
           "classification": 0,
           "name": "Confirmation qHTS Assay for Inhibitors of 12-hLO (12-human lipoxygenase)",
           "source": "NCGC",
           "grantNo": null,
           "deposited": null,
           "updated": null,
           "documents":
           [
               17826100,
               17869117,
               16500106,
               16864780
           ],
           "targets":
           [
               "P18054"
           ],
           "experiments":
           [
               17
           ],
           "projects":
           [
               147
           ],
           "kegg_disease_names":
           [
           ],
           "kegg_disease_cat":
           [
           ],
           "resourcePath": "/assays/17"
       }
       '''



    void "test Highlight"() {
        given:
        final Assay assay = new Assay()

        when:
        AssayAdapter assayAdapter = new AssayAdapter(assay)
        then:
        assert !assayAdapter.highlight

    }

    void "test getters"() {

        given:
        final Assay assay = objectMapper.readValue(ASSAY, Assay.class)
        Double score = 2
        NameDescription nameDescription = new NameDescription(description: "description", name: "name")
        when:
        AssayAdapter assayAdapter = new AssayAdapter(assay, score, nameDescription)

        then:
        assert assayAdapter.name == "Confirmation qHTS Assay for Inhibitors of 12-hLO (12-human lipoxygenase)"
        assert assayAdapter.getCapAssayId() == 4406
        assert assayAdapter.getBardAssayId() == 17
        assert assayAdapter.getId() == 17
        assert assayAdapter.aid == 2162
        assert assayAdapter.source == "NCGC"
        assert assayAdapter.keggAnnotations
        assert !assayAdapter.keggDiseaseCategories
        assert !assayAdapter.keggDiseaseNames
        assert !assayAdapter.getDescription()
        assert !assayAdapter.annotations
        assert assayAdapter.getRole() == AssayRole.Primary
        assert assayAdapter.getType() == AssayType.Other
        assert !assayAdapter.protocol
        assert !assayAdapter.comments
        assert assayAdapter.getCategory() == AssayCategory.MLSCN
        assert assayAdapter.score == score
        assert assayAdapter.matchingField.name == nameDescription.name
        assert assayAdapter.matchingField.description == nameDescription.description
        assert assayAdapter.highlight == "Score: 2.0 Matched Field: name"
        assert assayAdapter.documentIds
        assert assayAdapter.targetIds
        assert !assayAdapter.documents
        assert !assayAdapter.targets
    }
    void "test getters With Expanded Assay"() {

        given:
        final ExpandedAssay assay = objectMapper.readValue(EXPANDED_ASSAY, ExpandedAssay.class)
        Double score = 2
        NameDescription nameDescription = new NameDescription(description: "description", name: "name")
        when:
        AssayAdapter assayAdapter = new AssayAdapter(assay, score, nameDescription)
        then:
        assert assayAdapter.name == "SAR analysis of Antagonists of IAP-family anti-apoptotic proteins"
        assert assayAdapter.getCapAssayId() == 4098
        assert assayAdapter.getBardAssayId() == 600
        assert assayAdapter.getId() == 600
        assert assayAdapter.aid == 1749
        assert assayAdapter.source == "Burnham Center for Chemical Genomics"
        assert assayAdapter.keggAnnotations
        assert assayAdapter.keggDiseaseCategories
        assert assayAdapter.keggDiseaseNames
        assert assayAdapter.getDescription()
        assert !assayAdapter.annotations
        assert assayAdapter.getRole() == AssayRole.Primary
        assert assayAdapter.getType() == AssayType.Other
        assert assayAdapter.protocol
        assert assayAdapter.comments
        assert assayAdapter.getCategory() == AssayCategory.MLSCN
        assert assayAdapter.score == score
        assert assayAdapter.matchingField.name == nameDescription.name
        assert assayAdapter.matchingField.description == nameDescription.description
        assert assayAdapter.highlight == "Score: 2.0 Matched Field: name"
        assert assayAdapter.documentIds
        assert assayAdapter.targetIds
        assert assayAdapter.documents
        assert assayAdapter.targets
    }


}

