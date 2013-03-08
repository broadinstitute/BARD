import maas.ElementHandlerService
import org.springframework.transaction.support.DefaultTransactionStatus
import bard.db.dictionary.Element

/**
 * Run the script to add terms to ontology (Element table), if there is ELEMENT_HIERARCHY relationship, load it also
 *
 *
 * Created with IntelliJ IDEA.
 * User: xiaorong
 * Date: 3/6/13
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */


Map elementAndDescription = [
        // before 3-2013
        "percent parasitemia":"percent content or parasites in the blood",
        "hematocrit":"volume percentage of red blood cells. aka packed cell volume or erythrocyte volume fraction",
        "PerkinElmer AlphaScreen reagent":null,
        "green coffee bean":"unroasted coffee bean",
        "ABI Prism 7900HT":"sequence detection system instrument designed for automated, high-throughput detection of fluorescent PCR-related chemistries",
        "Tecan Spectra Mini Reader":null,
        "PerkinElmer AlphaScreen SureFire ERK reagent":null,
        "Bacillus anthracis":null,
        "Bacillus subtilis":null,
        "Biorad ChemiDoc Plus Imager":null,
        "Giardia lamblia":null,
        "Cercopithecus aethiops":null,
        "Microcal VP-ITC":null,
        "Packard Cobra Gamma Counter":null,
        "Methanococcus maripaludis":null,
        "Accuri C6 flow cytometer":null,
        "Marburg marburgvirus":null,
        "Lassa virus":null,
        "Avian infectious bronchitis virus":null,
        "Cricetulus griseus":"Chinese Hamster",
        "Tecan Freedom Evo 150":null,
        "Streptococcus pyogenes":"spherical gram-positive bacteria",
        "BMG Pherastar":null,
        "Alpco insulin ELISA assay kit":null,
        //jasonR starts here
        "SpectraMax M5 Multi-Mode Microplate Reader":null,
        "Perkin Elmer LabChip EZReader":null,
        "HT1080":null,
        "cytometer":null,
        "SDS-PAGE":null,
        "CCD fluorometer":"Charge-coupled device fluorometric camera",
        "BD Accuri C6 Flow Cytometer":null,
        "GE Typhoon laser scanner":null,
        "Sybr Green PCR Reagent Kit":null,
        "AB 7500 rtPCR System":"Applied Biosystems model 7500 real time PCR system",
        "LC-MS/MS":"Liquid Chromatography, tandem-mass spectrometry",
        "mass spectrometer":null,
        "Thermo Scientific Orbitrap Velos":null,
        "MDA-MB-435":null,
        "Tecan Spectrafluor Plus":"Tecan Spectrafluor Plus plate reader",
        "PerkinElmer AlphaScreen Bead Kit":"AlphaScreen protein-protein interaction assay",
        "Thermo Scientific LTQ-Orbitrap":null,
        "Biotek Synergy 2 plate reader":"Bio-Tek Synergy 2 Multi-Mode Microplate Reader",
        "ThermoFluor 384 System":null,
        "Roche LightCycler480Ž":"Roche Applied Science: LightCycler 480 System",
        "LJL Analyst":"Molecular Devices LJL Analyst HT Microplate Reader",
        "Agilent Cary Eclipse Fluorescence Spectrophotometer":null,
        "Streptomyces avidinii":null,
        "ABI PRISM 7700 sequence detector":null,
        "Beckman LS 6000SC counter":null,
        "Huh7":"Huh7 cell line",
        "CytoTox-ONE Homogenous Membrane Integrity Assay":null,
        // 3-2013
        'science officer' : '',
]

// element parent
Map elementParent = [
        'science officer' : 555  // id = 555   'project information'
]

ElementHandlerService elementHandlerService = new ElementHandlerService()
Element.withTransaction { DefaultTransactionStatus status ->
    elementHandlerService.addMissingElement("xiaorong-maas", elementAndDescription, elementParent)
}

// TODO: as we decided to load this to person table
//String fileWithUniqueName = 'test/exampleData/maas/missingNameUniq.txt'
//Element.withTransaction { DefaultTransactionStatus status ->
//    elementHandlerService.addMissingName(fileWithUniqueName)
//}
